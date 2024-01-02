package com.dev.mythiccore.events.hp_bar;

import com.dev.mythiccore.MythicCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class HpBar implements Listener {

    public static final HashMap<UUID, BarData> hpBars = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAttack(EntityDamageEvent event) {

        if (event.isCancelled()) return;

        if (event.getEntity() instanceof LivingEntity entity) {
            if (entity.getHealth() > 100000000) return;

            if (hpBars.containsKey(event.getEntity().getUniqueId())) {

                BarData bar = hpBars.get(event.getEntity().getUniqueId());
                bar.setHp(entity.getHealth() - event.getFinalDamage());
                BukkitTask task = getTask(event.getEntity().getUniqueId());
                bar.newLifeTime(task);

            } else {

                BukkitTask task = getTask(event.getEntity().getUniqueId());
                hpBars.put(event.getEntity().getUniqueId(), new BarData(entity.getHealth() - event.getFinalDamage(), entity.getMaxHealth(), entity.getHealth() - (entity.getHealth() - event.getFinalDamage()), 40, task));
            }
        }
    }

    private BukkitTask getTask(UUID uuid) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(MythicCore.getInstance(), () -> {

            if (hpBars.containsKey(uuid)) {
                if (hpBars.get(uuid).getTask() != null) {
                    hpBars.get(uuid).getTask().cancel();
                }
                hpBars.remove(uuid);
            }

        }, 200);
    }

    public static String getHpBar(UUID uuid) {
        if (hpBars.containsKey(uuid)) {
            return hpBars.get(uuid).getHpBar();
        }
        return "";
    }
}
