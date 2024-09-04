package com.dev.mythiccore.statistic;

import com.dev.mythiccore.MythicCore;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class DamagePerHitStatistic implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAttack(EntityDamageByEntityEvent event) {

        if (event.isCancelled()) return;

        if (event.getDamager() instanceof Player player) {
            NamespacedKey namespacedKey = new NamespacedKey(MythicCore.getInstance(), "damageperhit-statistic");
            PersistentDataContainer container = player.getPersistentDataContainer();

            Double amount = container.get(namespacedKey, PersistentDataType.DOUBLE);

            if (amount == null) {
                container.set(namespacedKey, PersistentDataType.DOUBLE, event.getFinalDamage());
            } else {
                if (amount < event.getFinalDamage()) {
                    container.set(namespacedKey, PersistentDataType.DOUBLE, event.getFinalDamage());
                }
            }
        }
    }
}
