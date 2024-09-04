package com.dev.mythiccore.listener.events;

import com.dev.mythiccore.buff.buffs.ElementalShield;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ShieldHitEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final LivingEntity entity;
    private final ElementalShield shield;
    private final double amount;

    public ShieldHitEvent(LivingEntity entity, ElementalShield shield, double amount) {
        this.entity = entity;
        this.shield = shield;
        this.amount = amount;
    }

    public ElementalShield getShield() {
        return shield;
    }

    public double getAmount() {
        return amount;
    }

    public LivingEntity getEntity() {
        return entity;
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
