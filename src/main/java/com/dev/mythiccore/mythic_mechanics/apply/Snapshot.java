package com.dev.mythiccore.mythic_mechanics.apply;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.utils.MythicUtil;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Snapshot implements INoTargetSkill {

    private final String skill;

    public Snapshot(MythicLineConfig config) {
        skill = config.getString(new String[] {"skill", "spell", "s"}, null);
    }

    @Override
    public SkillResult cast(SkillMetadata skillMetadata) {

        Entity bukkitcaster = skillMetadata.getCaster().getEntity().getBukkitEntity();

        if (bukkitcaster instanceof Player player) {

            PlayerData playerData = PlayerData.get(player);

            LivingEntity target = MythicUtil.getTargetedEntity(player);
            List<Entity> targets = new ArrayList<>();
            targets.add(target);

            MythicBukkit.inst().getAPIHelper().castSkill(bukkitcaster, skill, bukkitcaster, bukkitcaster.getLocation(), targets, null, 1.0F, skillMetadata1 -> skillMetadata1.setMetadata("SNAPSHOT_STATS", playerData.getMMOPlayerData().getStatMap()));

            return SkillResult.SUCCESS;
        }
        return SkillResult.MISSING_COMPATIBILITY;
    }
}
