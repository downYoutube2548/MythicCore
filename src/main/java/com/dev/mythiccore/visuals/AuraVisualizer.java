package com.dev.mythiccore.visuals;

import com.dev.mythiccore.MythicCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class AuraVisualizer {

    public static final HashMap<UUID, TextDisplay> mapHologram = new HashMap<>();

    public static void start() {

        Bukkit.getScheduler().runTaskTimer(MythicCore.getInstance(), () -> {

            try {
                for (UUID uuid : mapHologram.keySet()) {
                    Entity entity = Bukkit.getEntity(uuid);
                    if (entity == null || entity.isDead() || !entity.getLocation().getChunk().isLoaded() || (!MythicCore.getAuraManager().getMapEntityAura().containsKey(uuid) /*&& !MythicCore.getCooldownManager().getEntityCooldown().containsKey(uuid)) && !MythicCore.getBuffManager().getMapBuffData().containsKey(uuid)*/)) {
                        TextDisplay textDisplay = mapHologram.get(uuid);
                        textDisplay.remove();
                        mapHologram.remove(uuid);
                    }
                }

                List<UUID> uuids = new ArrayList<>(MythicCore.getAuraManager().getMapEntityAura().keySet());
                for (UUID uuid : uuids) {
                    Entity entity = Bukkit.getEntity(uuid);
                    if (entity == null || entity.isDead() || !entity.getLocation().getChunk().isLoaded()) {
                        if (!mapHologram.containsKey(uuid)) continue;

                        TextDisplay textDisplay = mapHologram.get(uuid);
                        textDisplay.remove();

                        mapHologram.remove(uuid);

                        continue;
                    }

                    BoundingBox boundingBox = entity.getBoundingBox();
                    float scale = (float)Math.max(boundingBox.getHeight() * 0.5, 1);
                    double height = boundingBox.getHeight() * 1.3;

                    Location spawnLocation = new Location(entity.getWorld(), entity.getLocation().getX(), entity.getLocation().getY()+ height, entity.getLocation().getZ());

                    if (!mapHologram.containsKey(uuid)) {
                        TextDisplay textDisplay = entity.getWorld().spawn(spawnLocation, TextDisplay.class);
                        textDisplay.setPersistent(false);
                        textDisplay.setBillboard(Display.Billboard.CENTER);
                        textDisplay.setText(MythicCore.getAuraManager().getAura(uuid).getAuraIcon()/* + " | " + MythicCore.getCooldownManager().getCooldown(uuid).getMapCooldown()*/);
                        textDisplay.setTransformation(new Transformation(textDisplay.getTransformation().getTranslation(), textDisplay.getTransformation().getLeftRotation(), new Vector3f(scale), textDisplay.getTransformation().getRightRotation()));
                        textDisplay.setShadowed(true);
                        textDisplay.setSeeThrough(true);
                        textDisplay.setBrightness(new Display.Brightness(15, 15));
                        textDisplay.setMetadata("AST_AURA_VISUALIZER", new FixedMetadataValue(MythicCore.getInstance(), true));
                        mapHologram.put(uuid, textDisplay);

                    } else {
                        TextDisplay textDisplay = mapHologram.get(uuid);
                        textDisplay.setText(MythicCore.getAuraManager().getAura(uuid).getAuraIcon()/*+MythicCore.getCooldownManager().getCooldown(uuid).getMapCooldown()+"\n"+MythicCore.getBuffManager().getBuff(uuid).getActivateBuffs()*/);
                        textDisplay.teleport(spawnLocation);
                    }
                }
            } catch (Exception ignored) {}
        }, 1, 1);
    }
}
