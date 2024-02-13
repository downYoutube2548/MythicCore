package com.dev.mythiccore.mythic.mechanics.apply;

import com.dev.mythiccore.MythicCore;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;

public class ClearAura implements ITargetedEntitySkill {

    private final String aura;

    public ClearAura(MythicLineConfig config) {
        aura = config.getString(new String[]{"aura", "a"}, "ALL");
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {

        if (aura.equals("ALL")) MythicCore.getAuraManager().getAura(abstractEntity.getUniqueId()).clearAura();
        else MythicCore.getAuraManager().getAura(abstractEntity.getUniqueId()).removeAura(aura);

        return SkillResult.SUCCESS;
    }
}
