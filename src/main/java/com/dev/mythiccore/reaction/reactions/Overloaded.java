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
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Overloaded extends TriggerAuraReaction {

    public Overloaded(String id, ConfigurationSection config, String display, String aura, String trigger, double gauge_unit_tax) {
        super(id, config, display, aura, trigger, gauge_unit_tax);
    }

    @Override
    public void trigger(DamagePacket damage, double gauge_unit, String decay_rate, LivingEntity entity, @Nullable Entity damager, StatProvider stats, EntityDamageEvent.DamageCause damage_cause) {

        int attacker_level = 1;
        double elemental_mastery = 0;

        if (damager != null) {
            if (damager instanceof Player player) {
                PlayerData playerData = PlayerData.get(player);

                elemental_mastery = stats.getStat("AST_ELEMENTAL_MASTERY");
                attacker_level = playerData.getLevel();
            } else {
                ActiveMob mythicMob = MythicBukkit.inst().getMobManager().getActiveMob(damager.getUniqueId()).orElse(null);
                attacker_level = (mythicMob != null) ? (int) mythicMob.getLevel() : 1;
            }
        }


        double aoe_radius = getConfig().getDouble("aoe-radius");
        List<Entity> aoe_entities = new ArrayList<>(entity.getNearbyEntities(aoe_radius, aoe_radius, aoe_radius));
        aoe_entities.add(entity);
        for (Entity aoe_entity : aoe_entities) {
            boolean mob_type_filter = damager != null && ConfigLoader.aoeDamageFilterEnable() && Combat.getLastMobType(damager) != Combat.getMobType(aoe_entity);
            if (aoe_entity == damager || aoe_entity.isInvulnerable() || aoe_entity.hasMetadata("NPC") || mob_type_filter || (aoe_entity instanceof Player player && (player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR)))) continue;
            if (aoe_entity instanceof LivingEntity aoe_living_entity && !aoe_living_entity.isInvulnerable()) {

                double resistance_multiplier = StatCalculation.getResistanceMultiplier(entity.getUniqueId(), getConfig().getString("damage-element"));

                String formula = getConfig().getString("damage-formula");
                assert formula != null;
                Expression expression = new ExpressionBuilder(formula)
                        .variables("attacker_level", "elemental_mastery", "resistance_multiplier")
                        .build()
                        .setVariable("attacker_level", attacker_level)
                        .setVariable("elemental_mastery", elemental_mastery)
                        .setVariable("resistance_multiplier", resistance_multiplier);
                double final_damage = expression.evaluate();

                damage(final_damage, damager, aoe_living_entity, getConfig().getString("damage-element"), false, true, damage_cause);

                if (damager != null) {
                    Vector kb = aoe_living_entity.getLocation().toVector().subtract(damager.getLocation().toVector());
                    if (!kb.isZero()) {
                        int exponent = Math.max(Utils.getExponent(kb.getX()), Math.max(Utils.getExponent(kb.getY()), Utils.getExponent(kb.getZ())));
                        float x = Utils.getMantissa((float) kb.getX(), exponent) / 10;
                        float y = Utils.getMantissa((float) kb.getY(), exponent) / 10;
                        float z = Utils.getMantissa((float) kb.getZ(), exponent) / 10;
                        aoe_living_entity.setVelocity(new Vector(x, y, z).multiply(getConfig().getDouble("knockback-multiplier")));
                    }
                }
            }
        }
    }
}
