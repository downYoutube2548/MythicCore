package com.dev.mythiccore.mechanics.apply;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.buff.buffs.DefenseReduction;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import org.bukkit.entity.Entity;

public class ReduceDefense implements ITargetedEntitySkill {
    private final double amount;
    private final long duration;

    public ReduceDefense(MythicLineConfig config) {
        amount = config.getDouble(new String[] {"amount", "a"}, 0);
        duration = config.getLong(new String[] {"duration", "d", "t"}, 0);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        if (BukkitAdapter.adapt(abstractEntity) != null) {
            Entity bukkittarget = BukkitAdapter.adapt(abstractEntity);
            MythicCore.getBuffManager().getBuff(bukkittarget.getUniqueId()).addBuff(new DefenseReduction(amount, duration));
            return SkillResult.SUCCESS;
        }
        return SkillResult.ERROR;
    }
}
