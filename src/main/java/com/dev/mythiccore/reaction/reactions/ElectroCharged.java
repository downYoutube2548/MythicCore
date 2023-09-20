package com.dev.mythiccore.reaction.reactions;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.combat.Combat;
import com.dev.mythiccore.library.SnapshotStats;
import com.dev.mythiccore.reaction.reaction_type.DoubleAuraReaction;
import com.dev.mythiccore.utils.StatCalculation;
import io.lumine.mythic.lib.damage.DamagePacket;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ElectroCharged extends DoubleAuraReaction {

    public ElectroCharged(String id, String display, String aura1, String aura2, long reaction_frequency, double gauge_unit_tax) {
        super(id, display, aura1, aura2, reaction_frequency, gauge_unit_tax);
    }

    @Override
    public void trigger(DamagePacket damage, double gauge_unit, String decay_rate, LivingEntity entity, @Nullable Entity damager, EntityDamageEvent.DamageCause damage_cause, SnapshotStats stats) {

        BouncingDamage bouncingDamage = new BouncingDamage(damager, entity, stats, getConfig().getInt("maximum-bounces-target"), damage_cause);
        bouncingDamage.start();

    }


    private class BouncingDamage extends BukkitRunnable {
        private final Entity damager; // The player or entity initiating the damage
        private final Set<Entity> damagedEntities; // Set to track already damaged entities
        private final SnapshotStats stats; // The amount of damage to apply
        private final int maxBounces; // The maximum number of bounces
        private final LivingEntity entity;
        private final EntityDamageEvent.DamageCause damage_cause;
        private LivingEntity current_entity;

        public BouncingDamage(@Nullable Entity damager, LivingEntity entity, SnapshotStats stats, int maxBounces, EntityDamageEvent.DamageCause damage_cause) {
            this.damager = damager;
            this.entity = entity;
            this.damagedEntities = new HashSet<>();
            this.stats = stats;
            this.maxBounces = maxBounces;
            this.damage_cause = damage_cause;
        }

        @Override
        public void run() {
            // Find a valid target entity that hasn't been damaged yet
            LivingEntity currentTarget = (current_entity == null) ? findNextValidTarget(entity) : findNextValidTarget(current_entity);

            if (currentTarget != null && !currentTarget.isDead() && currentTarget.isValid()) {

                double resistance_multiplier = StatCalculation.getResistanceMultiplier(currentTarget.getUniqueId(), getConfig().getString("damage-element"));

                String formula = getConfig().getString("damage-formula");
                assert formula != null;
                Expression expression = new ExpressionBuilder(formula)
                        .variables("attacker_level", "elemental_mastery", "resistance_multiplier")
                        .build()
                        .setVariable("attacker_level", stats.getStat("LEVEL"))
                        .setVariable("elemental_mastery", stats.getStat("AST_ELEMENTAL_MASTERY"))
                        .setVariable("resistance_multiplier", resistance_multiplier);

                double final_damage = expression.evaluate();
                damage(final_damage, damager, currentTarget, getConfig().getString("damage-element"), false, false, damage_cause);

                // Add the entity to the set of damaged entities
                damagedEntities.add(currentTarget);

                current_entity = currentTarget;

                // Check if we've reached the maximum number of bounces
                if (damagedEntities.size() >= maxBounces) {
                    cancel(); // Stop the task
                }
            } else {
                cancel(); // Stop the task if the target is dead or null.
            }
        }

        // Find the next valid target entity
        private LivingEntity findNextValidTarget(Entity entity) {
            List<Entity> aoe_entities = new ArrayList<>(List.of(entity));
            double check_radius = getConfig().getDouble("check-radius");
            aoe_entities.addAll(entity.getNearbyEntities(check_radius, check_radius, check_radius));
            for (Entity aoe_entity : aoe_entities) {
                if (aoe_entity == damager || damagedEntities.contains(aoe_entity) || aoe_entity.isInvulnerable() || aoe_entity.hasMetadata("NPC") || stats.getStat("LAST_MOB_TYPE") != Combat.getMobType(aoe_entity).getId() || (aoe_entity instanceof Player player && (player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR)))) continue;
                if (aoe_entity instanceof LivingEntity aoe_living_entity && !aoe_living_entity.isInvulnerable() && MythicCore.getAuraManager().getAura(aoe_living_entity.getUniqueId()).getMapAura().containsKey(getConfig().getString("bounce-required-aura"))) {
                    return aoe_living_entity;
                }
            }
            return null; // No valid target found
        }

        // Start the bouncing damage task
        public void start() {
            runTaskTimer(MythicCore.getInstance(), 0L, 3L); // 0L initial delay, 2L ticks (0.1 seconds)
        }
    }
}
