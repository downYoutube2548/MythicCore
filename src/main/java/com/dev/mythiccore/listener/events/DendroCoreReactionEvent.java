package com.dev.mythiccore.listener.events;

import com.dev.mythiccore.reaction.reactions.bloom.DendroCoreReaction;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class DendroCoreReactionEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Entity entity;
    private final DendroCoreReaction reaction;

    public DendroCoreReactionEvent(Entity entity, DendroCoreReaction reaction) {
        this.entity = entity;
        this.reaction = reaction;
    }

    public Entity getEntity() {
        return entity;
    }

    public DendroCoreReaction getReaction() {
        return reaction;
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
