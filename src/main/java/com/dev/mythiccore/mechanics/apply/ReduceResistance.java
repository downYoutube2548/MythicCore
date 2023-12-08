package com.dev.mythiccore.mechanics.apply;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.buff.buffs.ElementalResistanceReduction;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.BukkitAdapter;
import org.bukkit.entity.Entity;

public class ReduceResistance implements ITargetedEntitySkill {
    private final String element;
    private final PlaceholderDouble amount;
    private final long duration;

    public ReduceResistance(MythicLineConfig config) {
        amount = config.getPlaceholderDouble(new String[] {"amount", "a"}, 0);
        duration = config.getLong(new String[] {"duration", "d", "t"}, 0);
        element = config.getString(new String[] {"element", "e"});
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        if (element == null) return SkillResult.INVALID_CONFIG;

        if (BukkitAdapter.adapt(abstractEntity) != null) {
            Entity bukkittarget = BukkitAdapter.adapt(abstractEntity);
            MythicCore.getBuffManager().getBuff(bukkittarget.getUniqueId()).addBuff(new ElementalResistanceReduction(amount.get(skillMetadata), duration, element));
            return SkillResult.SUCCESS;
        }
        return SkillResult.ERROR;
    }
}
