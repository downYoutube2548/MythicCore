package com.dev.mythiccore.events;

import com.dev.mythiccore.MythicCore;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class ProjectileLaunch implements Listener {

    @EventHandler
    public void onShoot(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        if (!(projectile.getShooter() instanceof Player player)) return;
        if (player.getItemInUse() == null) return;

        projectile.setMetadata("ATTACK_WEAPON", new FixedMetadataValue(MythicCore.getInstance(), player.getItemInUse()));
    }
}
