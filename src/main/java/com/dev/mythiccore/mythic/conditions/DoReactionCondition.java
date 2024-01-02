package com.dev.mythiccore.mythic.conditions;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;

public class DoReactionCondition implements ISkillMetaCondition {

    private final String reactionId;

    public DoReactionCondition(MythicLineConfig config) {
        reactionId = config.getString(new String[]{"reaction_id", "id", "r"});
    }

    @Override
    public boolean check(SkillMetadata skillMetadata) {
        return skillMetadata.getMetadata("REACTION_SUCCESS_"+reactionId.toUpperCase()).isPresent();
    }
}
