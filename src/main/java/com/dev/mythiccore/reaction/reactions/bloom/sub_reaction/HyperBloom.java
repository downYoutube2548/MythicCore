package com.dev.mythiccore.reaction.reactions.bloom.sub_reaction;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.combat.Combat;
import com.dev.mythiccore.reaction.reactions.bloom.DendroCore;
import com.dev.mythiccore.reaction.reactions.bloom.DendroCoreReaction;
import com.dev.mythiccore.utils.ConfigLoader;
import com.dev.mythiccore.utils.StatCalculation;
import com.dev.mythiccore.utils.Utils;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.lib.api.stat.provider.StatProvider;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

public class HyperBloom extends DendroCoreReaction {
    public HyperBloom(String id, ConfigurationSection config, String display, String trigger) {
        super(id, config, display, trigger);
    }

    @Override
    public void trigger(DendroCore dendro_core, LivingEntity entity, @Nullable Entity damager, StatProvider stats, EntityDamageEvent.DamageCause damage_cause) {
        int check_radius = getConfig().getInt("check-radius");
        Location dendroCoreLocation = dendro_core.getHologram().getLocation();

        LivingEntity target_entity = dendro_core.getHologram().getWorld().getNearbyEntities(dendroCoreLocation, check_radius, check_radius, check_radius).stream()
                .filter(e -> e instanceof LivingEntity)
                .filter(e -> !e.equals(damager))
                .filter(e -> !(ConfigLoader.aoeDamageFilterEnable() && damager != null && !Combat.getLastMobType(damager).equals(Combat.getMobType(e))))
                .map(e -> (LivingEntity) e)
                .min(Comparator.comparingDouble(e -> e.getLocation().distanceSquared(dendroCoreLocation)))
                .orElse(null);

        if (target_entity == null) return;

        long life_time = dendro_core.getInstance().getConfig().getLong("sub-reaction.HYPERBLOOM.life-time-after-trigger");
        if (life_time >= 0) dendro_core.setLifeTime(life_time);

        int attacker_level = 1;
        double elemental_mastery = 0;
        double hyperbloom_bonus = 0;

        if (damager != null) {
            if (damager instanceof Player player) {
                PlayerData playerData = PlayerData.get(player);
                elemental_mastery = stats.getStat("AST_ELEMENTAL_MASTERY");
                hyperbloom_bonus = stats.getStat("AST_HYPERBLOOM_BONUS");
                attacker_level = playerData.getLevel();
            } else {
                ActiveMob mythicMob = MythicBukkit.inst().getMobManager().getActiveMob(damager.getUniqueId()).orElse(null);
                attacker_level = (mythicMob != null) ? (int) mythicMob.getLevel() : 1;
            }
        }

        double resistance_multiplier = StatCalculation.getResistanceMultiplier(target_entity.getUniqueId(), getConfig().getString("damage-element"));

        int finalAttacker_level = attacker_level;
        double finalElemental_mastery = elemental_mastery;
        double finalHyperbloom_bonus = hyperbloom_bonus;

        final int[] taskIdHolder = new int[1]; // Hold task ID for cancellation

        taskIdHolder[0] = Bukkit.getScheduler().runTaskTimer(MythicCore.getInstance(), new Runnable() {
            final double speed = 0.4;
            final double homingStrength = 0.15;
            final double hitRadius = 1.2;
            Vector velocity = new Vector(0, 0.2, 0);
            final long startTime = System.currentTimeMillis();

            @Override
            public void run() {
                if (!target_entity.isValid() || target_entity.isDead() || !dendro_core.getHologram().isEnabled()) {
                    dendro_core.remove();
                    Bukkit.getScheduler().cancelTask(taskIdHolder[0]);
                    return;
                }

                Location currentLoc = dendro_core.getHologram().getLocation();
                Location targetLoc = target_entity.getLocation().add(0, 1, 0); // upper body

                // Homing logic
                Vector desired = targetLoc.toVector().subtract(currentLoc.toVector()).normalize().multiply(speed);
                velocity = velocity.multiply(1 - homingStrength).add(desired.multiply(homingStrength));

                Location next = currentLoc.clone().add(velocity);
                dendro_core.getHologram().setLoc(next);
                dendro_core.getHologram().update();

                currentLoc.getWorld().spawnParticle(
                        Particle.REDSTONE,
                        currentLoc,
                        1,
                        0, 0, 0,
                        new Particle.DustOptions(org.bukkit.Color.fromRGB(102, 255, 204), 1.2F)
                );

                // Check for impact
                if (next.distanceSquared(targetLoc) <= hitRadius * hitRadius) {
                    performImpact(damager, stats, finalAttacker_level, finalElemental_mastery, finalHyperbloom_bonus,
                            resistance_multiplier, dendro_core, target_entity, damage_cause);
                    Bukkit.getScheduler().cancelTask(taskIdHolder[0]);
                }

                // Timeout after 3 seconds
                if (System.currentTimeMillis() - startTime > 3000) {
                    dendro_core.remove();
                    Bukkit.getScheduler().cancelTask(taskIdHolder[0]);
                }
            }

        }, 0, 1).getTaskId(); // Store task ID properly
    }

    private void performImpact(Entity damager, StatProvider stats, int attacker_level, double elemental_mastery,
                               double hyperbloom_bonus, double resistance_multiplier,
                               DendroCore core, LivingEntity target, EntityDamageEvent.DamageCause cause) {

        String formula = getConfig().getString("damage-formula");
        if (formula == null) return;

        Expression expr = new ExpressionBuilder(formula)
                .variables("attacker_level", "elemental_mastery", "resistance_multiplier", "level_multiplier", "hyperbloom_bonus")
                .build()
                .setVariable("attacker_level", attacker_level)
                .setVariable("elemental_mastery", elemental_mastery)
                .setVariable("resistance_multiplier", resistance_multiplier)
                .setVariable("level_multiplier", 1)
                .setVariable("hyperbloom_bonus", hyperbloom_bonus);

        double final_damage = expr.evaluate();

        core.getInstance().damage(final_damage, damager, target, getConfig().getString("damage-element"), false, false, true, false, cause);

        try {
            for (String s : getConfig().getStringList("explode-sound")) {
                String[] parts = s.split(":");
                if (parts.length >= 3) {
                    target.getWorld().playSound(target.getLocation(), Sound.valueOf(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2]));
                }
            }

            for (String p : getConfig().getStringList("explode-particle")) {
                String[] parts = p.split(":");
                if (parts.length >= 3) {
                    target.getWorld().spawnParticle(Particle.valueOf(parts[0]), target.getLocation(), Integer.parseInt(parts[2]), 0, 0, 0, Double.parseDouble(parts[1]));
                }
            }
        } catch (Exception ignored) {}

        core.remove();
    }
}
