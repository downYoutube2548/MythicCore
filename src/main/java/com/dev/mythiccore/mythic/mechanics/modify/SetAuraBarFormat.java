package com.dev.mythiccore.mythic.mechanics.modify;

import com.dev.mythiccore.utils.EntityStatManager;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import org.bukkit.entity.Entity;

public class SetAuraBarFormat implements ITargetedEntitySkill {

    private final String barFormat;

    public SetAuraBarFormat(MythicLineConfig config) {
        this.barFormat = config.getString(new String[]{"format", "f"});
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        if (BukkitAdapter.adapt(abstractEntity) != null) {
            Entity bukkittarget = BukkitAdapter.adapt(abstractEntity);
            EntityStatManager entityStat = new EntityStatManager(bukkittarget);
            entityStat.set("AURA_BAR_FORMAT", barFormat);
            return SkillResult.SUCCESS;
        }
        return SkillResult.ERROR;
    }
}
