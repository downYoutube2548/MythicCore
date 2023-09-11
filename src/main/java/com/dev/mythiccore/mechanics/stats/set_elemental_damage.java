package com.dev.mythiccore.mechanics.stats;

import com.dev.mythiccore.utils.ConfigLoader;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.variables.Variable;
import io.lumine.mythic.core.skills.variables.VariableType;
import org.bukkit.entity.Entity;

public class set_elemental_damage implements ITargetedEntitySkill {

    private final String element;
    private final PlaceholderDouble amount;
    private final String gauge;
    private final String cooldown_source;

    public set_elemental_damage(MythicLineConfig config) {
        amount = config.getPlaceholderDouble(new String[] {"amount", "a"}, 0);
        element = config.getString(new String[] {"element", "e"}, ConfigLoader.getDefaultElement());
        gauge = config.getString(new String[] {"gauge_unit", "gu"}, ConfigLoader.getDefaultGauge());
        cooldown_source = config.getString(new String[]{"cooldown_source", "icd"}, "default");
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {

        if (BukkitAdapter.adapt(abstractEntity) != null) {
            Entity bukkittarget = BukkitAdapter.adapt(abstractEntity);
            ActiveMob mythicMob = MythicBukkit.inst().getMobManager().getActiveMob(bukkittarget.getUniqueId()).orElse(null);
            if (mythicMob != null) {
                mythicMob.getVariables().put("AST_ELEMENTAL_DAMAGE_AMOUNT", Variable.ofType(VariableType.FLOAT, amount.get()));
                mythicMob.getVariables().put("AST_ELEMENTAL_DAMAGE_ELEMENT", Variable.ofType(VariableType.STRING, element));
                mythicMob.getVariables().put("AST_ELEMENTAL_DAMAGE_GAUGE_UNIT", Variable.ofType(VariableType.STRING, gauge));
                mythicMob.getVariables().put("AST_ELEMENTAL_DAMAGE_COOLDOWN_SOURCE", Variable.ofType(VariableType.STRING, cooldown_source));
                return SkillResult.SUCCESS;
            }
            return SkillResult.MISSING_COMPATIBILITY;
        }
        return SkillResult.ERROR;
    }
}
