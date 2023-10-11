package com.dev.mythiccore.cooldown;

import com.dev.mythiccore.MythicCore;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownData {

    private final Map<LivingEntity, Map<String, Long>> cooldowns;
    private final UUID uuid;

    public CooldownData(UUID uuid) {
        this.uuid = uuid;
        this.cooldowns = new HashMap<>();
    }

    public Map<LivingEntity, Map<String, Long>> getMapCooldown() {
        return cooldowns;
    }

    public Map<String, Long> getEntityCooldown(LivingEntity entity) {
        return cooldowns.getOrDefault(entity, null);
    }

    public long getCooldown(LivingEntity entity, String source) {
        return cooldowns.containsKey(entity) ? cooldowns.get(entity).containsKey(source) ? cooldowns.get(entity).get(source) : 0 : 0;
    }

    public void setCooldown(LivingEntity entity, String source, long duration) {
        if (cooldowns.containsKey(entity)) {
            cooldowns.get(entity).put(source, duration);
        } else {
            cooldowns.put(entity, new HashMap<>(Map.of(source, duration)));
        }

        if (!MythicCore.getCooldownManager().entityCooldownData.containsKey(this.uuid)) MythicCore.getCooldownManager().entityCooldownData.put(this.uuid, this);
    }

    public void removeCooldown(LivingEntity entity) {
        if (!cooldowns.containsKey(entity)) return;
        cooldowns.remove(entity);
        if (cooldowns.isEmpty()) MythicCore.getCooldownManager().entityCooldownData.remove(this.uuid);
    }

    public void removeCooldown(LivingEntity entity, String source) {
        if (!cooldowns.containsKey(entity)) return;
        if (!cooldowns.get(entity).containsKey(source)) return;
        cooldowns.get(entity).remove(source);
        if (cooldowns.get(entity).isEmpty()) removeCooldown(entity);
    }

    public void clearCooldown() {
        MythicCore.getCooldownManager().entityCooldownData.remove(this.uuid);
    }

    public void reduceCooldown(LivingEntity entity, String source, long duration) {
        if (cooldowns.containsKey(entity) && cooldowns.get(entity).containsKey(source)) {
            if (duration >= cooldowns.get(entity).get(source)) {
                removeCooldown(entity, source);
            } else {
                setCooldown(entity, source, cooldowns.get(entity).get(source) - duration);
            }
        }
    }

}
