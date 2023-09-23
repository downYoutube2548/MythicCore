package com.dev.mythiccore.reaction.reaction_type;

import com.dev.mythiccore.combat.Combat;
import com.dev.mythiccore.reaction.ElementalReaction;
import io.lumine.mythic.lib.api.stat.provider.StatProvider;
import io.lumine.mythic.lib.damage.DamagePacket;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;

public abstract class DoubleAuraReaction extends ElementalReaction {

    private final long reaction_frequency;
    private final double gauge_unit_tax;

    public DoubleAuraReaction(String id, String display, String aura1, String aura2, long reaction_frequency, double gauge_unit_tax) {
        super(id, display, aura1, aura2);
        this.reaction_frequency = reaction_frequency;
        this.gauge_unit_tax = gauge_unit_tax;
    }

    public long getFrequency() {
        return reaction_frequency;
    }

    public void t(DamagePacket damage, double gauge_unit, String decay_rate, LivingEntity entity, @Nullable Entity damager, StatProvider stats, EntityDamageEvent.DamageCause damage_cause, Combat.MobType last_mob_type) {

        if (damage.getElement() == null) return;
        double final_gauge_unit = gauge_unit_tax;
        getAuraData(entity.getUniqueId()).reduceAura(getAura(), final_gauge_unit);
        getAuraData(entity.getUniqueId()).reduceAura(getTrigger(), final_gauge_unit);

        trigger(damage, gauge_unit, decay_rate, entity, damager, stats, damage_cause, last_mob_type);
    }

    public abstract void trigger(DamagePacket damage, double gauge_unit, String decay_rate, LivingEntity entity, @Nullable Entity damager, StatProvider stats, EntityDamageEvent.DamageCause damage_cause, Combat.MobType last_mob_type);
}
