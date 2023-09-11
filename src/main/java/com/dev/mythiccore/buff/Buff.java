package com.dev.mythiccore.buff;


import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.buff.buffs.BuffStatus;
import org.bukkit.Bukkit;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Store the data of Buffs of all entities
 * And count down tick of buff time
 */
public class Buff {
    public final Map<UUID, BuffData> mapBuffData = new HashMap<>();

    public BuffData getBuff(UUID uuid) {
        return !mapBuffData.containsKey(uuid) ? new BuffData(uuid) : mapBuffData.get(uuid);
    }

    public void startTick() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(MythicCore.getInstance(), () -> {
            try {
                if (!mapBuffData.isEmpty()) {
                    for (UUID keys : mapBuffData.keySet()) {
                        for (BuffStatus buff : mapBuffData.get(keys).getTotalBuffs()) {
                            mapBuffData.get(keys).reduceDuration(buff.getUniqueId(), 1);
                        }
                    }
                }
            } catch (ConcurrentModificationException ignored) {}
        }, 1, 1);
    }
}
