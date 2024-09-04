package com.dev.mythiccore.events;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.listener.events.ShieldHitEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ShieldHit implements Listener {

    @EventHandler
    public void onShieldHit(ShieldHitEvent event) {
        if (event.getEntity() instanceof Player player) {
            for (String command : MythicCore.getInstance().getConfig().getStringList("General.shield-hit-command")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("<player>", player.getName()));
            }
        }
    }
}
