package com.dev.mythiccore.reaction.reactions.frozen;

import com.dev.mythiccore.MythicCore;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class FreezeActionCanceling implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (MythicCore.getAuraManager().getAura(event.getPlayer().getUniqueId()).getMapAura().containsKey("FROZEN")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(PlayerAttackEvent event) {
        if (MythicCore.getAuraManager().getAura(event.getPlayer().getUniqueId()).getMapAura().containsKey("FROZEN")) {
            event.setCancelled(true);
        }
    }
}
