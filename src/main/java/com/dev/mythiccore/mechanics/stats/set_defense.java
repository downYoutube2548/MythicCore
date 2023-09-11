package com.dev.mythiccore.mechanics.stats;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.variables.Variable;
import io.lumine.mythic.core.skills.variables.VariableType;
import org.bukkit.entity.Entity;

public class set_defense implements ITargetedEntitySkill {

    private final double amount;

    public set_defense(MythicLineConfig config) {
        amount = config.getDouble(new String[]{"amount", "a"}, 0);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        if (BukkitAdapter.adapt(abstractEntity) != null) {
            Entity bukkittarget = BukkitAdapter.adapt(abstractEntity);
            ActiveMob mythicMob = MythicBukkit.inst().getMobManager().getActiveMob(bukkittarget.getUniqueId()).orElse(null);
            if (mythicMob != null) {
                mythicMob.getVariables().put("DEFENSE", Variable.ofType(VariableType.FLOAT, amount));
                return SkillResult.SUCCESS;
            }
            return SkillResult.MISSING_COMPATIBILITY;
        }
        return SkillResult.ERROR;
    }
}
