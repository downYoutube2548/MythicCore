package com.dev.mythiccore.reaction.reaction_type;

import com.dev.mythiccore.reaction.ElementalReaction;
import io.lumine.mythic.lib.damage.DamagePacket;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;

public abstract class DoubleAuraReaction extends ElementalReaction {
    public DoubleAuraReaction(String id, String display, String aura, String trigger) {
        super(id, display, aura, trigger);
    }

    @Override
    public void trigger(DamagePacket damage, double gauge_unit, String decay_rate, LivingEntity entity, @Nullable Entity damager, EntityDamageEvent.DamageCause damage_cause) {
        trigger(damage, gauge_unit, decay_rate, damager, entity, damage_cause);
    }

    public abstract void trigger(DamagePacket damage, double gauge_unit, String decay_rate, @Nullable Entity damager, LivingEntity entity, EntityDamageEvent.DamageCause damage_cause);
}
