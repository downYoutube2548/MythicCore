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
    public static final HashMap<UUID, BukkitTask> tasks = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAttack(EntityDamageEvent event) {

        if (event.isCancelled()) return;

        if (event.getEntity() instanceof LivingEntity entity) {

            UUID entityUUID = event.getEntity().getUniqueId();

            double currentHP = entity.getHealth() - event.getFinalDamage();
            double maxHP = entity.getMaxHealth();
            double lossHP = entity.getHealth() - (entity.getHealth() - event.getFinalDamage());

            if (hpBars.containsKey(entityUUID)) {
                hpBars.get(entityUUID).setCurrentHp(currentHP).setMaxHp(maxHP).setYellowHp(lossHP);
            } else {
                hpBars.put(entityUUID, new BarData(currentHP, maxHP, lossHP, MythicCore.getInstance().getConfig().getInt("General.hp-bar.bar-length")));
            }

            if (tasks.containsKey(entityUUID)) {
                tasks.get(entityUUID).cancel();
            }

            tasks.put(entityUUID, Bukkit.getScheduler().runTaskLaterAsynchronously(MythicCore.getInstance(), ()->{
                tasks.remove(entityUUID);
                hpBars.remove(entityUUID);
            }, 100));
        }
    }

    public static String getHpBar(UUID uuid) {
        if (hpBars.containsKey(uuid)) {
            return hpBars.get(uuid).getHpBar();
        }
        return "";
    }
}
