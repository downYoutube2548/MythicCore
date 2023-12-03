package com.dev.mythiccore.mechanics.modify;

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

import java.util.UUID;

public class SetElementalDamage implements ITargetedEntitySkill {

    private final String element;
    private final PlaceholderDouble amount;
    private final String gauge;
    private final String cooldown_source;
    private final long internal_cooldown;

    public SetElementalDamage(MythicLineConfig config) {
        amount = config.getPlaceholderDouble(new String[] {"amount", "a"}, 0);
        element = config.getString(new String[] {"element", "e"}, ConfigLoader.getDefaultElement());
        gauge = config.getString(new String[] {"gauge_unit", "gu"}, ConfigLoader.getDefaultGauge());
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
            ActiveMob mythicMob = MythicBukkit.inst().getMobManager().getActiveMob(bukkittarget.getUniqueId()).orElse(null);
            if (mythicMob != null) {
                mythicMob.getVariables().put("AST_ELEMENTAL_DAMAGE_AMOUNT", Variable.ofType(VariableType.FLOAT, amount.get()));
                mythicMob.getVariables().put("AST_ELEMENTAL_DAMAGE_ELEMENT", Variable.ofType(VariableType.STRING, element));
                mythicMob.getVariables().put("AST_ELEMENTAL_DAMAGE_GAUGE_UNIT", Variable.ofType(VariableType.STRING, gauge));
                mythicMob.getVariables().put("AST_ELEMENTAL_DAMAGE_COOLDOWN_SOURCE", Variable.ofType(VariableType.STRING, cooldown_source));
                mythicMob.getVariables().put("AST_ELEMENTAL_DAMAGE_INTERNAL_COOLDOWN", Variable.ofType(VariableType.INTEGER, internal_cooldown));
                return SkillResult.SUCCESS;
            }
            return SkillResult.MISSING_COMPATIBILITY;
        }
        return SkillResult.ERROR;
    }
}
