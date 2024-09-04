package com.dev.mythiccore.listener.events;

import com.dev.mythiccore.reaction.ElementalReaction;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MakeReactionEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Entity entity;
    private final ElementalReaction reaction;

    public MakeReactionEvent(Entity entity, ElementalReaction reaction) {
        this.entity = entity;
        this.reaction = reaction;
    }

    public Entity getEntity() {
        return entity;
    }

    public ElementalReaction getReaction() {
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
