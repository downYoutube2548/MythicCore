package com.dev.mythiccore.reaction.reactions.frozen;

import com.dev.mythiccore.reaction.reaction_type.TriggerAuraReaction;
import com.dev.mythiccore.utils.ConfigLoader;
import io.lumine.mythic.lib.api.stat.provider.StatProvider;
import io.lumine.mythic.lib.damage.DamagePacket;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;

public class Frozen extends TriggerAuraReaction {

    public Frozen(String id, ConfigurationSection config, String display, String aura, String trigger, double gauge_unit_tax) {
        super(id, config, display, aura, trigger, gauge_unit_tax);
    }

    @Override
    public void trigger(DamagePacket damage, double gauge_unit, String decay_rate, LivingEntity entity, @Nullable Entity damager, StatProvider stats, EntityDamageEvent.DamageCause damage_cause) {
        getAuraData(entity.getUniqueId()).addAura(getConfig().getString("frozen-aura-id"), gauge_unit, getConfig().getString("frozen-aura-gauge-decay-rate"));
        if (entity instanceof Player player) {
            player.setFreezeTicks((int) (gauge_unit * ConfigLoader.getDecayRate(getConfig().getString("frozen-aura-gauge-decay-rate"))));
        }

        spawnParticle(entity, getConfig().getStringList("particle"));
        playSound(entity, getConfig().getStringList("sound"));
    }

    public void trigger(LivingEntity entity, double gauge_unit) {
        getAuraData(entity.getUniqueId()).addAura(getConfig().getString("frozen-aura-id"), gauge_unit, getConfig().getString("frozen-aura-gauge-decay-rate"));
        if (entity instanceof Player player) {
            player.setFreezeTicks((int) (gauge_unit * ConfigLoader.getDecayRate(getConfig().getString("frozen-aura-gauge-decay-rate"))));
        }

        spawnParticle(entity, getConfig().getStringList("particle"));
        playSound(entity, getConfig().getStringList("sound"));
    }
}
