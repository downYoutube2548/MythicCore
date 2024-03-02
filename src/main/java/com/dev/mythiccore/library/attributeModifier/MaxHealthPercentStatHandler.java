package com.dev.mythiccore.library.attributeModifier;

import io.lumine.mythic.lib.api.stat.StatInstance;
import io.lumine.mythic.lib.api.stat.handler.StatHandler;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MaxHealthPercentStatHandler extends StatHandler {

    private final Attribute attribute;
    public MaxHealthPercentStatHandler(@NotNull ConfigurationSection config, @NotNull Attribute attribute, @NotNull String stat) {
        super(config, stat);
        this.attribute = attribute;
    }

    @Override
    public void runUpdate(@NotNull StatInstance instance) {
        Player player = instance.getMap().getPlayerData().getPlayer();
        double percentHP = player.getHealth() / player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

        final AttributeInstance attrIns = instance.getMap().getPlayerData().getPlayer().getAttribute(attribute);
        removeModifiers(attrIns);

        final double mmo = instance.getTotal();
        final double base = instance.getMap().getStat("MAX_HEALTH");

        attrIns.addModifier(new AttributeModifier("mythiccore.max_health_percent", base * mmo / 100, AttributeModifier.Operation.ADD_NUMBER));

        player.setHealth(Math.min(percentHP, 1) * player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
    }

    public void removeModifiers(AttributeInstance ins) {
        for (AttributeModifier attribute : ins.getModifiers())
            if (attribute.getName().equals("mythiccore.max_health_percent"))
                ins.removeModifier(attribute);
    }
}
