package com.dev.mythiccore.events.hp_bar;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.utils.EntityStatManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class HpBar implements Listener {

    public static final HashMap<Entity, BarData> hpBars = new HashMap<>();
    public static final HashMap<Entity, BukkitTask> tasks = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAttack(EntityDamageEvent event) {

        if (event.isCancelled()) return;

        if (event.getEntity() instanceof LivingEntity entity) {

            EntityStatManager entityStat = new EntityStatManager(entity);
            int bar_length = entityStat.has("AURA_BAR_FORMAT", String.class) ? MythicCore.getInstance().getConfig().getInt("General.aura-bar-format."+entityStat.getStringStat("AURA_BAR_FORMAT")+".hp-bar.bar-length") : MythicCore.getInstance().getConfig().getInt("General.hp-bar.bar-length");

            double currentHP = entity.getHealth() - event.getFinalDamage();
            double maxHP = entity.getMaxHealth();
            double lossHP = entity.getHealth() - (entity.getHealth() - event.getFinalDamage());

            if (hpBars.containsKey(entity)) {
                hpBars.get(entity).setCurrentHp(currentHP).setMaxHp(maxHP).setYellowHp(lossHP);
            } else {
                hpBars.put(entity, new BarData(currentHP, maxHP, lossHP, bar_length));
            }

            if (tasks.containsKey(entity)) {
                tasks.get(entity).cancel();
            }

            tasks.put(entity, Bukkit.getScheduler().runTaskLaterAsynchronously(MythicCore.getInstance(), ()->{
                tasks.remove(entity);
                hpBars.remove(entity);
            }, 100));
        }
    }

    public static String getHpBar(Entity entity) {
        if (hpBars.containsKey(entity)) {
            return hpBars.get(entity).getHpBar(entity);
        }
        return "";
    }
}
