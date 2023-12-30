package com.dev.mythiccore.mythic.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.targeters.IEntitySelector;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@MythicTargeter(
        author = "downn_fzL",
        name = "snapshot",
        description = "Use Snapshot skill's targeter"
)
public class SnapshotTargeter extends IEntitySelector {
    public SnapshotTargeter(SkillExecutor manager, MythicLineConfig mlc) {
        super(manager, mlc);
    }

    public Collection<AbstractEntity> getEntities(SkillMetadata data) {
        Optional<Object> optionalTarget = data.getMetadata("SNAPSHOT_TARGET");
        if (optionalTarget.isPresent()) {
            Set<AbstractEntity> targets = new HashSet<>();
            targets.add((AbstractEntity) optionalTarget.get());
            return targets;
        } else {
            return data.getEntityTargets();
        }
    }
}
