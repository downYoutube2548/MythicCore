package com.dev.mythiccore.reaction.reactions.bloom;

import com.dev.mythiccore.utils.Utils;
import io.lumine.mythic.lib.api.stat.provider.StatProvider;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;

public abstract class DendroCoreReaction {

    private final String id;
    private final ConfigurationSection config;
    private final String display;
    private final String trigger;

    public DendroCoreReaction(String id, ConfigurationSection config, String display, String trigger) {
        this.id = id;
        this.config = config;
        this.display = display;
        this.trigger = trigger;
    }

    public String getId() {
        return this.id;
    }
    public String getTrigger() {
        return this.trigger;
    }
    public String getDisplay() {
        return Utils.colorize(this.display);
    }
    public ConfigurationSection getConfig() {
        return config;
    }

    public abstract void trigger(DendroCore dendro_core, LivingEntity entity, @Nullable Entity damager, StatProvider stats, EntityDamageEvent.DamageCause damage_cause);

}
