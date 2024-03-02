package com.dev.mythiccore.mythic.mechanics.apply;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.buff.buffs.DefenseReduction;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.lang.reflect.Field;
import java.util.Map;

public class ReduceDefense implements ITargetedEntitySkill {
    private final PlaceholderDouble amount;
    private final long duration;

    public ReduceDefense(MythicLineConfig config) {
        amount = config.getPlaceholderDouble(new String[] {"amount", "a"}, 0);
        duration = config.getLong(new String[] {"duration", "d", "t"}, 0);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {

        Map<String, Object> metadata = null;

        try {
            Field f = ((SkillMetadataImpl) skillMetadata).getClass().getDeclaredField("metadata");
            f.setAccessible(true);

            metadata = (Map<String, Object>) f.get(Bukkit.getPluginManager());
        } catch (NoSuchFieldException | IllegalAccessException e) {

        }

        Bukkit.broadcastMessage(String.valueOf(metadata));

        if (BukkitAdapter.adapt(abstractEntity) != null) {
            Entity bukkittarget = BukkitAdapter.adapt(abstractEntity);
            MythicCore.getBuffManager().getBuff(bukkittarget.getUniqueId()).addBuff(new DefenseReduction(amount.get(skillMetadata), duration));
            return SkillResult.SUCCESS;
        }
        return SkillResult.ERROR;
    }
}
