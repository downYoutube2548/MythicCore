package com.dev.mythiccore.events.hp_bar;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.utils.EntityStatManager;
import com.dev.mythiccore.utils.Utils;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;

public class BarData {

    private double currentHp;
    private double maxHp;
    private double yellowHp;
    private final int maxBar;

    public BarData(double currentHp, double maxHp, double yellowBar, int maxBar) {
        this.currentHp = currentHp;
        this.maxHp = maxHp;
        this.yellowHp = yellowBar;
        this.maxBar = maxBar;
    }

    public BarData setCurrentHp(double value) {
        currentHp = value;
        return this;
    }

    public BarData setMaxHp(double maxHp) {
        this.maxHp = maxHp;
        return this;
    }

    public BarData setYellowHp(double yellowHp) {
        this.yellowHp = yellowHp;
        return this;
    }

    public String getHpBar(Entity entity) {

        StringBuilder sb = new StringBuilder();

        EntityStatManager entityStat = new EntityStatManager(entity);
        ConfigurationSection config = entityStat.has("AURA_BAR_FORMAT", String.class) ? MythicCore.getInstance().getConfig().getConfigurationSection("General.aura-bar-format."+entityStat.getStringStat("AURA_BAR_FORMAT")+".hp-bar") : MythicCore.getInstance().getConfig().getConfigurationSection("General.hp-bar");
        if (config == null) config = MythicCore.getInstance().getConfig().getConfigurationSection("General.hp-bar");

        sb.append(Utils.colorize(FontImageWrapper.replaceFontImages(config.getString("bar-prefix")
                .replace("{currentHP}", Utils.formatNumber(currentHp))
                .replace("{maxHP}", Utils.formatNumber(maxHp))
                .replace("{lossHP}", Utils.formatNumber(yellowHp))
                .replace("{color}", currentHp >= 0.5 * maxHp ? "&a" : currentHp >= 0.2 * maxHp ? "&e" : "&c")
        )));

        int bars = (int) ((currentHp / maxHp) * maxBar);
        int y = 0;
        for (int i = 1; i <= maxBar; i++) {
            if (i <= bars) {
                sb.append(Utils.colorize(FontImageWrapper.replaceFontImages(config.getString("current-hp-color") + config.getString("symbol"))));
            } else {
                if (y < (int)(yellowHp / maxHp * maxBar)) {
                    sb.append(Utils.colorize(FontImageWrapper.replaceFontImages(config.getString("loss-hp-color") + config.getString("symbol"))));
                    y++;
                } else {
                    sb.append(Utils.colorize(FontImageWrapper.replaceFontImages(config.getString("bar-color") + config.getString("symbol"))));
                }
            }
        }

        sb.append(Utils.colorize(FontImageWrapper.replaceFontImages(config.getString("bar-suffix")
                .replace("{currentHP}", Utils.formatNumber(currentHp))
                .replace("{maxHP}", Utils.formatNumber(maxHp))
                .replace("{lossHP}", Utils.formatNumber(yellowHp))
                .replace("{color}", currentHp >= 0.5 * maxHp ? "&a" : currentHp >= 0.2 * maxHp ? "&e" : "&c")
        )));
        return sb.toString();
    }
}
