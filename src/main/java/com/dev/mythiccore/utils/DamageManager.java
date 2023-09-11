package com.dev.mythiccore.utils;

import com.dev.mythiccore.stats.provider.ASTEntityStatProvider;
import io.lumine.mythic.bukkit.utils.lib.lang3.Validate;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.player.PlayerMetadata;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.logging.Level;

public class DamageManager {

    public static void registerAttack(@NotNull AttackMetadata attack, boolean knockback, boolean ignoreImmunity, EntityDamageEvent.DamageCause damage_cause) {
        try {
            double damage = attack.getDamage().getDamage();
            if (!(damage <= 0.0)) {
                Validate.notNull(attack.getTarget(), "Target cannot be null");
                MythicLib.plugin.getDamage().markAsMetadata(attack);

                try {
                    LivingEntity entity = (attack.getAttacker() instanceof PlayerMetadata) ? ((PlayerMetadata) attack.getAttacker()).getPlayer() : (attack.getAttacker() instanceof ASTEntityStatProvider) ? ((ASTEntityStatProvider) attack.getAttacker()).getEntity() : null;
                    applyDamage(attack.getDamage().getDamage(), attack.getTarget(), entity, knockback, ignoreImmunity, damage_cause);
                } catch (Exception var10) {
                    MythicLib.plugin.getLogger().log(Level.SEVERE, "Caught an exception (1) while damaging entity '" + attack.getTarget().getUniqueId() + "':");
                    var10.printStackTrace();
                } finally {
                    MythicLib.plugin.getDamage().unmarkAsMetadata(attack);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void applyDamage(double damage, @NotNull LivingEntity target, @Nullable LivingEntity damager, boolean knockback, boolean ignoreImmunity, EntityDamageEvent.DamageCause damage_cause) {
        try {
            if (!knockback) {
                AttributeInstance instance = target.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
                AttributeModifier mod = getModifier("noKnockback", 100);

                try {
                    assert instance != null;
                    instance.addModifier(mod);
                    applyDamage(damage, target, damager, true, ignoreImmunity, damage_cause);
                } catch (Exception var21) {
                    MythicLib.plugin.getLogger().log(Level.SEVERE, "Caught an exception (2) while damaging entity '" + target.getUniqueId() + "':");
                    var21.printStackTrace();
                } finally {
                    assert instance != null;
                    instance.removeModifier(mod);
                }
            } else if (ignoreImmunity) {
                int noDamageTicks = target.getNoDamageTicks();

                try {
                    target.setNoDamageTicks(0);
                    applyDamage(damage, target, damager, knockback, false, damage_cause);
                } catch (Exception var19) {
                    MythicLib.plugin.getLogger().log(Level.SEVERE, "Caught an exception (3) while damaging entity '" + target.getUniqueId() + "':");
                    var19.printStackTrace();
                } finally {
                    target.setNoDamageTicks(noDamageTicks);
                }
            } else {
                Validate.isTrue(damage > 0.0, "Damage must be strictly positive");
                if (damager == null) {
                    EntityDamageEvent event = new EntityDamageEvent(target, damage_cause, damage);
                    target.setLastDamageCause(event);
                    target.setLastDamage(damage);
                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) target.setHealth(Math.max(0, target.getHealth() - damage));
                } else {
                    target.damage(damage, damager);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static AttributeModifier getModifier(String name, double amount) {
        return new AttributeModifier(UUID.randomUUID(), name, amount, AttributeModifier.Operation.ADD_NUMBER);
    }

}
