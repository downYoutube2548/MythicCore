package com.dev.mythiccore.listener.events;

import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.DamageMetadata;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

/**
 * This class is the Event triggered when damage is dealt by Mob
 * can use with @EventHandler
 */
public class MobAttackEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final EntityDamageByEntityEvent event;
    private final AttackMetadata attack;

    public MobAttackEvent(EntityDamageByEntityEvent event, AttackMetadata attack) {
        this.event = event;
        this.attack = attack;
    }

    public EntityDamageByEntityEvent toBukkit() {
        return event;
    }

    public Entity getDamager() {
        return event.getDamager();
    }

    public boolean isCancelled() {
        return this.event.isCancelled();
    }

    public void setCancelled(boolean value) {
        this.event.setCancelled(value);
    }

    public @NotNull AttackMetadata getAttack() {
        return this.attack;
    }

    public @NotNull DamageMetadata getDamage() {
        return this.attack.getDamage();
    }

    public @NotNull LivingEntity getEntity() {
        return this.attack.getTarget();
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
