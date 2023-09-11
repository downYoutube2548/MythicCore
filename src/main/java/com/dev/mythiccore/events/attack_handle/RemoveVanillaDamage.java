package com.dev.mythiccore.events.attack_handle;

import com.dev.mythiccore.MythicCore;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.event.AttackEvent;
import io.lumine.mythic.lib.api.event.DamageCheckEvent;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.damage.AttackMetadata;
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * This class use to remove vanilla damage calculation after MythicCore deal damage
 * (e.g. Armor, Armor Toughness of Victim)
 */
public class RemoveVanillaDamage implements Listener{

    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onDamage(EntityDamageEvent event) {
        if (!MythicCore.getInstance().getConfig().getBoolean("General.disable-vanilla-damage")) { return; }

        if (event.getEntity() instanceof LivingEntity && !(event instanceof DamageCheckEvent) && event.getDamage() != 0.0) {
            AttackMetadata attack = MythicLib.plugin.getDamage().findAttack(event);
            if (!attack.isPlayer() || attack.getPlayer().getGameMode() != GameMode.SPECTATOR) {
                AttackEvent attackEvent = attack.isPlayer() ? new PlayerAttackEvent(event, attack) : new AttackEvent(event, attack);
                if (!attackEvent.isCancelled()) {
                    try {
                        event.setDamage(EntityDamageEvent.DamageModifier.BASE, attack.getDamage().getDamage());
                        event.setDamage(EntityDamageEvent.DamageModifier.ABSORPTION, 0);
                        event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0);
                        event.setDamage(EntityDamageEvent.DamageModifier.HARD_HAT, 0);
                        event.setDamage(EntityDamageEvent.DamageModifier.RESISTANCE, 0);
                        event.setDamage(EntityDamageEvent.DamageModifier.MAGIC, 0);
                        event.setDamage(EntityDamageEvent.DamageModifier.BLOCKING, 0);
                    } catch (Exception ignored) {}

                }
            }
        }
    }
}
