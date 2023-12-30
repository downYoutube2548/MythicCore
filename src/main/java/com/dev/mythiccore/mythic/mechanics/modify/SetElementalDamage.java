package com.dev.mythiccore.mythic.mechanics.modify;

import com.dev.mythiccore.utils.ConfigLoader;
import com.dev.mythiccore.utils.EntityStatManager;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.BukkitAdapter;
import org.bukkit.entity.Entity;

import java.util.UUID;

public class SetElementalDamage implements ITargetedEntitySkill {

    private final String element;
    private final PlaceholderDouble amount;
    private final String gauge;
    private final String cooldown_source;
    private final long internal_cooldown;
    private final String damage_calculation;

    private final PlaceholderDouble talent_percent;

    public SetElementalDamage(MythicLineConfig config) {
        amount = config.getPlaceholderDouble(new String[] {"amount", "a"}, 0);
        element = config.getString(new String[] {"element", "e"}, ConfigLoader.getDefaultElement());
        gauge = config.getString(new String[] {"gauge_unit", "gu"}, ConfigLoader.getDefaultGauge());
        damage_calculation = config.getString(new String[] {"dc", "formula", "f"}, ConfigLoader.getDefaultDamageCalculation());
        talent_percent = config.getPlaceholderDouble(new String[] {"p", "percent"}, 100);
        UUID uuid = UUID.randomUUID();

        if (config.getLong(new String[]{"icd", "internal_cooldown"}, -1) < 0) {
            cooldown_source = config.getString(new String[]{"icd", "internal_cooldown"}, "default");
            internal_cooldown = ConfigLoader.getInternalCooldown(cooldown_source);
        } else {
            cooldown_source = "INTERNAL_COOLDOWN_"+ uuid;
            internal_cooldown = config.getLong(new String[]{"icd", "internal_cooldown"}, 0);
        }
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {

        if (BukkitAdapter.adapt(abstractEntity) != null) {
            Entity bukkittarget = BukkitAdapter.adapt(abstractEntity);

            EntityStatManager entityStat = new EntityStatManager(bukkittarget);

            entityStat.set("AST_ELEMENTAL_DAMAGE_AMOUNT", amount.get(skillMetadata));
            entityStat.set("AST_ELEMENTAL_DAMAGE_ELEMENT", element);
            entityStat.set("AST_ELEMENTAL_DAMAGE_GAUGE_UNIT", gauge);
            entityStat.set("AST_ELEMENTAL_DAMAGE_COOLDOWN_SOURCE", cooldown_source);
            entityStat.set("AST_ELEMENTAL_DAMAGE_INTERNAL_COOLDOWN", internal_cooldown);
            entityStat.set("AST_ELEMENTAL_DAMAGE_FORMULA", damage_calculation);
            entityStat.set("AST_ELEMENTAL_DAMAGE_PERCENT", talent_percent.get(skillMetadata));

            return SkillResult.SUCCESS;
        }
        return SkillResult.ERROR;
    }
}
