package com.dev.mythiccore.events.attack_handle;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.combat.Combat;
import com.dev.mythiccore.enums.MobType;
import com.dev.mythiccore.library.ASTAttackMetadata;
import com.dev.mythiccore.library.ASTProjectileAttackMetadata;
import com.dev.mythiccore.library.AttackSource;
import com.dev.mythiccore.listener.events.attack.MiscAttackEvent;
import com.dev.mythiccore.listener.events.attack.MobAttackEvent;
import com.dev.mythiccore.listener.events.attack.PlayerAttackEvent;
import com.dev.mythiccore.reaction.ElementalReaction;
import com.dev.mythiccore.reaction.reaction_type.DoubleAuraReaction;
import com.dev.mythiccore.reaction.reaction_type.TriggerAuraReaction;
import com.dev.mythiccore.utils.ConfigLoader;
import com.dev.mythiccore.utils.Utils;
import io.lumine.mythic.lib.api.stat.provider.StatProvider;
import io.lumine.mythic.lib.damage.DamagePacket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.*;

public class TriggerReaction implements Listener {

    private static final Map<LivingEntity, Set<DoubleAuraReaction>> reactionTasks = new HashMap<>();
    private static final Map<LivingEntity, Map<DoubleAuraReaction, StatProvider>> statProviderMap = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void playerAttack(PlayerAttackEvent event) {

        double gauge_unit;
        String decay_rate;
        String cooldown_source;
        long internal_cooldown;

        if (event.getAttack() instanceof ASTAttackMetadata astAttack && astAttack.getAttackSource() == AttackSource.REACTION) return;
        if (event.getAttack() instanceof ASTProjectileAttackMetadata astAttack && astAttack.getAttackSource() == AttackSource.REACTION) return;

        if (event.getAttack() instanceof ASTAttackMetadata astAttack) {
            gauge_unit = astAttack.getGaugeUnit();
            decay_rate = astAttack.getDecayRate();
            cooldown_source = astAttack.getInternalCooldownSource();
            internal_cooldown = astAttack.getInternalCooldown();
        } else if (event.getAttack() instanceof ASTProjectileAttackMetadata astAttack) {
            gauge_unit = astAttack.getGaugeUnit();
            decay_rate = astAttack.getDecayRate();
            cooldown_source = astAttack.getInternalCooldownSource();
            internal_cooldown = astAttack.getInternalCooldown();
        } else {
            return;
        }

        if (MythicCore.getCooldownManager().getCooldown(event.getAttacker().getPlayer().getUniqueId()).getCooldown(event.getEntity(), cooldown_source) > 0) return;

        for (DamagePacket packet : event.getDamage().getPackets()) {
            if (packet.getElement() == null) continue;

            if (ConfigLoader.getAuraWhitelist().contains(packet.getElement().getId())) MythicCore.getAuraManager().getAura(event.getEntity().getUniqueId()).addAura(packet.getElement().getId(), gauge_unit, decay_rate);
            boolean reaction_respond = triggerReactions(packet, gauge_unit, decay_rate, event.getEntity(), event.getAttacker().getPlayer(), event.getAttack().getAttacker(), event.toBukkit().getCause());

            if ((gauge_unit > 0 && ConfigLoader.getAuraWhitelist().contains(packet.getElement().getId())) || reaction_respond) {
                MythicCore.getCooldownManager().getCooldown(event.getAttacker().getPlayer().getUniqueId()).setCooldown(event.getEntity(), cooldown_source, internal_cooldown);
            }

        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void mobAttack(MobAttackEvent event) {

        double gauge_unit;
        String decay_rate;
        String cooldown_source;
        long internal_cooldown;

        if (event.getAttack() instanceof ASTAttackMetadata astAttack && astAttack.getAttackSource() == AttackSource.REACTION) return;
        if (event.getAttack() instanceof ASTProjectileAttackMetadata astAttack && astAttack.getAttackSource() == AttackSource.REACTION) return;

        if (event.getAttack() instanceof ASTAttackMetadata astAttack) {
            gauge_unit = astAttack.getGaugeUnit();
            decay_rate = astAttack.getDecayRate();
            cooldown_source = astAttack.getInternalCooldownSource();
            internal_cooldown = astAttack.getInternalCooldown();
        } else if (event.getAttack() instanceof ASTProjectileAttackMetadata astAttack) {
            gauge_unit = astAttack.getGaugeUnit();
            decay_rate = astAttack.getDecayRate();
            cooldown_source = astAttack.getInternalCooldownSource();
            internal_cooldown = astAttack.getInternalCooldown();
        } else {
            return;
        }

        if (MythicCore.getCooldownManager().getCooldown(event.getDamager().getUniqueId()).getCooldown(event.getEntity(), cooldown_source) > 0) return;

        for (DamagePacket packet : event.getDamage().getPackets()) {
            if (packet.getElement() == null) continue;

            if (ConfigLoader.getAuraWhitelist().contains(packet.getElement().getId())) MythicCore.getAuraManager().getAura(event.getEntity().getUniqueId()).addAura(packet.getElement().getId(), gauge_unit, decay_rate);
            boolean reaction_respond = triggerReactions(packet, gauge_unit, decay_rate, event.getEntity(), event.getDamager(), event.getAttack().getAttacker(), event.toBukkit().getCause());

            if ((gauge_unit > 0 && ConfigLoader.getAuraWhitelist().contains(packet.getElement().getId())) || reaction_respond) {
                MythicCore.getCooldownManager().getCooldown(event.getDamager().getUniqueId()).setCooldown(event.getEntity(), cooldown_source, internal_cooldown);
            }

        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void miscAttack(MiscAttackEvent event) {
        for (DamagePacket packet : event.getDamage().getPackets()) {
            if (packet.getElement() == null) continue;

            if (ConfigLoader.getAuraWhitelist().contains(packet.getElement().getId())) MythicCore.getAuraManager().getAura(event.getEntity().getUniqueId()).addAura(packet.getElement().getId(), ConfigLoader.getDefaultGaugeUnit(), ConfigLoader.getDefaultDecayRate());
            triggerReactions(packet, ConfigLoader.getDefaultGaugeUnit(), ConfigLoader.getDefaultDecayRate(), event.getEntity(), null, s -> 0, event.toBukkit().getCause());
        }
    }

    public static boolean triggerReactions(DamagePacket damage, double gauge_unit, String decay_rate, LivingEntity entity, Entity damager, StatProvider statProvider, EntityDamageEvent.DamageCause damage_cause) {
        if (damage.getElement() == null) return false;

        boolean reaction_success = false;
        List<String> reaction_ids = ConfigLoader.getReactionPriorityList(damage.getElement().getId());
        for (String reaction_id : reaction_ids) {

            if (!MythicCore.getReactionManager().getElementalReactions().containsKey(reaction_id)) continue;
            ElementalReaction rawReaction = MythicCore.getReactionManager().getElementalReactions().get(reaction_id);

            if (rawReaction instanceof TriggerAuraReaction reaction) {
                if (MythicCore.getAuraManager().getAura(entity.getUniqueId()).getMapAura().containsKey(rawReaction.getAura()) && damage.getElement().getId().equals(rawReaction.getTrigger())) {
                    if (!reaction.getDisplay().equals("")) Utils.displayIndicator(reaction.getDisplay(), entity);
                    reaction.t(damage, gauge_unit, decay_rate, entity, damager, statProvider, damage_cause);
                    reaction_success = true;
                }
            } else if (rawReaction instanceof DoubleAuraReaction reaction) {
                if (reaction.getAura().equals(damage.getElement().getId()) || reaction.getTrigger().equals(damage.getElement().getId())) {
                    if (MythicCore.getAuraManager().getAura(entity.getUniqueId()).getMapAura().containsKey(rawReaction.getAura()) && MythicCore.getAuraManager().getAura(entity.getUniqueId()).getMapAura().containsKey(rawReaction.getTrigger())) {

                        if (!statProviderMap.containsKey(entity)) {
                            statProviderMap.put(entity, new HashMap<>(Map.of(reaction, statProvider)));
                        } else {
                            statProviderMap.get(entity).put(reaction, statProvider);
                        }

                        // if already have task
                        if (reactionTasks.containsKey(entity) && reactionTasks.get(entity).contains(reaction))
                            return false;

                        // display indicator
                        if (!reaction.getDisplay().equals(""))
                            Utils.displayIndicator(reaction.getDisplay(), entity);

                        if (!reactionTasks.containsKey(entity)) {
                            reactionTasks.put(entity, new HashSet<>(Set.of(reaction)));
                        } else {
                            reactionTasks.get(entity).add(reaction);
                        }

                        MobType last_mob_type = MobType.NULL;
                        if (damager != null) {
                            last_mob_type = Combat.getLastMobType(damager);
                        }

                        reaction_success = true;

                        MobType finalLast_mob_type = last_mob_type;
                        Bukkit.getScheduler().runTaskTimerAsynchronously(MythicCore.getInstance(), (task) -> {
                            if (MythicCore.getAuraManager().getAura(entity.getUniqueId()).getMapAura().containsKey(rawReaction.getAura()) && MythicCore.getAuraManager().getAura(entity.getUniqueId()).getMapAura().containsKey(rawReaction.getTrigger())) {

                                Bukkit.getScheduler().runTask(MythicCore.getInstance(), () -> reaction.t(damage, gauge_unit, decay_rate, entity, damager, statProviderMap.get(entity).get(reaction), damage_cause, finalLast_mob_type));

                            } else {

                                if (reactionTasks.containsKey(entity)) {
                                    reactionTasks.get(entity).remove(reaction);
                                    if (reactionTasks.get(entity).isEmpty()) reactionTasks.remove(entity);
                                }

                                if (statProviderMap.containsKey(entity)) {
                                    statProviderMap.get(entity).remove(reaction);
                                    if (statProviderMap.get(entity).isEmpty()) statProviderMap.remove(entity);
                                }

                                task.cancel();
                            }
                        }, 3, reaction.getFrequency());
                    }
                }
            }

        }

        return reaction_success;
    }
}
