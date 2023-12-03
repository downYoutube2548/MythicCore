package com.dev.mythiccore.mechanics.apply;

import com.dev.mythiccore.MythicCore;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import org.bukkit.entity.Entity;

public class ElementalShield implements ITargetedEntitySkill {

    private final double amount;
    private final long duration;
    private final String element;

    public ElementalShield(MythicLineConfig config) {
        amount = config.getDouble(new String[] {"amount", "a"}, 0);
        duration = config.getLong(new String[] {"duration", "d", "t"}, 0);
        element = config.getString(new String[] {"element", "e"});
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        if (element == null) return SkillResult.INVALID_CONFIG;

        if (BukkitAdapter.adapt(abstractEntity) != null) {
            Entity bukkittarget = BukkitAdapter.adapt(abstractEntity);
            MythicCore.getBuffManager().getBuff(bukkittarget.getUniqueId()).addBuff(new com.dev.mythiccore.buff.buffs.ElementalShield(amount, element, duration));
            return SkillResult.SUCCESS;
        }
        return SkillResult.ERROR;
    }
}
