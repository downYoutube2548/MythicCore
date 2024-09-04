package com.dev.mythiccore.events;

import io.lumine.mythic.bukkit.events.MythicStatChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class StatsChanged implements Listener {

    @EventHandler
    public void onStatsChanged(MythicStatChangeEvent event) {

        Bukkit.broadcastMessage("a");

    }
}
