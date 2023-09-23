package com.dev.mythiccore.events.attack_handle.deal_damage;

import com.dev.mythiccore.listener.events.MiscAttackEvent;
import com.dev.mythiccore.utils.StatCalculation;
import io.lumine.mythic.lib.damage.DamagePacket;
import io.lumine.mythic.lib.damage.DamageType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Arrays;

/**
 * This class use to deal damage from another damage source -> Mob or Player
 * (e.g. Cactus, Magma Block, Void, Fire Tick, Potion)
 */
public class MiscAttack implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onMiscAttack(MiscAttackEvent event) {
        try {
            LivingEntity victim = event.getEntity();

            for (DamagePacket packet : event.getDamage().getPackets()) {
                if (packet.getElement() == null || Arrays.asList(packet.getTypes()).contains(DamageType.DOT)) {
                    continue;
                }

                packet.setValue(StatCalculation.getFinalDamage(victim.getUniqueId(), packet));
            }
        } catch (NullPointerException ignored) {}
    }
}
