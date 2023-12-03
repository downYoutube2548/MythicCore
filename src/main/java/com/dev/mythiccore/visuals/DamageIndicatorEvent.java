package com.dev.mythiccore.visuals;

import com.dev.mythiccore.MythicCore;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.event.AttackEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DamageIndicatorEvent implements Listener {

    @EventHandler(
            priority = EventPriority.MONITOR,
            ignoreCancelled = true
    )
    public void a(AttackEvent event) {

        ConfigurationSection config = MythicCore.getInstance().getConfig().getConfigurationSection("Indicators");
        ASTDamageIndicators astDamageIndicators = new ASTDamageIndicators(config);

        if (!astDamageIndicators.enable) return;

        Entity entity = event.getEntity();

        if (!(entity instanceof Player) || !UtilityMethods.isVanished((Player)entity)) {
            List<String> holos = new ArrayList<>();
            Map<ASTDamageIndicators.IndicatorType, Double> mappedDamage = astDamageIndicators.mapDamage(event.getDamage());
            mappedDamage.forEach((type, val) -> {
                if (!(val < 0.02) && type.element != null) {
                    String s = (type.crit) ? astDamageIndicators.crit_format : astDamageIndicators.format;
                    assert s != null;
                    holos.add(astDamageIndicators.computeFormat(val, type.crit, s, type.element));
                }
            });
            if (astDamageIndicators.splitHolograms) {

                for (String holo : holos) {
                    astDamageIndicators.displayIndicator(entity, holo, astDamageIndicators.getDirection(event.toBukkit()), io.lumine.mythic.lib.api.event.IndicatorDisplayEvent.IndicatorType.DAMAGE);
                }
            } else {
                String joined = String.join(" ", holos);
                astDamageIndicators.displayIndicator(entity, joined, astDamageIndicators.getDirection(event.toBukkit()), io.lumine.mythic.lib.api.event.IndicatorDisplayEvent.IndicatorType.DAMAGE);
            }
        }
    }
}
