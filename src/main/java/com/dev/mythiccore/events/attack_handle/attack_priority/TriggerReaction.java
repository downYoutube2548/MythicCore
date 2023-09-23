package com.dev.mythiccore.events.attack_handle.attack_priority;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.combat.Combat;
import com.dev.mythiccore.library.ASTAttackMetadata;
import com.dev.mythiccore.library.ASTProjectileAttackMetadata;
import com.dev.mythiccore.library.AttackSource;
import com.dev.mythiccore.library.SnapshotStats;
import com.dev.mythiccore.listener.events.MiscAttackEvent;
import com.dev.mythiccore.listener.events.MobAttackEvent;
import com.dev.mythiccore.listener.events.PlayerAttackEvent;
import com.dev.mythiccore.reaction.ElementalReaction;
import com.dev.mythiccore.reaction.reaction_type.DoubleAuraReaction;
import com.dev.mythiccore.reaction.reaction_type.TriggerAuraReaction;
import com.dev.mythiccore.utils.ConfigLoader;
import com.dev.mythiccore.utils.Utils;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.lib.api.stat.StatInstance;
import io.lumine.mythic.lib.damage.DamagePacket;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.*;

public class TriggerReaction implements Listener {

    private static final Map<LivingEntity, Set<DoubleAuraReaction>> reactionTasks = new HashMap<>();
    private static final Map<LivingEntity, Map<DoubleAuraReaction, SnapshotStats>> snapshotStats = new HashMap<>();

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
            boolean reaction_respond = triggerReactions(packet, gauge_unit, decay_rate, event.getEntity(), event.getAttacker().getPlayer(), event.toBukkit().getCause());

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
            boolean reaction_respond = triggerReactions(packet, gauge_unit, decay_rate, event.getEntity(), event.getDamager(), event.toBukkit().getCause());

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
            triggerReactions(packet, ConfigLoader.getDefaultGaugeUnit(), ConfigLoader.getDefaultDecayRate(), event.getEntity(), null, event.toBukkit().getCause());
        }
    }

    /*
    @AttackHandle(priority = 2)
    public void attack(PlayerAttackEvent event) {

        if (event.getAttack() instanceof ASTAttackMetadata astAttack) {
            if (astAttack.getAttackSource() == AttackSource.REACTION) return;
        }

        /*
        for (DamagePacket packet : event.getDamage().getPackets()) {
            if (packet.getElement() == null) continue;

            if (event.getAttack() instanceof ASTAttackMetaData ast_attack_data) {
                if (MythicCore.getCooldownManager().getCooldown(event.getAttacker().getPlayer().getUniqueId()).getCooldown(event.getEntity(), ast_attack_data.getInternalCooldownSource()) > 0) return;
            } else {
                if (MythicCore.getCooldownManager().getCooldown(event.getAttacker().getPlayer().getUniqueId()).getCooldown(event.getEntity(), "default") > 0) return;
            }

            if (Arrays.asList(packet.getTypes()).contains(DamageType.DOT)) continue;
            if (ConfigLoader.getAuraWhitelist().contains(packet.getElement().getId())) MythicCore.getAuraManager().getAura(event.getEntity().getUniqueId()).addAura(packet.getElement().getId(), ConfigLoader.getDefaultGaugeUnit(), ConfigLoader.getDefaultDecayRate());

            boolean reaction_respond = triggerReactions(packet, ConfigLoader.getDefaultGaugeUnit(), ConfigLoader.getDefaultDecayRate(), event.getEntity(), event.getAttacker().getPlayer(), event.toBukkit().getCause());

            if ((ConfigLoader.getDefaultGaugeUnit() > 0 && ConfigLoader.getAuraWhitelist().contains(packet.getElement().getId())) || reaction_respond) {
                if (event.getAttack() instanceof ASTAttackMetaData ast_attack_data) {
                    MythicCore.getCooldownManager().getCooldown(event.getAttacker().getPlayer().getUniqueId()).setCooldown(event.getEntity(), ast_attack_data.getInternalCooldownSource(), ConfigLoader.getInternalCooldown(ast_attack_data.getInternalCooldownSource()));
                } else {
                    MythicCore.getCooldownManager().getCooldown(event.getAttacker().getPlayer().getUniqueId()).setCooldown(event.getEntity(), "default", ConfigLoader.getInternalCooldown("default"));
                }
            }
        }
    }
    */


    /*
    @AttackHandle(priority = 2)
    public void attack(AttackEvent a) {

        try {
            if (!a.getAttack().isPlayer()) {
                EntityDamageByEntityEvent e = null;
                LivingEntity attacker = null;

                if (a.getAttack().getAttacker() instanceof ASTEntityStatProvider statProvider) {
                    e = new EntityDamageByEntityEvent(statProvider.getEntity(), a.getEntity(), a.toBukkit().getCause(), a.getDamage().getDamage());
                } else {
                    if (a.toBukkit() instanceof EntityDamageByEntityEvent b) {
                        e = b;
                    }
                }

                if (e != null) {
                    MobAttackEvent event = new MobAttackEvent(e, a.getAttack());

                    Entity damager = event.getDamager();
                    if (damager instanceof Projectile projectile) {

                        // if damager is not Thrown Potion
                        if (!(projectile instanceof ThrownPotion)) {
                            // if shooter is living entity
                            if (projectile.getShooter() instanceof LivingEntity) {
                                attacker = (LivingEntity) projectile.getShooter();
                            }
                        }

                    } else if (damager instanceof LivingEntity) {

                        attacker = (LivingEntity) event.getDamager();

                    }
                }

                for (DamagePacket packet : a.getDamage().getPackets()) {
                    if (packet.getElement() == null) continue;

                    if (attacker != null) {
                        if (a.getAttack() instanceof ASTAttackMetadata ast_attack_data) {
                            if (MythicCore.getCooldownManager().getCooldown(attacker.getUniqueId()).getCooldown(a.getEntity(), ast_attack_data.getInternalCooldownSource()) > 0) return;
                        } else {
                            if (MythicCore.getCooldownManager().getCooldown(attacker.getUniqueId()).getCooldown(a.getEntity(), "default") > 0) return;
                        }
                    }

                    if (Arrays.asList(packet.getTypes()).contains(DamageType.SKILL) || Arrays.asList(packet.getTypes()).contains(DamageType.DOT)) continue;
                    if (ConfigLoader.getAuraWhitelist().contains(packet.getElement().getId())) MythicCore.getAuraManager().getAura(a.getEntity().getUniqueId()).addAura(packet.getElement().getId(), ConfigLoader.getDefaultGaugeUnit(), ConfigLoader.getDefaultDecayRate());
                    boolean reaction_respond = triggerReactions(packet, ConfigLoader.getDefaultGaugeUnit(), ConfigLoader.getDefaultDecayRate(), a.getEntity(), attacker, a.toBukkit().getCause());

                    if (ConfigLoader.getDefaultGaugeUnit() > 0 && ConfigLoader.getAuraWhitelist().contains(packet.getElement().getId()) || reaction_respond) {
                        if (attacker != null) {
                            if (a.getAttack() instanceof ASTAttackMetadata ast_attack_data) {
                                MythicCore.getCooldownManager().getCooldown(attacker.getUniqueId()).setCooldown(a.getEntity(), ast_attack_data.getInternalCooldownSource(), ConfigLoader.getInternalCooldown(ast_attack_data.getInternalCooldownSource()));
                            } else {
                                MythicCore.getCooldownManager().getCooldown(attacker.getUniqueId()).setCooldown(a.getEntity(), "default", ConfigLoader.getInternalCooldown("default"));
                            }
                        }
                    }
                }
            }

        } catch (NullPointerException ignored) {}
    }

     */

    public static boolean triggerReactions(DamagePacket damage, double gauge_unit, String decay_rate, LivingEntity entity, Entity damager, EntityDamageEvent.DamageCause damage_cause) {
        if (damage.getElement() == null) return false;

        boolean reaction_success = false;
        List<String> reaction_ids = ConfigLoader.getReactionPriorityList(damage.getElement().getId());
        for (String reaction_id : reaction_ids) {

            if (!MythicCore.getReactionManager().getElementalReactions().containsKey(reaction_id)) continue;
            ElementalReaction rawReaction = MythicCore.getReactionManager().getElementalReactions().get(reaction_id);

            if (rawReaction instanceof TriggerAuraReaction reaction) {
                if (MythicCore.getAuraManager().getAura(entity.getUniqueId()).getMapAura().containsKey(rawReaction.getAura()) && MythicCore.getAuraManager().getAura(entity.getUniqueId()).getMapAura().containsKey(rawReaction.getTrigger())) {
                    if (!reaction.getDisplay().equals("")) Utils.displayIndicator(reaction.getDisplay(), entity);
                    reaction.t(damage, gauge_unit, decay_rate, entity, damager, damage_cause);
                    reaction_success = true;
                }
            } else if (rawReaction instanceof DoubleAuraReaction reaction) {
                if (reaction.getAura().equals(damage.getElement().getId()) || reaction.getTrigger().equals(damage.getElement().getId())) {
                    if (MythicCore.getAuraManager().getAura(entity.getUniqueId()).getMapAura().containsKey(rawReaction.getAura()) && MythicCore.getAuraManager().getAura(entity.getUniqueId()).getMapAura().containsKey(rawReaction.getTrigger())) {

                        if (!snapshotStats.containsKey(entity)) {
                            snapshotStats.put(entity, new HashMap<>(Map.of(reaction, new SnapshotStats())));
                        } else {
                            snapshotStats.get(entity).put(reaction, new SnapshotStats());
                        }

                        if (damager != null) {
                            if (damager instanceof Player player) {
                                PlayerData playerData = PlayerData.get(player);
                                for (StatInstance instance : playerData.getStats().getMap().getInstances()) {
                                    snapshotStats.get(entity).get(reaction).setStat(instance.getStat(), instance.getTotal());
                                }
                                snapshotStats.get(entity).get(reaction).setStat("LEVEL", playerData.getLevel());
                                snapshotStats.get(entity).get(reaction).setStat("LAST_MOB_TYPE", Combat.getLastMobType(player).getId());
                            } else {
                                ActiveMob mythicMob = MythicBukkit.inst().getMobManager().getActiveMob(damager.getUniqueId()).orElse(null);
                                snapshotStats.get(entity).get(reaction).setStat("LEVEL", (mythicMob != null) ? mythicMob.getLevel() : 1);
                                snapshotStats.get(entity).get(reaction).setStat("LAST_MOB_TYPE", Combat.getLastMobType(damager).getId());
                            }
                        } else {
                            snapshotStats.get(entity).get(reaction).setStat("LEVEL", 1);
                            snapshotStats.get(entity).get(reaction).setStat("LAST_MOB_TYPE", Combat.MobType.NULL.getId());
                        }

                        if (reactionTasks.containsKey(entity) && reactionTasks.get(entity).contains(reaction)) return false;

                        if (!reaction.getDisplay().equals("")) Utils.displayIndicator(reaction.getDisplay(), entity);

                        if (!reactionTasks.containsKey(entity)) {
                            reactionTasks.put(entity, new HashSet<>(Set.of(reaction)));
                        } else {
                            reactionTasks.get(entity).add(reaction);
                        }

                        reaction_success = true;

                        Bukkit.getScheduler().runTaskTimerAsynchronously(MythicCore.getInstance(), (task) -> {
                            if (MythicCore.getAuraManager().getAura(entity.getUniqueId()).getMapAura().containsKey(rawReaction.getAura()) && MythicCore.getAuraManager().getAura(entity.getUniqueId()).getMapAura().containsKey(rawReaction.getTrigger())) {

                                Bukkit.getScheduler().runTask(MythicCore.getInstance(), () -> reaction.t(damage, gauge_unit, decay_rate, entity, damager, damage_cause, snapshotStats.get(entity).getOrDefault(reaction, new SnapshotStats().setStat("LEVEL", 1))));

                            } else {

                                if (reactionTasks.containsKey(entity)) {
                                    reactionTasks.get(entity).remove(reaction);
                                    if (reactionTasks.get(entity).isEmpty()) reactionTasks.remove(entity);
                                }

                                if (snapshotStats.containsKey(entity)) {
                                    snapshotStats.get(entity).remove(reaction);
                                    if (snapshotStats.get(entity).isEmpty()) snapshotStats.remove(entity);
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
