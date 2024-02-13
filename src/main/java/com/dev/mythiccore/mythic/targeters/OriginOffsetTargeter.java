package com.dev.mythiccore.mythic.targeters;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.targeters.ILocationSelector;

import java.util.Collection;
import java.util.HashSet;

public class OriginOffsetTargeter extends ILocationSelector {

    private final float xOffset;
    private final float yOffset;
    private final float zOffset;

    public OriginOffsetTargeter(SkillExecutor manager, MythicLineConfig mlc) {
        super(manager, mlc);
        xOffset = config.getFloat(new String[]{"xOffset", "x"}, 0);
        yOffset = config.getFloat(new String[]{"yOffset", "y"}, 0);
        zOffset = config.getFloat(new String[]{"zOffset", "z"}, 0);
    }

    @Override
    public Collection<AbstractLocation> getLocations(SkillMetadata data) {
        HashSet<AbstractLocation> targets = new HashSet<>();
        AbstractLocation origin = data.getOrigin().add(xOffset, yOffset, zOffset);
        targets.add(this.mutate(data, origin.clone()));
        return targets;
    }
}
