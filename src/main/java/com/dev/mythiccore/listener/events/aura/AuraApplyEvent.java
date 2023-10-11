package com.dev.mythiccore.listener.events.aura;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class AuraApplyEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final String aura;
    private final UUID uuid;

    public AuraApplyEvent(UUID uuid, String aura) {
        this.aura = aura;
        this.uuid = uuid;
    }

    public String getAura() {
        return aura;
    }
    public UUID getUUID() {
        return uuid;
    }
    public @Nullable Entity getEntity() {
        return Bukkit.getEntity(uuid);
    }


    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
