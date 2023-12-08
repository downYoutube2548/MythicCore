package com.dev.mythiccore.reaction.reaction_type;

import com.dev.mythiccore.enums.MobType;
import com.dev.mythiccore.reaction.ElementalReaction;
import io.lumine.mythic.lib.api.stat.provider.StatProvider;
import io.lumine.mythic.lib.damage.DamagePacket;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;

public abstract class DoubleAuraReaction extends ElementalReaction {

    private final long reaction_frequency;
    private final double gauge_unit_tax;
    private final String aura1;
    private final String aura2;

    public DoubleAuraReaction(String id, ConfigurationSection config, String display, String aura1, String aura2, long reaction_frequency, double gauge_unit_tax) {
        super(id, config, display);
        this.reaction_frequency = reaction_frequency;
        this.gauge_unit_tax = gauge_unit_tax;
        this.aura1 = aura1;
        this.aura2 = aura2;
    }

    public String getFirstAura() {
        return this.aura1;
    }
    public String getSecondAura() {
        return this.aura2;
    }

    public long getFrequency() {
        return reaction_frequency;
    }

    public void t(DamagePacket damage, double gauge_unit, String decay_rate, LivingEntity entity, @Nullable Entity damager, StatProvider stats, EntityDamageEvent.DamageCause damage_cause, MobType last_mob_type) {

        if (damage.getElement() == null) return;

        boolean reduceGauge = trigger(damage, gauge_unit, decay_rate, entity, damager, stats, damage_cause, last_mob_type);

        if (reduceGauge) {
            double final_gauge_unit = gauge_unit_tax;
            getAuraData(entity.getUniqueId()).reduceAura(aura1, final_gauge_unit);
            getAuraData(entity.getUniqueId()).reduceAura(aura2, final_gauge_unit);
        }
    }

    public abstract boolean trigger(DamagePacket damage, double gauge_unit, String decay_rate, LivingEntity entity, @Nullable Entity damager, StatProvider stats, EntityDamageEvent.DamageCause damage_cause, MobType last_mob_type);
}
