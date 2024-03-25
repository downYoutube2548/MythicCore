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
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ReduceMana implements ITargetedEntitySkill {

    private final PlaceholderDouble amount;

    public ReduceMana(MythicLineConfig config) {
        amount = config.getPlaceholderDouble(new String[] {"amount", "a"}, 0);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        if (BukkitAdapter.adapt(abstractEntity) != null) {
            Entity bukkittarget = BukkitAdapter.adapt(abstractEntity);

            if (bukkittarget instanceof Player player) {

                PlayerData playerData = PlayerData.get(player);
                playerData.setMana(playerData.getMana() - amount.get(skillMetadata));

                return SkillResult.SUCCESS;

            } else return SkillResult.MISSING_COMPATIBILITY;
        }
        return SkillResult.ERROR;
    }
}
