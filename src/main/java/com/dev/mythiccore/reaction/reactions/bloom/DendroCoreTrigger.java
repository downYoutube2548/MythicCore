package com.dev.mythiccore.reaction.reactions.bloom;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.enums.AttackSource;
import com.dev.mythiccore.library.ASTAttackMetadata;
import com.dev.mythiccore.library.ASTProjectileAttackMetadata;
import com.dev.mythiccore.listener.events.attack.MiscAttackEvent;
import com.dev.mythiccore.listener.events.attack.MobAttackEvent;
import com.dev.mythiccore.listener.events.attack.PlayerAttackEvent;
import com.dev.mythiccore.utils.ConfigLoader;
import com.dev.mythiccore.utils.Utils;
import io.lumine.mythic.lib.api.stat.provider.StatProvider;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.damage.DamagePacket;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DendroCoreTrigger implements Listener {

    @EventHandler
    public void dendroCore(PlayerAttackEvent event) {

        if (event.getAttack() instanceof ASTAttackMetadata astAttack && astAttack.getAttackSource() == AttackSource.REACTION) return;
        if (event.getAttack() instanceof ASTProjectileAttackMetadata astAttack && astAttack.getAttackSource() == AttackSource.REACTION) return;

        trigger(event.getDamage(), event.getEntity(), event.getAttacker().getPlayer(), event.getAttacker(), event.toBukkit().getCause());

    }

    @EventHandler
    public void dendroCore(MobAttackEvent event) {

        if (event.getAttack() instanceof ASTAttackMetadata astAttack && astAttack.getAttackSource() == AttackSource.REACTION) return;
        if (event.getAttack() instanceof ASTProjectileAttackMetadata astAttack && astAttack.getAttackSource() == AttackSource.REACTION) return;

        trigger(event.getDamage(), event.getEntity(), event.getDamager(), event.getAttack().getAttacker(), event.toBukkit().getCause());

    }

    @EventHandler
    public void dendroCore(MiscAttackEvent event) {

        trigger(event.getDamage(), event.getEntity(), null, s -> 0, event.toBukkit().getCause());

    }

    public void trigger(DamageMetadata damage, LivingEntity entity, @Nullable Entity damager, StatProvider stats, EntityDamageEvent.DamageCause damageCause) {

        double trigger_radius = ConfigLoader.getReactionConfig().getDouble("BLOOM.dendro-core-trigger-radius");
        List<Entity> entities = entity.getNearbyEntities(trigger_radius, trigger_radius, trigger_radius);
        for (Entity nearbyEntity : entities) {

            if (nearbyEntity.hasMetadata("AST_DENDRO_CORE_ENTITY") && !nearbyEntity.hasMetadata("AST_DENDRO_CORE_TRIGGERED")) {

                DendroCore dendroCore = (DendroCore) nearbyEntity.getMetadata("AST_DENDRO_CORE_ENTITY").get(0).value();
                assert dendroCore != null;

                for (DamagePacket packet : damage.getPackets()) {
                    if (packet.getElement() == null) continue;

                    for (DendroCoreReaction reaction : MythicCore.getReactionManager().getDendroCoreReactions().values()) {
                        if (reaction.getTrigger().equals(packet.getElement().getId())) {
                            nearbyEntity.setMetadata("AST_DENDRO_CORE_TRIGGERED", new FixedMetadataValue(MythicCore.getInstance(), true));
                            dendroCore.remove(true);

                            if (!reaction.getDisplay().equals("")) Utils.displayIndicator(reaction.getDisplay(), dendroCore.getDendroCore());

                            reaction.trigger(dendroCore, entity, damager, stats, damageCause);
                        }
                    }
                }
            }
        }
    }
}
