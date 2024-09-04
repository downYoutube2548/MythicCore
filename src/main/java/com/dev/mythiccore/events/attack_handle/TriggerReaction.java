package com.dev.mythiccore.events.attack_handle;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.combat.Combat;
import com.dev.mythiccore.enums.AttackSource;
import com.dev.mythiccore.enums.MobType;
import com.dev.mythiccore.enums.ReactionResponse;
import com.dev.mythiccore.library.attackMetadata.AstAttackMeta;
import com.dev.mythiccore.listener.events.MakeReactionEvent;
import com.dev.mythiccore.listener.events.attack.MiscAttackEvent;
import com.dev.mythiccore.listener.events.attack.MobAttackEvent;
import com.dev.mythiccore.listener.events.attack.PlayerAttackEvent;
import com.dev.mythiccore.listener.events.aura.ReactionTriggerEvent;
import com.dev.mythiccore.reaction.ElementalReaction;
import com.dev.mythiccore.reaction.reaction_type.DoubleAuraReaction;
import com.dev.mythiccore.reaction.reaction_type.SingleReaction;
import com.dev.mythiccore.reaction.reaction_type.TriggerAuraReaction;
import com.dev.mythiccore.utils.ConfigLoader;
import com.dev.mythiccore.utils.Utils;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.lib.api.stat.provider.StatProvider;
import io.lumine.mythic.lib.damage.AttackMetadata;
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

        if (event.getAttack() instanceof AstAttackMeta astAttack && !astAttack.isTriggerReaction()) return;

        if (event.getAttack() instanceof AstAttackMeta astAttack) {
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

            ReactionTriggerEvent reactionTriggerEvent = new ReactionTriggerEvent(event.getAttacker().getPlayer().getUniqueId());
            Bukkit.getPluginManager().callEvent(reactionTriggerEvent);

            if (reactionTriggerEvent.isCancelled()) continue;

            ReactionResponse reaction_respond = triggerReactions(event.getAttack(), packet, gauge_unit, decay_rate, event.getEntity(), event.getAttacker().getPlayer(), event.getAttack().getAttacker(), event.toBukkit().getCause());

            if ((gauge_unit > 0 && ConfigLoader.getAuraWhitelist().contains(packet.getElement().getId())) || reaction_respond != ReactionResponse.NONE) {
                MythicCore.getCooldownManager().getCooldown(event.getAttacker().getPlayer().getUniqueId()).setCooldown(event.getEntity(), cooldown_source, internal_cooldown);
            }

            if (reaction_respond == ReactionResponse.NONE || reaction_respond == ReactionResponse.DOUBLE_REACTION) {
                if (ConfigLoader.getAuraWhitelist().contains(packet.getElement().getId()))
                    MythicCore.getAuraManager().getAura(event.getEntity().getUniqueId()).addAura(packet.getElement().getId(), gauge_unit, decay_rate);
            }

        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void mobAttack(MobAttackEvent event) {

        //Bukkit.broadcastMessage(ChatColor.YELLOW+"TriggerReaction.MobAttackEvent.Damager = "+event.getDamager().getName());

        double gauge_unit;
        String decay_rate;
        String cooldown_source;
        long internal_cooldown;

        if (event.getAttack() instanceof AstAttackMeta astAttack && !astAttack.isTriggerReaction()) return;

        if (event.getAttack() instanceof AstAttackMeta astAttack) {
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

            ReactionTriggerEvent reactionTriggerEvent = new ReactionTriggerEvent(event.getDamager().getUniqueId());
            Bukkit.getPluginManager().callEvent(reactionTriggerEvent);

            if (reactionTriggerEvent.isCancelled()) continue;

            ReactionResponse reaction_respond = triggerReactions(event.getAttack(), packet, gauge_unit, decay_rate, event.getEntity(), event.getDamager(), event.getAttack().getAttacker(), event.toBukkit().getCause());

            if ((gauge_unit > 0 && ConfigLoader.getAuraWhitelist().contains(packet.getElement().getId())) || reaction_respond != ReactionResponse.NONE) {
                MythicCore.getCooldownManager().getCooldown(event.getDamager().getUniqueId()).setCooldown(event.getEntity(), cooldown_source, internal_cooldown);
            }

            if (reaction_respond == ReactionResponse.NONE || reaction_respond == ReactionResponse.DOUBLE_REACTION) {
                if (ConfigLoader.getAuraWhitelist().contains(packet.getElement().getId()))
                    MythicCore.getAuraManager().getAura(event.getEntity().getUniqueId()).addAura(packet.getElement().getId(), gauge_unit, decay_rate);
            }

        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void miscAttack(MiscAttackEvent event) {
        for (DamagePacket packet : event.getDamage().getPackets()) {
            if (packet.getElement() == null) continue;

            ReactionTriggerEvent reactionTriggerEvent = new ReactionTriggerEvent(null);
            Bukkit.getPluginManager().callEvent(reactionTriggerEvent);

            if (reactionTriggerEvent.isCancelled()) continue;

            ReactionResponse reaction_respond = triggerReactions(event.getAttack(), packet, ConfigLoader.getDefaultGaugeUnit(), ConfigLoader.getDefaultDecayRate(), event.getEntity(), null, s -> 0, event.toBukkit().getCause());

            if (reaction_respond == ReactionResponse.NONE || reaction_respond == ReactionResponse.DOUBLE_REACTION) {
                if (ConfigLoader.getAuraWhitelist().contains(packet.getElement().getId()))
                    MythicCore.getAuraManager().getAura(event.getEntity().getUniqueId()).addAura(packet.getElement().getId(), ConfigLoader.getDefaultGaugeUnit(), ConfigLoader.getDefaultDecayRate());
            }
        }
    }

    public static ReactionResponse triggerReactions(AttackMetadata attack, DamagePacket damage, double gauge_unit, String decay_rate, LivingEntity entity, Entity damager, StatProvider statProvider, EntityDamageEvent.DamageCause damage_cause) {
        if (damage.getElement() == null) return ReactionResponse.NONE;

        ReactionResponse reaction_success = ReactionResponse.NONE;
        List<String> reaction_ids = ConfigLoader.getReactionPriorityList(damage.getElement().getId());
        for (String reaction_id : reaction_ids) {

            if (!MythicCore.getReactionManager().getElementalReactions().containsKey(reaction_id)) continue;
            ElementalReaction rawReaction = MythicCore.getReactionManager().getElementalReactions().get(reaction_id);

            if (rawReaction instanceof SingleReaction reaction) {
                if (MythicCore.getAuraManager().getAura(entity.getUniqueId()).getMapAura().containsKey(reaction.getTrigger()) && damage.getElement().getId().equals(reaction.getTrigger())) {
                    if (!reaction.getDisplay().equals("")) Utils.displayIndicator(reaction.getDisplay(), entity);
                    reaction.t(damage, gauge_unit, decay_rate, entity, damager, statProvider, damage_cause);
                    reaction_success = ReactionResponse.SINGLE_REACTION;

                    Bukkit.getPluginManager().callEvent(new MakeReactionEvent(damager, rawReaction));

                    if (attack instanceof AstAttackMeta astAttackMeta && astAttackMeta.getAttackSource().equals(AttackSource.MYTHIC_SKILL)) {
                        astAttackMeta.getMetadata("SKILL_METADATA").ifPresent(o -> ((SkillMetadata) o).setMetadata("REACTION_SUCCESS_" + reaction_id, true));
                    }
                }
            }
            else if (rawReaction instanceof TriggerAuraReaction reaction) {
                if (MythicCore.getAuraManager().getAura(entity.getUniqueId()).getMapAura().containsKey(reaction.getAura()) && damage.getElement().getId().equals(reaction.getTrigger())) {
                    if (!reaction.getDisplay().equals("")) Utils.displayIndicator(reaction.getDisplay(), entity);
                    reaction.t(damage, gauge_unit, decay_rate, entity, damager, statProvider, damage_cause);
                    reaction_success = ReactionResponse.TRIGGER_REACTION;

                    Bukkit.getPluginManager().callEvent(new MakeReactionEvent(damager, rawReaction));

                    if (attack instanceof AstAttackMeta astAttackMeta && astAttackMeta.getAttackSource().equals(AttackSource.MYTHIC_SKILL)) {
                        astAttackMeta.getMetadata("SKILL_METADATA").ifPresent(o -> ((SkillMetadata) o).setMetadata("REACTION_SUCCESS_" + reaction_id, true));
                    }
                }
            } else if (rawReaction instanceof DoubleAuraReaction reaction) {
                if ((reaction.getFirstAura().equals(damage.getElement().getId()) && MythicCore.getAuraManager().getAura(entity.getUniqueId()).getMapAura().containsKey(reaction.getSecondAura())) || (reaction.getSecondAura().equals(damage.getElement().getId()) && MythicCore.getAuraManager().getAura(entity.getUniqueId()).getMapAura().containsKey(reaction.getFirstAura()))) {

                    if (!statProviderMap.containsKey(entity)) {
                        statProviderMap.put(entity, new HashMap<>(Map.of(reaction, statProvider)));
                    } else {
                        statProviderMap.get(entity).put(reaction, statProvider);
                    }

                    // if already have task
                    if (reactionTasks.containsKey(entity) && reactionTasks.get(entity).contains(reaction))
                        return ReactionResponse.NONE;

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

                    reaction_success = ReactionResponse.DOUBLE_REACTION;

                    Bukkit.getPluginManager().callEvent(new MakeReactionEvent(damager, rawReaction));

                    if (attack instanceof AstAttackMeta astAttackMeta && astAttackMeta.getAttackSource().equals(AttackSource.MYTHIC_SKILL)) {
                        astAttackMeta.getMetadata("SKILL_METADATA").ifPresent(o -> ((SkillMetadata) o).setMetadata("REACTION_SUCCESS_" + reaction_id, true));
                    }

                    MobType finalLast_mob_type = last_mob_type;
                    Bukkit.getScheduler().runTaskTimerAsynchronously(MythicCore.getInstance(), (task) -> {
                        if (MythicCore.getAuraManager().getAura(entity.getUniqueId()).getMapAura().containsKey(reaction.getFirstAura()) && MythicCore.getAuraManager().getAura(entity.getUniqueId()).getMapAura().containsKey(reaction.getSecondAura())) {

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

        return reaction_success;
    }
}
