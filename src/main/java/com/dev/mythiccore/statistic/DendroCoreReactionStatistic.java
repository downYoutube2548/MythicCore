package com.dev.mythiccore.statistic;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.listener.events.DendroCoreReactionEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class DendroCoreReactionStatistic implements Listener {

    @EventHandler
    public void onReaction(DendroCoreReactionEvent event) {
        if (event.getEntity() instanceof Player player) {
            NamespacedKey namespacedKey = new NamespacedKey(MythicCore.getInstance(), "dendrocore-reaction-statistic-"+event.getReaction().getId());
            PersistentDataContainer container = player.getPersistentDataContainer();

            Long count = container.get(namespacedKey, PersistentDataType.LONG);

            if (count == null) {
                container.set(namespacedKey, PersistentDataType.LONG, 1L);
            } else {
                container.set(namespacedKey, PersistentDataType.LONG, count+1);
            }
        }
    }
}
