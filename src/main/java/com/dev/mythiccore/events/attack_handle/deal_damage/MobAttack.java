package com.dev.mythiccore.events.attack_handle.deal_damage;

import com.dev.mythiccore.listener.events.MiscAttackEvent;
import com.dev.mythiccore.listener.events.MobAttackEvent;
import com.dev.mythiccore.utils.StatCalculation;
import io.lumine.mythic.lib.damage.DamagePacket;
import io.lumine.mythic.lib.damage.DamageType;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Arrays;

/**
 * This class use to deal damage from Mob -> Mob or Player
 */
public class MobAttack implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onMobAttack(MobAttackEvent event) {

        try {

            LivingEntity attacker;
            Entity damager = event.getDamager();

            if (damager instanceof Projectile projectile) {

                // return if damager is Thrown Potion
                if (projectile instanceof ThrownPotion) {
                    new MiscAttack().onMiscAttack(new MiscAttackEvent(event.toBukkit(), event.getAttack()));
                    return;
                }
                // return if shooter is not living entity
                if (!(projectile.getShooter() instanceof LivingEntity)) {
                    new MiscAttack().onMiscAttack(new MiscAttackEvent(event.toBukkit(), event.getAttack()));
                    return;
                }

                attacker = (LivingEntity) projectile.getShooter();

            } else if (damager instanceof AreaEffectCloud) {

                new MiscAttack().onMiscAttack(new MiscAttackEvent(event.toBukkit(), event.getAttack()));
                return;

            } else if (damager instanceof LivingEntity) {

                attacker = (LivingEntity) event.getDamager();

            } else {
                return;
            }

            LivingEntity victim = event.getEntity();

            for (DamagePacket packet : event.getDamage().getPackets()) {


                // get Vi Mob and check if it is mythic mob or regular mob
                //ActiveMob attackerMythicMob = MythicBukkit.inst().getMobManager().getActiveMob(attacker.getUniqueId()).orElse(null);

                if (Arrays.asList(packet.getTypes()).contains(DamageType.DOT) || Arrays.asList(packet.getTypes()).contains(DamageType.MINION)) continue;

                // working only damage that have element (include physical damage)
                if (packet.getElement() == null) {
                    packet.setValue(0);
                    continue;
                }

                packet.setValue(StatCalculation.getFinalDamage(attacker.getUniqueId(), victim.getUniqueId(), packet, false));
            }
        } catch (NullPointerException ignored) {}
    }
}
