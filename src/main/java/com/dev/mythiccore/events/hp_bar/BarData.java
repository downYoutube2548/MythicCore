package com.dev.mythiccore.events.hp_bar;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.utils.ConfigLoader;
import com.dev.mythiccore.utils.Utils;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;

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

    public String getHpBar() {

        StringBuilder sb = new StringBuilder();

        FileConfiguration config = MythicCore.getInstance().getConfig();

        sb.append(Utils.colorize(FontImageWrapper.replaceFontImages(config.getString("message.hp-bar.bar-prefix")
                .replace("{currentHP}", Utils.Format(currentHp))
                .replace("{maxHP}", Utils.Format(maxHp))
                .replace("{lossHP}", Utils.Format(yellowHp))
        )));

        int bars = (int) ((currentHp / maxHp) * maxBar);
        int y = 0;
        for (int i = 1; i <= maxBar; i++) {
            if (i <= bars) {
                sb.append(Utils.colorize(FontImageWrapper.replaceFontImages(config.getString("message.hp-bar.current-hp-color") + config.getString("message.hp-bar.symbol"))));
            } else {
                if (y < (int)(yellowHp / maxHp * maxBar)) {
                    sb.append(Utils.colorize(FontImageWrapper.replaceFontImages(config.getString("message.hp-bar.loss-hp-color") + config.getString("message.hp-bar.symbol"))));
                    y++;
                } else {
                    sb.append(Utils.colorize(FontImageWrapper.replaceFontImages(config.getString("message.hp-bar.bar-color") + config.getString("message.hp-bar.symbol"))));
                }
            }
        }

        sb.append(Utils.colorize(FontImageWrapper.replaceFontImages(config.getString("message.hp-bar.bar-suffix")
                .replace("{currentHP}", Utils.Format(currentHp))
                .replace("{maxHP}", Utils.Format(maxHp))
                .replace("{lossHP}", Utils.Format(yellowHp))
        )));
        return sb.toString();
    }
}
