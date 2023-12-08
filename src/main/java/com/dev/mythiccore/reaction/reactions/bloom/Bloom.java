package com.dev.mythiccore.reaction.reactions.bloom;

import com.dev.mythiccore.combat.Combat;
import com.dev.mythiccore.reaction.reaction_type.TriggerAuraReaction;
import com.dev.mythiccore.utils.Utils;
import io.lumine.mythic.lib.api.stat.provider.StatProvider;
import io.lumine.mythic.lib.damage.DamagePacket;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;

public class Bloom extends TriggerAuraReaction {
    public Bloom(String id, ConfigurationSection config, String display, String aura, String trigger, double gauge_unit_tax) {
        super(id, config, display, aura, trigger, gauge_unit_tax);
    }

    @Override
    public boolean trigger(DamagePacket damage, double gauge_unit, String decay_rate, LivingEntity entity, @Nullable Entity damager, StatProvider stats, EntityDamageEvent.DamageCause damage_cause) {

        double random_radius = Utils.randomNumber(0.5, getConfig().getDouble("dendro-core-spawn-radius"));

        double random_angle = Utils.randomNumber(0, 360);
        double angle_radian = Math.toRadians(random_angle);

        double x = random_radius * Math.cos(angle_radian);
        double z = random_radius * Math.sin(angle_radian);

        Location dendro_core_loc = entity.getLocation().clone().add(x, 1, z);

        DendroCoreManager.spawnDendroCore(this, dendro_core_loc, damager instanceof LivingEntity livingEntity ? livingEntity : null, stats, 100, damage_cause, Combat.getLastMobType(damager));

        spawnParticle(entity, getConfig().getStringList("particle"));
        playSound(entity, getConfig().getStringList("sound"));

        return true;
    }
}
