package com.dev.mythiccore.cooldown;

import com.dev.mythiccore.MythicCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import java.util.*;

public class InternalCooldown {

    protected Map<UUID, CooldownData> entityCooldownData = new HashMap<>();

    public CooldownData getCooldown(UUID uuid) {
        return !entityCooldownData.containsKey(uuid) ? new CooldownData(uuid) : entityCooldownData.get(uuid);
    }

    public Map<UUID, CooldownData> getEntityCooldown() { return entityCooldownData; }

    public void startTick() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(MythicCore.getInstance(), () -> {
            try {
                if (!entityCooldownData.isEmpty()) {
                    for (UUID keys : entityCooldownData.keySet()) {
                        Iterator<LivingEntity> iteratorB = entityCooldownData.get(keys).getMapCooldown().keySet().iterator();
                        while (iteratorB.hasNext()) {
                            LivingEntity entity = iteratorB.next();
                            if (entity == null || entity.isDead() || !entity.isValid()) {
                                iteratorB.remove();
                            } else {
                                for (String source : entityCooldownData.get(keys).getEntityCooldown(entity).keySet()) {
                                    entityCooldownData.get(keys).reduceCooldown(entity, source, 1);
                                }
                            }
                        }
                    }
                }
            } catch (ConcurrentModificationException ignored) {}
        }, 1, 1);
    }
}
