package com.dev.mythiccore.mythic.mechanics.apply;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.mobs.GenericCaster;
import io.lumine.mythic.api.skills.*;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.utils.MythicUtil;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.player.PlayerMetadata;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;

public class Snapshot implements ITargetedEntitySkill {

    public final String skill;
    public final boolean check_owner;
    public final boolean update_stats;
    public final boolean update_targeter;
    public final Set<Map.Entry<String, String>> entryConfig;

    public Snapshot(MythicLineConfig config) {
        skill = config.getString(new String[] {"skill", "spell", "s"}, null);
        check_owner = config.getBoolean(new String[]{"check_owner", "co"}, false);
        update_stats = config.getBoolean(new String[]{"update_stats"}, true);
        update_targeter = config.getBoolean(new String[]{"update_targeter"}, true);
        entryConfig = config.entrySet();
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {

        Entity bukkitcaster = skillMetadata.getCaster().getEntity().getBukkitEntity();

        if (bukkitcaster instanceof Player || (skillMetadata.getCaster() instanceof ActiveMob && check_owner)) {
            Player player = null;

            if (bukkitcaster instanceof Player p) {

                player = p;

            } else {

                ActiveMob am = (ActiveMob) skillMetadata.getCaster();

                if (am.getOwner().isPresent()) {
                    UUID uuid = am.getOwner().get();
                    assert uuid != null;
                    Player a = Bukkit.getPlayer(uuid);
                    if (a != null) {
                        player = a;
                    }
                }
            }

            if (player != null) {
                PlayerData playerData = PlayerData.get(player);
                cast(skillMetadata, bukkitcaster, playerData, abstractEntity);

            } else {
                cast(skillMetadata, bukkitcaster, abstractEntity);
            }

        }
        else {
            cast(skillMetadata, bukkitcaster, abstractEntity);
        }

        return SkillResult.SUCCESS;
    }

    public void cast(SkillMetadata skillMetadata, Entity caster, PlayerData playerData, AbstractEntity snapshotTarget) {

        Optional<Skill> maybeSkill = MythicBukkit.inst().getSkillManager().getSkill(skill);
        maybeSkill.ifPresent(skill -> {

            SkillCaster c;
            if (MythicBukkit.inst().getMobManager().isActiveMob(caster.getUniqueId())) {
                c = MythicBukkit.inst().getMobManager().getMythicMobInstance(caster);
            } else {
                c = new GenericCaster(BukkitAdapter.adapt(caster));
            }

            SkillMetadata newSkillMeta = skillMetadata.deepClone();
            PlayerMetadata playerMetadata = new PlayerMetadata(playerData.getMMOPlayerData().getStatMap(), EquipmentSlot.MAIN_HAND);
            entryConfig.stream().filter(a -> a.getKey().startsWith("stat_")).forEach(a -> playerMetadata.setStat(a.getKey().substring(5).toUpperCase(), PlaceholderDouble.of(a.getValue()).get(skillMetadata)));
            if (update_stats) newSkillMeta.setMetadata("SNAPSHOT_STATS", playerMetadata);
            if (update_targeter) newSkillMeta.setMetadata("SNAPSHOT_TARGET", snapshotTarget);
            entryConfig.forEach((a) -> newSkillMeta.setMetadata("SNAPSHOT_DATA_" + a.getKey().toLowerCase(), String.valueOf(PlaceholderDouble.of(a.getValue()).get(skillMetadata))));

            newSkillMeta.setCaster(c);

            if (skill.isUsable(newSkillMeta, SkillTriggers.API)) {
                skill.execute(newSkillMeta);
            }
        });
    }
    public void cast(SkillMetadata skillMetadata, Entity caster, AbstractEntity snapshotTarget) {
        Optional<Skill> maybeSkill = MythicBukkit.inst().getSkillManager().getSkill(skill);
        maybeSkill.ifPresent(skill -> {

            SkillCaster c;
            if (MythicBukkit.inst().getMobManager().isActiveMob(caster.getUniqueId())) {
                c = MythicBukkit.inst().getMobManager().getMythicMobInstance(caster);
            } else {
                c = new GenericCaster(BukkitAdapter.adapt(caster));
            }

            SkillMetadata newSkillMeta = skillMetadata.deepClone();
            if (update_targeter) newSkillMeta.setMetadata("SNAPSHOT_TARGET", snapshotTarget);
            entryConfig.forEach((a) -> newSkillMeta.setMetadata("SNAPSHOT_DATA_" + a.getKey().toLowerCase(), String.valueOf(PlaceholderDouble.of(a.getValue()).get(skillMetadata))));

            newSkillMeta.setCaster(c);

            if (skill.isUsable(newSkillMeta, SkillTriggers.API)) {
                skill.execute(newSkillMeta);
            }
        });
    }
}
