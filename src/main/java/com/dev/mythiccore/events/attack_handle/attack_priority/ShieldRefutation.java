package com.dev.mythiccore.events.attack_handle.attack_priority;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.buff.buffs.ElementalShield;
import com.dev.mythiccore.visuals.ASTDamageIndicators;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.event.AttackEvent;
import io.lumine.mythic.lib.damage.DamagePacket;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ShieldRefutation implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void attack(AttackEvent event) {

        for (DamagePacket packet : event.getDamage().getPackets()) {
            if (packet.getElement() == null) continue;

            ElementalShield shield = MythicCore.getBuffManager().getBuff(event.getEntity().getUniqueId()).getActivateBuff(ElementalShield.class, new String[]{"element"}, new String[]{packet.getElement().getId()});
            if (shield == null) continue;
            double shield_attack;

            if (shield.getAmount() > packet.getValue()) {
                shield_attack = packet.getValue();
                shield.setAmount(shield.getAmount() - packet.getValue());
                packet.setValue(0);
            } else if (shield.getAmount() < packet.getValue()) {
                shield_attack = shield.getAmount();
                MythicCore.getBuffManager().getBuff(event.getEntity().getUniqueId()).removeBuff(shield.getUniqueId());
                packet.setValue(packet.getValue() - shield.getAmount());
            } else {
                shield_attack = shield.getAmount();
                MythicCore.getBuffManager().getBuff(event.getEntity().getUniqueId()).removeBuff(shield.getUniqueId());
                packet.setValue(0);
            }

            Entity entity = event.getEntity();

            if (!(entity instanceof Player) || !UtilityMethods.isVanished((Player)entity)) {
                ConfigurationSection config = MythicCore.getInstance().getConfig().getConfigurationSection("Indicators");
                ASTDamageIndicators indicators = new ASTDamageIndicators(config);
                assert config != null;
                String format = config.getString("shield-attack-format");
                assert format != null;
                indicators.displayIndicator(entity, indicators.computeFormat(shield_attack, false, format, packet.getElement()), indicators.getDirection(event.toBukkit()), io.lumine.mythic.lib.api.event.IndicatorDisplayEvent.IndicatorType.DAMAGE);
            }
        }
    }
}
