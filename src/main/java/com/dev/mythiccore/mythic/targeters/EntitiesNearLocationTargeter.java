package com.dev.mythiccore.mythic.targeters;

import com.google.common.collect.Sets;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillTargeter;
import io.lumine.mythic.core.skills.targeters.CustomTargeter;
import io.lumine.mythic.core.skills.targeters.EntitiesInRadiusTargeter;
import io.lumine.mythic.core.skills.targeters.ILocationSelector;

import java.util.Collection;

public class EntitiesNearLocationTargeter extends EntitiesInRadiusTargeter {

    private final String location;

    public EntitiesNearLocationTargeter(SkillExecutor manager, MythicLineConfig mlc) {
        super(manager, mlc);
        location = config.getString(new String[]{"location", "loc", "l"}, "@SelfLocation");
    }

    @Override
    public Collection<AbstractEntity> getEntities(SkillMetadata data) {
        return this.getEntitiesNearPoint(data, (AbstractLocation) getEntityTargeter(data, this.getPlugin().getSkillManager().getTargeter(location)));
    }

    private Collection<AbstractLocation> getEntityTargeter(SkillMetadata data, SkillTargeter targeter) {

        if (targeter instanceof CustomTargeter && ((CustomTargeter)targeter).getTargeter().isPresent()) {
            targeter = ((CustomTargeter)targeter).getTargeter().get();
        }

        if (targeter instanceof ILocationSelector) {
            return ((ILocationSelector)targeter).getLocations(data);
        }

        return Sets.newHashSet();
    }
}
