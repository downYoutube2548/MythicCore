package com.dev.mythiccore.events;

import com.dev.mythiccore.MythicCore;
import net.Indyuce.mmocore.api.event.PlayerResourceUpdateEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class Stamina implements Listener {

    public static HashMap<Player, BukkitTask> sprintTasks = new HashMap<>();

    public void addSprintTaskPlayer(Player player) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(MythicCore.getInstance(), () -> {
            PlayerData data = PlayerData.get(player);
            if (player.isSprinting() && !player.isSneaking() && !player.isFlying() && !(player.getGameMode() == GameMode.CREATIVE) && !(player.getGameMode() == GameMode.SPECTATOR)) {
                if (data.getStamina() <= 5) {
                    player.sendTitle("", ChatColor.RED+"Low Stamina!", 0, 100, 2);
                }
                if (data.getStamina() <= 1) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 2, true, false, false));
                    player.setSprinting(false);
                }
                data.giveStamina(-1, PlayerResourceUpdateEvent.UpdateReason.SKILL_COST);
            } else {
                data.giveStamina(1, PlayerResourceUpdateEvent.UpdateReason.REGENERATION);
            }
        }, 20, 20);
        sprintTasks.put(player, task);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        sprintTasks.get(event.getPlayer()).cancel();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        addSprintTaskPlayer(player);
    }
}
