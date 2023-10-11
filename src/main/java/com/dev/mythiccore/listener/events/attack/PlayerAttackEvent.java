package com.dev.mythiccore.listener.events.attack;

import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.player.PlayerMetadata;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerAttackEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final EntityDamageByEntityEvent event;
    private final AttackMetadata attack;
    private final PlayerMetadata attacker;

    public PlayerAttackEvent(EntityDamageByEntityEvent event, AttackMetadata attack) {
        this.event = event;
        this.attack = attack;
        this.attacker = (PlayerMetadata) attack.getAttacker();
    }

    public EntityDamageByEntityEvent toBukkit() {
        return event;
    }

    public PlayerMetadata getAttacker() {
        return attacker;
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

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean b) {

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
