package com.dev.mythiccore.events.attack_handle.deal_damage;

import com.dev.mythiccore.enums.AttackSource;
import com.dev.mythiccore.library.attackMetadata.AstAttackMeta;
import com.dev.mythiccore.listener.events.attack.PlayerAttackEvent;
import com.dev.mythiccore.utils.ConfigLoader;
import com.dev.mythiccore.utils.StatCalculation;
import io.lumine.mythic.lib.damage.DamagePacket;
import io.lumine.mythic.lib.player.PlayerMetadata;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Random;

/**
 * This class use to deal damage from Player -> Mob or Player
 */
public class PlayerAttack implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerAttack(PlayerAttackEvent event) {

        String damage_formula = ConfigLoader.getDefaultDamageCalculation();
        double talent_percent = 100;
        String weaponType = "NONE";

        if (event.getAttack() instanceof AstAttackMeta astAttack) {
            if (!astAttack.calculate()) return;
            damage_formula = astAttack.getDamageCalculation();
            weaponType = astAttack.getWeaponType();

            if (astAttack.getAttackSource().equals(AttackSource.SKILL) || astAttack.getAttackSource().equals(AttackSource.MYTHIC_SKILL)) {
                talent_percent = astAttack.getTalentPercent();
            }
        }

        LivingEntity victim = event.getEntity();

        double weaponBonus = weaponType.equals("NONE") ? 0 : event.getAttacker().getStat("AST_WEAPON_BONUS_"+weaponType);

        // loop all elemental type damage
        for (DamagePacket packet : event.getDamage().getPackets()) {

            // working only damage that have element (include physical damage)
            if (packet.getElement() == null) {
                event.getDamage().getPackets().remove(packet);
                continue;
            }

            if (event.getAttack() instanceof AstAttackMeta astAttack && astAttack.getAttackSource().equals(AttackSource.NORMAL)) {
                talent_percent = event.getAttacker().getStat("AST_"+packet.getElement().getId()+"_PERCENT");
                if (talent_percent == 0) talent_percent = 100;
            }

            PlayerMetadata attackerStats = event.getAttacker();
            double AttackerCRITRate = Math.max(Math.min(attackerStats.getStat("AST_CRITICAL_RATE"), 100), 0);
            boolean isCritical = new Random().nextDouble() < AttackerCRITRate / 100;
            if (isCritical) event.getDamage().registerElementalCriticalStrike(packet.getElement());

            double damage = StatCalculation.getFinalDamage(event.getAttacker(), victim.getUniqueId(), damage_formula, talent_percent, packet, isCritical, weaponBonus);
            packet.setValue(damage);
        }
    }
}
