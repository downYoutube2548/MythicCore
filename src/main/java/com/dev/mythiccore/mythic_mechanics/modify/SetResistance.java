package com.dev.mythiccore.mythic_mechanics.modify;

import com.dev.mythiccore.utils.ConfigLoader;
import com.dev.mythiccore.utils.EntityStatManager;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.BukkitAdapter;
import org.bukkit.entity.Entity;

public class SetResistance implements ITargetedEntitySkill {
    private final PlaceholderDouble amount;
    private final String element;

    public SetResistance(MythicLineConfig config) {
        amount = config.getPlaceholderDouble(new String[]{"amount", "a"}, 0);
        element = config.getString(new String[]{"element", "e"}, ConfigLoader.getDefaultElement());
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        if (BukkitAdapter.adapt(abstractEntity) != null) {
            Entity bukkittarget = BukkitAdapter.adapt(abstractEntity);

            EntityStatManager entityStat = new EntityStatManager(bukkittarget);
            entityStat.set("AST_"+element+"_RESISTANCE", amount.get(skillMetadata));

            return SkillResult.SUCCESS;
        }
        return SkillResult.ERROR;
    }
}
