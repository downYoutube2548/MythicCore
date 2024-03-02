package com.dev.mythiccore.mythic.mechanics.apply;

import com.dev.mythiccore.enums.AttackSource;
import com.dev.mythiccore.library.ASTEntityStatProvider;
import com.dev.mythiccore.library.attackMetadata.ASTAttackMetadata;
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
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.element.Element;
import io.lumine.mythic.lib.player.PlayerMetadata;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

//This class will register the ElementDamage Skill to MythicMobs
public class ElementalDamage implements ITargetedEntitySkill {
    private final String element;
    private final PlaceholderDouble amount;
    private final String gauge;
    private final String cooldown_source;
    private final long internal_cooldown;
    private final String damage_calculation;
    private final PlaceholderDouble talent_percent;
    private final Set<Map.Entry<String, String>> entryConfig;
    private final DamageType damageType;
    private final String weaponType;

    /**
     * This is constructor for the Skill
     * Usage: ElementDamage{amount=5;element=GEO} @target
     *
     * @param config The config of the skill.
     */
    public ElementalDamage(MythicLineConfig config) {
        amount = config.getPlaceholderDouble(new String[] {"amount", "a"}, 0);
        element = config.getString(new String[] {"element", "e"}, ConfigLoader.getDefaultElement());
        gauge = config.getString(new String[] {"gauge_unit", "gu"}, ConfigLoader.getDefaultGauge());
        damage_calculation = config.getString(new String[] {"dc", "formula", "f"}, ConfigLoader.getDefaultDamageCalculation());
        talent_percent = config.getPlaceholderDouble(new String[] {"p", "percent"}, 100);
        entryConfig = config.entrySet();
        damageType = DamageType.valueOf(config.getString(new String[] {"damageType", "dt"}, "SKILL"));
        weaponType = config.getString(new String[]{"weaponType", "wt", "weapon"}, "NONE");

        UUID uuid = UUID.randomUUID();

        if (config.getLong(new String[]{"icd", "internal_cooldown"}, -1) < 0) {
            cooldown_source = config.getString(new String[]{"icd", "internal_cooldown"}, "default");
            internal_cooldown = ConfigLoader.getInternalCooldown(cooldown_source);
        } else {
            cooldown_source = "INTERNAL_COOLDOWN_"+ uuid;
            internal_cooldown = config.getLong(new String[]{"icd", "internal_cooldown"}, 0);
        }
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
            Entity bukkittarget = BukkitAdapter.adapt(abstractEntity);
            Entity bukkitcaster = skillMetadata.getCaster().getEntity().getBukkitEntity();

            double gauge_unit = Double.parseDouble(Utils.splitTextAndNumber(gauge)[0]);
            String decay_rate = Utils.splitTextAndNumber(gauge)[1];

            Element element1 = Objects.requireNonNull(Element.valueOf(element), ConfigLoader.getDefaultElement());
            // caster is player
            if (bukkitcaster instanceof Player) {

                //This part will damage the player

                DamageMetadata damage = new DamageMetadata(amount.get(skillMetadata), element1, damageType);
                PlayerMetadata playerMetadata = (PlayerMetadata) skillMetadata.getMetadata("SNAPSHOT_STATS").orElse(new PlayerMetadata(PlayerData.get(bukkitcaster.getUniqueId()).getMMOPlayerData().getStatMap(), EquipmentSlot.MAIN_HAND));
                entryConfig.stream().filter(a -> a.getKey().startsWith("stat_")).forEach(a -> playerMetadata.setStat(a.getKey().substring(5).toUpperCase(), PlaceholderDouble.of(a.getValue()).get(skillMetadata)));

                ASTAttackMetadata attack = new ASTAttackMetadata(damage, (LivingEntity) bukkittarget, playerMetadata, cooldown_source, internal_cooldown, gauge_unit, decay_rate, damage_calculation, weaponType, talent_percent.get(skillMetadata), AttackSource.MYTHIC_SKILL, true, true);
                attack.setMetadata("SKILL_METADATA", skillMetadata);

                DamageManager.registerAttack(attack, true, false, EntityDamageEvent.DamageCause.ENTITY_ATTACK);
            }

            // caster is not player
            else {

                DamageMetadata damage = new DamageMetadata(amount.get(skillMetadata), Objects.requireNonNull(Element.valueOf(element), ConfigLoader.getDefaultElement()), damageType);
                ASTAttackMetadata attack = new ASTAttackMetadata(damage, (LivingEntity) bukkittarget, new ASTEntityStatProvider((LivingEntity) bukkitcaster), cooldown_source, internal_cooldown, gauge_unit, decay_rate, damage_calculation, weaponType, talent_percent.get(skillMetadata), AttackSource.MYTHIC_SKILL, true, true);
                attack.setMetadata("SKILL_METADATA", skillMetadata);

                DamageManager.registerAttack(attack, true, false, EntityDamageEvent.DamageCause.ENTITY_ATTACK);
            }
            return SkillResult.SUCCESS;
        }
        return SkillResult.ERROR;
    }
}
