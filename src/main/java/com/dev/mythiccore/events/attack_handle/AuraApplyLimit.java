package com.dev.mythiccore.events.attack_handle;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.listener.events.aura.ReactionTriggerEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.UUID;

public class AuraApplyLimit implements Listener {

    private static final HashMap<UUID, AuraLimit> limitMap = new HashMap<>();

    @EventHandler
    public void auraLimit(ReactionTriggerEvent event) {
        if (event.getTrigger() != null) {

            if (limitMap.containsKey(event.getTrigger())) {

                var limit = limitMap.get(event.getTrigger());

                if (limit.getCount() >= MythicCore.getInstance().getConfig().getInt("General.aura-apply-limit-per-hit")) {
                    event.setCancelled(true);
                } else {
                    limit.add();
                }
            } else {
                limitMap.put(event.getTrigger(), new AuraLimit());

                Bukkit.getScheduler().runTaskLaterAsynchronously(MythicCore.getInstance(), ()->{
                    limitMap.remove(event.getTrigger());
                }, 5);
            }
        }
    }

    public static class AuraLimit {
        private int count = 1;

        public void add() {
            count++;
        }
        public int getCount() {
            return count;
        }
    }
}
