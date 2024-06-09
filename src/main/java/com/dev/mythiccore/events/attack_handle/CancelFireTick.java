package com.dev.mythiccore.events.attack_handle;

import com.dev.mythiccore.MythicCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class CancelFireTick implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onFireTickDamage(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK && MythicCore.getInstance().getConfig().getBoolean("General.cancel-fire-tick")) {
            e.setCancelled(true);
            e.getEntity().setFireTicks(0);
        }
    }
}
