package com.dev.mythiccore.visuals;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.listener.events.aura.AuraApplyEvent;
import com.dev.mythiccore.utils.ConfigLoader;
import net.Indyuce.mmoitems.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class HealthBar implements Listener {

    public static HealthBarSettings defaultHealthBar = ConfigLoader.getDefaultHealthBar();
    public static Map<String, HealthBarSettings> customHealthBars = ConfigLoader.getCustomHealthBars();
    public static Map<String, String> textReplace = ConfigLoader.getTextReplace();


    public static final HashMap<Entity, HealthBarData> healthBarMap = new HashMap<>();
    public static final Map<UUID, Pair<BukkitRunnable, String>> removeTaskMap = new HashMap<>();
    public static Team damageIndicatorTeam;

    @EventHandler(priority = EventPriority.MONITOR)
    public void damageEvent(EntityDamageEvent event) {
        if (event.isCancelled()) return;
        handleHealthEvent((LivingEntity) event.getEntity(), event.getFinalDamage());
    }

    @EventHandler
    public void healEvent(EntityRegainHealthEvent event) {
        if (event.isCancelled()) return;
        handleHealthEvent((LivingEntity) event.getEntity(), 0);
    }

    private static void handleHealthEvent(LivingEntity entity, double damaged) {
        double currentHealth = Math.max(0, entity.getHealth() - damaged);
        double maxHealth = entity.getMaxHealth();

        checkInstance(entity, (healthBarData -> {
            healthBarData.name = entity.getName();
            healthBarData.name_uncolored = entity.getName().replaceAll("(?i)[§&][0-9A-FK-OR]", "");
            healthBarData.health = String.format("%.2f", currentHealth);
            healthBarData.max_health = String.format("%.2f", maxHealth);
            healthBarData.health_bar = healthBarData.createHealthBar(currentHealth, maxHealth, damaged);

            healthBarData.reload();

            if (removeTaskMap.containsKey(entity.getUniqueId())) {
                removeTaskMap.get(entity.getUniqueId()).getKey().cancel();
            }

            BukkitRunnable removeHealthTask = new BukkitRunnable() {
                @Override
                public void run() {
                    healthBarData.name = "";
                    healthBarData.name_uncolored = "";
                    healthBarData.health = "";
                    healthBarData.max_health = "";
                    healthBarData.health_bar = "";

                    healthBarData.reload();

                    removeTaskMap.remove(entity.getUniqueId());
                }
            };


            removeHealthTask.runTaskLater(MythicCore.getInstance(), customHealthBars.getOrDefault(healthBarData.entityType, defaultHealthBar).hologramDuration()); // healthBarHologramDuration ticks
            removeTaskMap.put(entity.getUniqueId(), new Pair<>(removeHealthTask, healthBarData.health_bar));

        }));
    }

    @EventHandler
    public void auraApplyEvent(AuraApplyEvent event) {
        handleAuraEvent((LivingEntity) event.getEntity());
    }

    private static void handleAuraEvent(LivingEntity entity) {
        checkInstance(entity, healthBarData -> {
            healthBarData.aura = MythicCore.getAuraManager().getAura(entity.getUniqueId()).getAuraIcon();
            healthBarData.reload();
        });
    }

    public static void checkInstance(Entity entity, Consumer<HealthBarData> consumer) {

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getMainScoreboard();

        damageIndicatorTeam = board.getTeam("DamageIndicatorBars");
        if (damageIndicatorTeam == null) {
            damageIndicatorTeam = board.registerNewTeam("DamageIndicatorBars");
            damageIndicatorTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        }

        if (healthBarMap.containsKey(entity)) {
            consumer.accept(healthBarMap.get(entity));
        } else {
            HealthBarData healthBarData = new HealthBarData(entity);
            consumer.accept(healthBarData);
            healthBarMap.put(entity, healthBarData);
        }
    }
}
