package com.dev.mythiccore.reaction.reactions;

import com.dev.mythiccore.combat.Combat;
import com.dev.mythiccore.reaction.reaction_type.TriggerAuraReaction;
import com.dev.mythiccore.utils.ConfigLoader;
import com.dev.mythiccore.utils.StatCalculation;
import com.dev.mythiccore.utils.Utils;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.lib.api.stat.provider.StatProvider;
import io.lumine.mythic.lib.damage.DamagePacket;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Swirl extends TriggerAuraReaction {
    private final String aura;

    public Swirl(String id, ConfigurationSection config, String display, String aura, String trigger, double gauge_unit_tax) {
        super(id, config, display, aura, trigger, gauge_unit_tax);
        this.aura = aura;
    }

    @Override
    public void trigger(DamagePacket damage, double gauge_unit, String decay_rate, LivingEntity entity, @Nullable Entity damager, StatProvider stats, EntityDamageEvent.DamageCause damage_cause) {

        int level = 1;
        double swirl_bonus = 0;
        if (damager != null) {
            if (damager instanceof Player player) {
                level = PlayerData.get(player).getLevel();
                swirl_bonus = stats.getStat("AST_SWIRL_BONUS");
            } else {
                ActiveMob mythicMob = MythicBukkit.inst().getMobManager().getActiveMob(damager.getUniqueId()).orElse(null);
                if (mythicMob != null) {
                    level = (int) mythicMob.getLevel();
                }
            }
        }

        double resistance_multiplier = StatCalculation.getResistanceMultiplier(entity.getUniqueId(), getConfig().getString("damage-element"));

        String formula = getConfig().getString("damage-formula");
        assert formula != null;
        Expression expression = new ExpressionBuilder(formula)
                .variables("attacker_level", "elemental_mastery", "resistance_multiplier", "swirl_bonus")
                .build()
                .setVariable("attacker_level", level)
                .setVariable("elemental_mastery", stats.getStat("AST_ELEMENTAL_MASTERY"))
                .setVariable("resistance_multiplier", resistance_multiplier)
                .setVariable("swirl_bonus", swirl_bonus);

        double final_damage = expression.evaluate();
        damage(final_damage, damager, entity, this.aura, false, false, damage_cause);

        double swirl_radius = getConfig().getDouble("swirl-radius");
        List<Entity> swirl_entities = new ArrayList<>(entity.getNearbyEntities(swirl_radius, swirl_radius, swirl_radius));

        String swirled_aura_gauge = ConfigLoader.getDefaultGauge();
        for (String trigger_gauge : Objects.requireNonNull(getConfig().getConfigurationSection("swirled-aura-gauge")).getKeys(false)) {
            double start = Double.parseDouble(trigger_gauge.split("-")[0]);
            double end = Double.parseDouble(trigger_gauge.split("-")[1]);

            if (gauge_unit > start && gauge_unit <= end) {
                String s = getConfig().getString("swirled-aura-gauge."+trigger_gauge);
                if (s != null) swirled_aura_gauge = s;
            }
        }

        for (Entity swirl_entity : swirl_entities) {
            boolean mob_type_filter = damager != null && ConfigLoader.aoeDamageFilterEnable() && Combat.getLastMobType(damager) != Combat.getMobType(swirl_entity);
            if (swirl_entity == damager || swirl_entity.isInvulnerable() || swirl_entity.hasMetadata("NPC") || mob_type_filter || (swirl_entity instanceof Player player && (player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR)))) continue;
            if (swirl_entity instanceof LivingEntity swirled_living_entity && !swirled_living_entity.isInvulnerable()) {
                damage(final_damage, damager, swirled_living_entity, Objects.requireNonNull(getConfig().getConfigurationSection("aura-overriding")).getKeys(false).contains(this.aura) ? getConfig().getString("aura-overriding."+this.aura) : this.aura, false, Double.parseDouble(Utils.splitTextAndNumber(swirled_aura_gauge)[0]), Utils.splitTextAndNumber(swirled_aura_gauge)[1], "SWIRL_"+this.aura, 0, false, damage_cause);
            }
        }

        spawnParticle(entity, getConfig().getStringList("particle"));
        playSound(entity, getConfig().getStringList("sound"));
    }
}
