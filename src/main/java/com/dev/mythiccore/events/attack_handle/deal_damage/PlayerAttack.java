package com.dev.mythiccore.events.attack_handle.deal_damage;

import com.dev.mythiccore.enums.AttackSource;
import com.dev.mythiccore.library.ASTAttackMetadata;
import com.dev.mythiccore.library.ASTProjectileAttackMetadata;
import com.dev.mythiccore.listener.events.attack.PlayerAttackEvent;
import com.dev.mythiccore.utils.ConfigLoader;
import com.dev.mythiccore.utils.StatCalculation;
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

        String damage_formula = ConfigLoader.getDefaultDamageCalculation();

        if (event.getAttack() instanceof ASTAttackMetadata astAttack) {
            if (astAttack.getAttackSource().equals(AttackSource.REACTION)) return;
            damage_formula = astAttack.getDamageCalculation();
        }
        if (event.getAttack() instanceof ASTProjectileAttackMetadata astAttack) {
            if (astAttack.getAttackSource().equals(AttackSource.REACTION)) return;
            damage_formula = astAttack.getDamageCalculation();
        }

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
            packet.setValue(StatCalculation.getFinalDamage(event.getAttacker(), victim.getUniqueId(), damage_formula, packet, isCritical));
        }
    }
}
