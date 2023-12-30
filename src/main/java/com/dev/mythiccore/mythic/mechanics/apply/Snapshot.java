package com.dev.mythiccore.mythic.mechanics.apply;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.utils.MythicUtil;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.player.PlayerMetadata;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Snapshot implements ITargetedEntitySkill {

    private final String skill;
    private final Set<Map.Entry<String, String>> entryConfig;

    public Snapshot(MythicLineConfig config) {
        skill = config.getString(new String[] {"skill", "spell", "s"}, null);
        entryConfig = config.entrySet();
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {

        Entity bukkitcaster = skillMetadata.getCaster().getEntity().getBukkitEntity();

        if (bukkitcaster instanceof Player player) {

            PlayerData playerData = PlayerData.get(player);

            LivingEntity target = MythicUtil.getTargetedEntity(player);
            List<Entity> targets = new ArrayList<>();
            targets.add(target);

            MythicBukkit.inst().getAPIHelper().castSkill(bukkitcaster, skill, bukkitcaster, bukkitcaster.getLocation(), targets, null, 1.0F, skillMetadata1 -> {
                skillMetadata1.setMetadata("SNAPSHOT_STATS", new PlayerMetadata(playerData.getMMOPlayerData().getStatMap(), EquipmentSlot.MAIN_HAND));
                skillMetadata1.setMetadata("SNAPSHOT_TARGET", abstractEntity);

                entryConfig.forEach((a) -> skillMetadata1.setMetadata("SNAPSHOT_DATA_"+a.getKey().toLowerCase(), String.valueOf(PlaceholderDouble.of(a.getValue()).get(skillMetadata))));
            });

        } else {
            MythicBukkit.inst().getAPIHelper().castSkill(bukkitcaster, skill, skillMetadata1 -> {
                skillMetadata1.setMetadata("SNAPSHOT_TARGET", abstractEntity);
                entryConfig.forEach((a) -> skillMetadata1.setMetadata("SNAPSHOT_DATA_"+a.getKey().toLowerCase(), String.valueOf(PlaceholderDouble.of(a.getValue()).get(skillMetadata))));
            });
        }

        return SkillResult.SUCCESS;
    }
}
