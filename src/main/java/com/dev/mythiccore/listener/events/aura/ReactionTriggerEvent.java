package com.dev.mythiccore.listener.events.aura;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ReactionTriggerEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final UUID trigger;
    private boolean cancelled;

    public ReactionTriggerEvent(@Nullable UUID trigger) {
        this.trigger = trigger;
    }
    public @Nullable UUID getTrigger() {
        return trigger;
    }


    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
