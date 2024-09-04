package com.dev.mythiccore.aura;
import com.dev.mythiccore.MythicCore;
import org.bukkit.Bukkit;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Store the data of Elemental Inflection
 * of all entities
 * And count down tick of inflection time
 */
public class Aura {
    protected final Map<UUID, AuraData> entityAura = new HashMap<>();

    public AuraData getAura(UUID uuid) {
        return !entityAura.containsKey(uuid) ? new AuraData(uuid) : entityAura.get(uuid);
    }

    public Map<UUID, AuraData> getMapEntityAura() { return entityAura; }

    public void startTick() {

        Bukkit.getScheduler().runTaskTimerAsynchronously(MythicCore.getInstance(), () -> {
            try {
                if (!entityAura.isEmpty()) {
                    for (UUID keys : entityAura.keySet()) {
                        for (String aura : entityAura.get(keys).getMapAura().keySet()) {
                            entityAura.get(keys).reduceAura(aura, 1);
                        }
                    }
                }
            } catch (ConcurrentModificationException ignored) {}
        }, 1, 1);
    }
}
