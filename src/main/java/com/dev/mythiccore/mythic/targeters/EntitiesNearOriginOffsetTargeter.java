package com.dev.mythiccore.mythic.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.targeters.EntitiesInRadiusTargeter;

import java.util.Collection;

public class EntitiesNearOriginOffsetTargeter extends EntitiesInRadiusTargeter {

    private final float xOffset;
    private final float yOffset;
    private final float zOffset;

    public EntitiesNearOriginOffsetTargeter(SkillExecutor manager, MythicLineConfig mlc) {
        super(manager, mlc);
        xOffset = config.getFloat(new String[]{"xOffset", "x"}, 0);
        yOffset = config.getFloat(new String[]{"yOffset", "y"}, 0);
        zOffset = config.getFloat(new String[]{"zOffset", "z"}, 0);
    }

    @Override
    public Collection<AbstractEntity> getEntities(SkillMetadata data) {
        return this.getEntitiesNearPoint(data, data.getOrigin().add(xOffset, yOffset, zOffset));
    }
}
