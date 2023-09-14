package com.dev.mythiccore.mechanics.apply;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.events.attack_handle.attack_priority.TriggerReaction;
import com.dev.mythiccore.library.ASTAttackMetaData;
import com.dev.mythiccore.library.provider.ASTEntityStatProvider;
import com.dev.mythiccore.utils.ConfigLoader;
import com.dev.mythiccore.utils.DamageManager;
import com.dev.mythiccore.utils.Utils;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.stat.StatMap;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.damage.DamagePacket;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.element.Element;
import io.lumine.mythic.lib.player.PlayerMetadata;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Objects;

//This class will register the ElementDamage Skill to MythicMobs
public class elemental_damage implements ITargetedEntitySkill {
    private final String element;
    private final PlaceholderDouble amount;
    private final String gauge;
    private final String cooldown_source;

    /**
     * This is constructor for the Skill
     * Usage: ElementDamage{amount=5;element=GEO} @target
     *
     * @param config The config of the skill.
     */
    public elemental_damage(MythicLineConfig config) {
        amount = config.getPlaceholderDouble(new String[] {"amount", "a"}, 0);
        element = config.getString(new String[] {"element", "e"}, ConfigLoader.getDefaultElement());
        gauge = config.getString(new String[] {"gauge_unit", "gu"}, ConfigLoader.getDefaultGauge());
        cooldown_source = config.getString(new String[]{"cooldown_source", "icd"}, "default");
    }

    /**
     * This method will trigger when the skill is cast at an entity.
     *
     * @param skillMetadata The skill metadata.
     * @param abstractEntity The entity that was targeted.
     * @return The result of the skill.
     */
    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {

        if (BukkitAdapter.adapt(abstractEntity) != null) {
            Bukkit.getScheduler().runTaskAsynchronously(MythicCore.getInstance(), ()-> {
                Entity bukkittarget = BukkitAdapter.adapt(abstractEntity);
                Entity bukkitcaster = skillMetadata.getCaster().getEntity().getBukkitEntity();

                double gauge_unit = Double.parseDouble(Utils.splitTextAndNumber(gauge)[0]);
                String decay_rate = Utils.splitTextAndNumber(gauge)[1];

                Element element1 = Objects.requireNonNull(Element.valueOf(element), ConfigLoader.getDefaultElement());
                // caster is player
                if (bukkitcaster instanceof Player) {

                    //This part will damage the player

                    PlayerData playerData = PlayerData.get(bukkitcaster.getUniqueId());

                    DamageMetadata damage = new DamageMetadata(amount.get(skillMetadata), element1, DamageType.SKILL);
                    StatMap statMap = playerData.getMMOPlayerData().getStatMap();
                    PlayerMetadata playerMetadata = new PlayerMetadata(statMap, EquipmentSlot.MAIN_HAND);
                    AttackMetadata attack = new ASTAttackMetaData(damage, (LivingEntity) bukkittarget, playerMetadata, cooldown_source);

                    Bukkit.getScheduler().runTask(MythicCore.getInstance(), () -> {

                        for (DamagePacket packet : damage.getPackets()) {
                            if (packet.getElement() == null) continue;
                            if (!ConfigLoader.getAuraWhitelist().contains(packet.getElement().getId())) continue;
                            MythicCore.getAuraManager().getAura(bukkittarget.getUniqueId()).addAura(packet.getElement().getId(), gauge_unit, decay_rate);
                            TriggerReaction.triggerReactions(packet, gauge_unit, decay_rate, (LivingEntity) bukkittarget, bukkitcaster, EntityDamageEvent.DamageCause.ENTITY_ATTACK);
                        }

                        DamageManager.registerAttack(attack, true, false, EntityDamageEvent.DamageCause.ENTITY_ATTACK);
                    });
                }

                // caster is not player
                else {

                    DamageMetadata damage = new DamageMetadata(amount.get(skillMetadata), Objects.requireNonNull(Element.valueOf(element), ConfigLoader.getDefaultElement()), DamageType.SKILL);
                    AttackMetadata attack = new ASTAttackMetaData(damage, (LivingEntity) bukkittarget, new ASTEntityStatProvider((LivingEntity) bukkitcaster), cooldown_source);
                    Bukkit.getScheduler().runTask(MythicCore.getInstance(), () -> {
                        DamageManager.registerAttack(attack, true, false, EntityDamageEvent.DamageCause.ENTITY_ATTACK);

                        for (DamagePacket packet : damage.getPackets()) {
                            if (packet.getElement() == null) continue;
                            if (!ConfigLoader.getAuraWhitelist().contains(packet.getElement().getId())) continue;
                            MythicCore.getAuraManager().getAura(bukkittarget.getUniqueId()).addAura(packet.getElement().getId(), gauge_unit, decay_rate);
                            TriggerReaction.triggerReactions(packet, gauge_unit, decay_rate, (LivingEntity) bukkittarget, bukkitcaster, EntityDamageEvent.DamageCause.ENTITY_ATTACK);
                        }
                    });
                }
            });
            return SkillResult.SUCCESS;
        }
        return SkillResult.ERROR;
    }
}
