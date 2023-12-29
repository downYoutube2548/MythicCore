package com.dev.mythiccore.events.hp_bar;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.utils.Utils;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class BarData {

    private double currentHp;
    private final double maxHp;
    private double yellowHp;
    private final int maxBar;
    private BukkitTask task = null;
    private BukkitTask hpBarLifetime;

    public BarData(double currentHp, double maxHp, double yellowBar, int maxBar, BukkitTask hpBarTask) {
        this.currentHp = currentHp;
        this.maxHp = maxHp;
        this.yellowHp = yellowBar;
        this.maxBar = maxBar;
        this.hpBarLifetime = hpBarTask;

        if (yellowBar > 0) {
            task = Bukkit.getScheduler().runTaskTimerAsynchronously(MythicCore.getInstance(), () -> {
                if (yellowHp > 0) {
                    yellowHp -= (int) (0.01 * maxHp);
                } else {
                    task.cancel();
                    task = null;
                }
            }, 2, 2);
        }
    }

    public void setHp(double value) {
        if (value < currentHp) {
            this.yellowHp = this.currentHp - value + this.yellowHp;
            if (task == null) {
                task = Bukkit.getScheduler().runTaskTimerAsynchronously(MythicCore.getInstance(), () -> {
                    if (yellowHp > 0) {
                        yellowHp -= (int) (0.01 * maxHp);
                    } else {
                        task.cancel();
                        task = null;
                    }
                }, 2, 2);
            }
        }
        this.currentHp = value;
    }

    public BukkitTask getTask() {
        return task;
    }

    public void newLifeTime(BukkitTask task) {
        hpBarLifetime.cancel();
        hpBarLifetime = task;
    }

    public String getHpBar() {

        StringBuilder sb = new StringBuilder();

        sb.append(Utils.colorize(FontImageWrapper.replaceFontImages("&f:offset_-16:桂:offset_-61:")));

        int bars = (int) ((currentHp / maxHp) * maxBar);
        int y = 0;
        for (int i = 1; i <= maxBar; i++) {
            if (i <= bars) {
                sb.append(Utils.colorize(FontImageWrapper.replaceFontImages("&a蔓:offset_-16:")));
            } else {
                if (y < (int)(yellowHp / maxHp * maxBar)) {
                    sb.append(Utils.colorize(FontImageWrapper.replaceFontImages("&4蔓:offset_-16:")));
                    y++;
                } else {
                    sb.append(Utils.colorize(FontImageWrapper.replaceFontImages("&8蔓:offset_-16:")));
                }
            }
        }
        return sb.toString();
    }
}
