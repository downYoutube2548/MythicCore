package com.dev.mythiccore.reaction.reactions.bloom.sub_reaction;

import com.dev.mythiccore.combat.Combat;
import com.dev.mythiccore.reaction.reactions.bloom.DendroCore;
import com.dev.mythiccore.reaction.reactions.bloom.DendroCoreReaction;
import com.dev.mythiccore.utils.ConfigLoader;
import com.dev.mythiccore.utils.StatCalculation;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.lib.api.stat.provider.StatProvider;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;

public class Burgeon extends DendroCoreReaction {
    public Burgeon(String id, ConfigurationSection config, String display, String trigger) {
        super(id, config, display, trigger);
    }

    @Override
    public void trigger(DendroCore dendro_core, LivingEntity entity, @Nullable Entity damager, StatProvider stats, EntityDamageEvent.DamageCause damage_cause) {

        int check_radius = getConfig().getInt("check-radius");
        for (Entity e : dendro_core.getDendroCore().getNearbyEntities(check_radius, check_radius, check_radius)) {
            if (e instanceof LivingEntity livingEntity) {
                if (e == damager) continue;
                if (ConfigLoader.aoeDamageFilterEnable() && damager != null && !Combat.getLastMobType(damager).equals(Combat.getMobType(e))) continue;

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

                double resistance_multiplier = StatCalculation.getResistanceMultiplier(livingEntity.getUniqueId(), getConfig().getString("damage-element"));

                String formula = getConfig().getString("damage-formula");
                assert formula != null;
                Expression expression = new ExpressionBuilder(formula)
                        .variables("attacker_level", "elemental_mastery", "resistance_multiplier")
                        .build()
                        .setVariable("attacker_level", attacker_level)
                        .setVariable("elemental_mastery", elemental_mastery)
                        .setVariable("resistance_multiplier", resistance_multiplier);

                double final_damage = expression.evaluate();

                dendro_core.getInstance().damage(final_damage, damager, livingEntity, getConfig().getString("damage-element"), false, false, damage_cause);

                // visual
                try {
                    for (String s : getConfig().getStringList("explode-sound")) {
                        String[] raw_sound = s.split(":");
                        String sound = raw_sound[0];
                        int volume = Integer.parseInt(raw_sound[1]);
                        int pitch = Integer.parseInt(raw_sound[2]);

                        livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.valueOf(sound), volume, pitch);
                    }

                    for (String p : getConfig().getStringList("explode-particle")) {
                        String[] raw_particle = p.split(":");
                        String particle = raw_particle[0];
                        int speed = Integer.parseInt(raw_particle[1]);
                        int count = Integer.parseInt(raw_particle[2]);

                        livingEntity.getWorld().spawnParticle(Particle.valueOf(particle), livingEntity.getLocation(), count, 0, 0, 0, speed);
                    }

                } catch (NumberFormatException ignored) {}

                dendro_core.getDendroCore().remove();

            }
        }

    }
}
