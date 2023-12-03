package com.dev.mythiccore.reaction.reactions.frozen;

import com.dev.mythiccore.MythicCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class FreezeEffect {

    public static void effectApplier() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(MythicCore.getInstance(), ()->{
            for (UUID uuid : MythicCore.getAuraManager().getMapEntityAura().keySet()) {
                if (MythicCore.getAuraManager().getAura(uuid).getMapAura().containsKey(frozen_aura_id)) {
                    Bukkit.getScheduler().runTask(MythicCore.getInstance(), ()->{
                        Entity entity = Bukkit.getEntity(uuid);
                        if (entity != null && entity.isValid() && !entity.isDead() && entity instanceof LivingEntity livingEntity) {
                            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 21, amplifier, false, false, false));
                        }
                    });
                }
            }
        },20, 20);
    }

    static String frozen_aura_id = MythicCore.getInstance().getConfig().getString("Elemental-Reaction.FROZEN.frozen-aura-id");
    static int amplifier = MythicCore.getInstance().getConfig().getInt("Elemental-Reaction.FROZEN.frozen-slowness-amplifier");
}
