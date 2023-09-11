package com.dev.mythiccore.listener;

import com.dev.mythiccore.listener.events.MiscAttackEvent;
import com.dev.mythiccore.listener.events.MobAttackEvent;
import com.dev.mythiccore.stats.provider.ASTEntityStatProvider;
import io.lumine.mythic.lib.api.event.AttackEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * The class to call "MiscAttackEvent" and "MobAttackEvent"
 */
public class AttackEventListener implements Listener {

    @EventHandler(
            priority = EventPriority.HIGH,
            ignoreCancelled = true
    )
    public void registerEvents(AttackEvent event) {
        if (!event.getAttack().isPlayer()) {
            if (event.getAttack().getAttacker() instanceof ASTEntityStatProvider statProvider) {
                EntityDamageByEntityEvent e = new EntityDamageByEntityEvent(statProvider.getEntity(), event.getEntity(), event.toBukkit().getCause(), event.getDamage().getDamage());
                Bukkit.getPluginManager().callEvent(new MobAttackEvent(e, event.getAttack()));
            } else {
                if (event.toBukkit() instanceof EntityDamageByEntityEvent e) {
                    Bukkit.getPluginManager().callEvent(new MobAttackEvent(e, event.getAttack()));
                } else {
                    Bukkit.getPluginManager().callEvent(new MiscAttackEvent(event.toBukkit(), event.getAttack()));
                }
            }
        }
    }
}
