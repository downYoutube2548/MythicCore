package com.dev.mythiccore.reaction.reactions;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.combat.Combat;
import com.dev.mythiccore.reaction.reaction_type.SingleReaction;
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
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

public class Resonance extends SingleReaction {
    public Resonance(String id, ConfigurationSection config, String display, String aura, double gauge_unit_tax) {
        super(id, config, display, aura, gauge_unit_tax);
    }

    @Override
    public boolean trigger(DamagePacket damage, double gauge_unit, String decay_rate, LivingEntity entity, @Nullable Entity damager, StatProvider stats, EntityDamageEvent.DamageCause damage_cause) {

        if (MythicCore.getCooldownManager().getCooldown(entity.getUniqueId()).getCooldown(entity, "RESONANCE_COOLDOWN") > 0) return false;
        MythicCore.getCooldownManager().getCooldown(entity.getUniqueId()).setCooldown(entity, "RESONANCE_COOLDOWN", getConfig().getLong("resonance-cooldown"));

        int level = 1;
        double resonance_bonus = 0;
        double defense = 0;
        if (damager != null) {
            if (damager instanceof Player player) {
                level = PlayerData.get(player).getLevel();
                resonance_bonus = stats.getStat("AST_RESONANCE_BONUS");
                defense = stats.getStat("DEFENSE");
            } else {
                ActiveMob mythicMob = MythicBukkit.inst().getMobManager().getActiveMob(damager.getUniqueId()).orElse(null);
                if (mythicMob != null) {
                    level = (int) mythicMob.getLevel();
                    defense = mythicMob.getVariables().getFloat("DEFENSE");
                }
            }
        }

        Utils.generateParticles(Particle.valueOf(getConfig().getString("resonance-particle.particle")), getConfig().getDouble("resonance-particle.radius"), getConfig().getInt("resonance-particle.points"), getConfig().getDouble("resonance-particle.speed"), entity.getLocation(), getConfig().getDouble("resonance-particle.x-rotation"), getConfig().getDouble("resonance-particle.y-rotation"));
        //damage(final_damage, damager, entity, this.getTrigger(), false, false, damage_cause);

        double resonance_radius = getConfig().getDouble("resonance-radius");
        List<LivingEntity> entities = entity.getNearbyEntities(resonance_radius, resonance_radius, resonance_radius).stream()
                .filter(e -> e instanceof LivingEntity)
                .filter(e -> !e.equals(damager) && !e.hasMetadata("NPC") && !e.isInvulnerable() && !(e instanceof Player player && (player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR))))
                .filter(e -> !(ConfigLoader.aoeDamageFilterEnable() && damager != null && !Combat.getLastMobType(damager).equals(Combat.getMobType(e))))
                .map(e -> (LivingEntity) e).sorted(Comparator.comparingDouble(e -> e.getLocation().distanceSquared(entity.getLocation()))).toList();


        int i = 0;
        for (LivingEntity living_entity : entities) {

            double resistance_multiplier = StatCalculation.getResistanceMultiplier(living_entity.getUniqueId(), getConfig().getString("damage-element"));

            String formula = getConfig().getString("damage-formula");
            assert formula != null;
            Expression expression = new ExpressionBuilder(formula)
                    .variables("attacker_level", "elemental_mastery", "resistance_multiplier", "resonance_bonus", "defense")
                    .build()
                    .setVariable("attacker_level", level)
                    .setVariable("elemental_mastery", stats.getStat("AST_ELEMENTAL_MASTERY"))
                    .setVariable("resistance_multiplier", resistance_multiplier)
                    .setVariable("resonance_bonus", resonance_bonus)
                    .setVariable("defense", defense);

            double final_damage = expression.evaluate();

            if (i >= getConfig().getInt("resonance-limit")) break;

            double distance = entity.getLocation().distance(living_entity.getLocation());
            Bukkit.getScheduler().runTaskLater(MythicCore.getInstance(), ()-> damage(final_damage, damager, living_entity, this.getTrigger(), false, 0, ConfigLoader.getDefaultDecayRate(), "RESONANCE_REACTION", 0, false, damage_cause), (long) (getConfig().getDouble("delay-tick-per-block") * distance));

            i++;
            if (getAuraData(living_entity.getUniqueId()).getMapAura().containsKey(this.getTrigger()) && getConfig().getBoolean("stop-on-resonate-other")) break;

        }

        spawnParticle(entity, getConfig().getStringList("particle"));
        playSound(entity, getConfig().getStringList("sound"));

        return true;
    }
}
