package com.dev.mythiccore.events.attack_handle.deal_damage;

import com.dev.mythiccore.library.ASTAttackMetadata;
import com.dev.mythiccore.library.ASTProjectileAttackMetadata;
import com.dev.mythiccore.library.AttackSource;
import com.dev.mythiccore.listener.events.attack.MobAttackEvent;
import com.dev.mythiccore.utils.StatCalculation;
import io.lumine.mythic.lib.damage.DamagePacket;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * This class use to deal damage from Mob -> Mob or Player
 */
public class MobAttack implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onMobAttack(MobAttackEvent event) {

        try {

            if (event.getAttack() instanceof ASTAttackMetadata astAttack && astAttack.getAttackSource().equals(AttackSource.REACTION)) return;
            if (event.getAttack() instanceof ASTProjectileAttackMetadata astAttack && astAttack.getAttackSource().equals(AttackSource.REACTION)) return;

            LivingEntity victim = event.getEntity();

            for (DamagePacket packet : event.getDamage().getPackets()) {

                // working only damage that have element (include physical damage)
                if (packet.getElement() == null) {
                    packet.setValue(0);
                    continue;
                }

                packet.setValue(StatCalculation.getFinalDamage(event.getAttack().getAttacker(), victim.getUniqueId(), packet, false));
            }
        } catch (NullPointerException ignored) {}
    }
}
