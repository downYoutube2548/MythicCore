package com.dev.mythiccore.reaction.reaction_type;

import com.dev.mythiccore.reaction.ElementalReaction;
import io.lumine.mythic.lib.api.stat.provider.StatProvider;
import io.lumine.mythic.lib.damage.DamagePacket;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;

public abstract class TriggerAuraReaction extends ElementalReaction {

    private final double gauge_unit_tax;
    private final String aura;
    private final String trigger;

    public TriggerAuraReaction(String id, ConfigurationSection config, String display, String aura, String trigger, double gauge_unit_tax) {
        super(id, config, display);
        this.gauge_unit_tax = gauge_unit_tax;
        this.aura = aura;
        this.trigger = trigger;
    }


    public String getAura() {
        return this.aura;
    }
    public String getTrigger() {
        return this.trigger;
    }

    public void t(DamagePacket damage, double gauge_unit, String decay_rate, LivingEntity entity, @Nullable Entity damager, StatProvider stats, EntityDamageEvent.DamageCause damage_cause) {

        if (damage.getElement() == null) return;

        boolean reduceGauge = trigger(damage, gauge_unit, decay_rate, entity, damager, stats, damage_cause);

        if (reduceGauge) {
            double final_gauge_unit = gauge_unit * gauge_unit_tax;
            getAuraData(entity.getUniqueId()).reduceAura(getAura(), final_gauge_unit);
        }
    }

    public abstract boolean trigger(DamagePacket damage, double gauge_unit, String decay_rate, LivingEntity entity, @Nullable Entity damager, StatProvider stats, EntityDamageEvent.DamageCause damage_cause);
}
