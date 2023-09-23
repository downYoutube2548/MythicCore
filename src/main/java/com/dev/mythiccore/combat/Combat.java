package com.dev.mythiccore.combat;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.utils.ConfigLoader;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;

public class Combat implements Listener {

    private static final HashMap<Entity, MobType> lastCombat = new HashMap<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageByEntityEvent event) {

        LivingEntity damager;
        if (event.getDamager() instanceof Projectile projectile) {
            if (!(projectile instanceof ThrownPotion) && projectile.getShooter() instanceof LivingEntity shooter) {

                damager = shooter;
            } else {
                return;
            }
        } else if (event.getDamager() instanceof LivingEntity entity) {
            damager = entity;
        } else {
            return;
        }

        if (lastCombat.containsKey(damager)) {
            if (ConfigLoader.aoeDamageFilterEnable() && !lastCombat.get(damager).equals(getMobType(event.getEntity()))) {
                event.setCancelled(true);
                return;
            }
        }

        lastCombat.put(damager, getMobType(event.getEntity()));

        Bukkit.getScheduler().runTaskLaterAsynchronously(MythicCore.getInstance(), () -> lastCombat.remove(damager), 2);
    }
    public static MobType getMobType(Entity entity) {
        if (entity instanceof Enemy) {
            return MobType.ENEMY;
        } else if (entity instanceof Player) {
            return MobType.PLAYER;
        } else {
            return MobType.OTHER;
        }
    }

    public static MobType getLastMobType(Entity entity) {
        return lastCombat.getOrDefault(entity, MobType.NULL);
    }

    public enum MobType {

        NULL(-1),
        ENEMY(0),
        PLAYER(1),
        OTHER(2);

        final int id;
        MobType(int id) {
            this.id = id;
        }
        public int getId() {
            return id;
        }
    }
}
