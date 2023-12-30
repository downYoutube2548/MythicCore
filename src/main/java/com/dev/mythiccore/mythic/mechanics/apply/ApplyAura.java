package com.dev.mythiccore.mythic.mechanics.apply;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.utils.ConfigLoader;
import com.dev.mythiccore.utils.Utils;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;

public class ApplyAura implements ITargetedEntitySkill {

    private final String aura;
    private final String gauge;
    private final boolean trigger_reaction;

    public ApplyAura(MythicLineConfig config) {
        aura = config.getString(new String[]{"aura", "a"});
        gauge = config.getString(new String[]{"gauge_unit", "gu"}, ConfigLoader.getDefaultGauge());
        trigger_reaction = config.getBoolean(new String[]{"trigger_reaction", "tr"}, true);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {

        if (aura != null) {

            double gauge_unit = Double.parseDouble(Utils.splitTextAndNumber(gauge)[0]);
            String decay_rate = Utils.splitTextAndNumber(gauge)[1];

            MythicCore.getAuraManager().getAura(abstractEntity.getUniqueId()).addAura(aura, gauge_unit, decay_rate, trigger_reaction);
        }

        return SkillResult.INVALID_CONFIG;
    }
}
