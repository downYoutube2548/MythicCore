package com.dev.mythiccore.events.attack_handle.deal_damage;

import com.dev.mythiccore.enums.AttackSource;
import com.dev.mythiccore.library.attackMetadata.ASTAttackMetadata;
import com.dev.mythiccore.library.attackMetadata.ASTProjectileAttackMetadata;
import com.dev.mythiccore.library.attackMetadata.AstAttackMeta;
import com.dev.mythiccore.listener.events.attack.MobAttackEvent;
import com.dev.mythiccore.utils.ConfigLoader;
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

    @EventHandler(priority = EventPriority.HIGH)
    public void onMobAttack(MobAttackEvent event) {

        try {

            String damage_formula = ConfigLoader.getDefaultDamageCalculation();
            double talent_percent = 100;

            if (event.getAttack() instanceof AstAttackMeta astAttack) {
                if (astAttack.getAttackSource().equals(AttackSource.REACTION)) return;
                damage_formula = astAttack.getDamageCalculation();

                if (astAttack.getAttackSource().equals(AttackSource.SKILL) || astAttack.getAttackSource().equals(AttackSource.MYTHIC_SKILL)) {
                    talent_percent = astAttack.getTalentPercent();
                }
            }

            LivingEntity victim = event.getEntity();



            for (DamagePacket packet : event.getDamage().getPackets()) {

                // working only damage that have element (include physical damage)
                if (packet.getElement() == null) {
                    event.getDamage().getPackets().remove(packet);
                    continue;
                }

                double damage = StatCalculation.getFinalDamage(event.getAttack().getAttacker(), victim.getUniqueId(), damage_formula, talent_percent, packet, false);
                //Bukkit.broadcastMessage(damage+"");
                packet.setValue(damage);
            }
        } catch (NullPointerException ignored) {}
    }
}
