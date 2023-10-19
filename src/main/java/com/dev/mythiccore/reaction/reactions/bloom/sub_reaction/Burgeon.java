package com.dev.mythiccore.reaction.reactions.bloom.sub_reaction;

import com.dev.mythiccore.reaction.reactions.bloom.DendroCore;
import com.dev.mythiccore.reaction.reactions.bloom.DendroCoreReaction;
import io.lumine.mythic.lib.api.stat.provider.StatProvider;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;

public class Burgeon extends DendroCoreReaction {
    public Burgeon(String id, ConfigurationSection config, String display, String trigger) {
        super(id, config, display, trigger);
    }

    @Override
    public void trigger(DendroCore dendro_core, LivingEntity entity, @Nullable Entity damager, StatProvider stats, EntityDamageEvent.DamageCause damage_cause) {

    }
}
