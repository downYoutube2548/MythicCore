package com.dev.mythiccore.events.attack_handle;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class CancelFireTick implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onFireTickDamage(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
            e.setCancelled(true);
            e.getEntity().setFireTicks(0);
        }
    }
}
