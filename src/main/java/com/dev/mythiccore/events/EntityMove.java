package com.dev.mythiccore.events;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class EntityMove implements Listener {
    @EventHandler
    public void onEntityMove(PlayerMoveEvent event) {
        if (event.getTo() != null) {
            Bukkit.broadcastMessage(event.getFrom().getX() + " " + event.getTo().getX());
        }
    }
}
