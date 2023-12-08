package com.dev.mythiccore.events.attack_handle.deal_damage;

import com.dev.mythiccore.utils.StatCalculation;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.damage.DamagePacket;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.player.PlayerMetadata;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.Random;

/**
 * This class use to deal damage from Player -> Mob or Player
 */
public class PlayerAttack implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerAttack(PlayerAttackEvent event) {

        LivingEntity victim = event.getEntity();

        // loop all elemental type damage
        for (DamagePacket packet : event.getDamage().getPackets()) {

            if (Arrays.asList(packet.getTypes()).contains(DamageType.DOT) || Arrays.asList(packet.getTypes()).contains(DamageType.MINION)) continue;

            // working only damage that have element (include physical damage)
            if (packet.getElement() == null) {
                packet.setValue(0);
                continue;
            }

            PlayerMetadata attackerStats = event.getAttacker();
            double AttackerCRITRate = Math.max(Math.min(attackerStats.getStat("AST_CRITICAL_RATE"), 100), 0);
            boolean isCritical = new Random().nextDouble() < AttackerCRITRate / 100;
            if (isCritical) event.getDamage().registerElementalCriticalStrike(packet.getElement());
            packet.setValue(StatCalculation.getFinalDamage(event.getAttacker(), victim.getUniqueId(), packet, isCritical));
        }
    }
}
