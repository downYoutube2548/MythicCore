package com.dev.mythiccore.events.dps_check;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class DPSCheck implements Listener {

    public static final HashMap<UUID, DamageCount> dps = new HashMap<>();
    public static final HashMap<UUID, BukkitTask> tasks = new HashMap<>();
    public static final HashMap<UUID, Long> startTime = new HashMap<>();
    public static final HashMap<UUID, Long> lastHitTime = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAttack(EntityDamageEvent event) {

        Entity entity = event.getEntity();
        if (tasks.containsKey(entity.getUniqueId())) {
            if (dps.containsKey(entity.getUniqueId())) {

                tasks.get(entity.getUniqueId()).cancel();

                DamageCount damageCount = dps.get(entity.getUniqueId());
                damageCount.add(event.getDamage());

                lastHitTime.put(entity.getUniqueId(), System.currentTimeMillis());

                tasks.put(entity.getUniqueId(), Bukkit.getScheduler().runTaskLaterAsynchronously(MythicCore.getInstance(), ()->{
                    startTime.remove(entity.getUniqueId());
                    dps.remove(entity.getUniqueId());
                    lastHitTime.remove(entity.getUniqueId());
                    tasks.remove(entity.getUniqueId());
                }, 100));
            }
        } else {

            long now = System.currentTimeMillis();

            startTime.put(entity.getUniqueId(), now);
            dps.put(entity.getUniqueId(), new DamageCount(event.getDamage()));

            tasks.put(entity.getUniqueId(), Bukkit.getScheduler().runTaskLaterAsynchronously(MythicCore.getInstance(), ()->{
                startTime.remove(entity.getUniqueId());
                dps.remove(entity.getUniqueId());
                lastHitTime.remove(entity.getUniqueId());
                tasks.remove(entity.getUniqueId());
            }, 100));

            lastHitTime.put(entity.getUniqueId(), now + 1000);

        }
    }

    public static class DamageCount {
        private double damage;

        public DamageCount(double damage) {
            this.damage = damage;
        }

        public void add(double value) {
            damage += value;
        }
        public double getCount() {
            return damage;
        }
    }

    public static String getDPS(UUID entity) {
        //Bukkit.broadcastMessage(dps.get(entity).getCount() + " / (" + (lastHitTime.get(entity) - startTime.get(entity)) + " / 1000)");
        return dps.containsKey(entity) ? Utils.formatNumber(dps.get(entity).getCount() / ((lastHitTime.get(entity) - startTime.get(entity)) / 1000.0)) : "";
    }

    public static String getTotal(UUID entity) {
        return dps.containsKey(entity) ? Utils.formatNumber(dps.get(entity).getCount()) : "";
    }
}
