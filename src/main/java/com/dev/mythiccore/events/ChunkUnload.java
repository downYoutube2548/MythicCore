package com.dev.mythiccore.events;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.reaction.reactions.bloom.DendroCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.metadata.MetadataValue;

public class ChunkUnload implements Listener {

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity.hasMetadata("AST_DENDRO_CORE_ENTITY")) {
                MetadataValue metadataValue = entity.getMetadata("AST_DENDRO_CORE_ENTITY").get(0);
                DendroCore dendroCore = (DendroCore) metadataValue.value();
                if (dendroCore != null) Bukkit.getScheduler().runTask(MythicCore.getInstance(), ()->dendroCore.remove());
            }
        }
    }
}
