package com.dev.mythiccore.mythic.placeholders;

import com.dev.mythiccore.utils.EntityStatManager;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.placeholders.all.FunctionalMetaPlaceholder;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.player.PlayerMetadata;
import net.Indyuce.mmocore.api.player.PlayerData;

public class SnapshotPlaceholder {
    public static void register() {
        MythicBukkit.inst().getPlaceholderManager().register("snapshot", new FunctionalMetaPlaceholder((placeholderMeta, param) -> {

            AbstractEntity caster = placeholderMeta.getCaster().getEntity();

            if (placeholderMeta instanceof SkillMetadata skillMetadata) {
                if (placeholderMeta.getCaster().getEntity().isPlayer()) {
                    return switch (param) {
                        case "damage" ->
                                String.valueOf(((PlayerMetadata) skillMetadata.getMetadata("SNAPSHOT_STATS").orElse(new PlayerMetadata(PlayerData.get(caster.getUniqueId()).getMMOPlayerData().getStatMap(), EquipmentSlot.MAIN_HAND))).getStat("ATTACK_DAMAGE"));
                        case "hp" ->
                                String.valueOf(((PlayerMetadata) skillMetadata.getMetadata("SNAPSHOT_STATS").orElse(new PlayerMetadata(PlayerData.get(caster.getUniqueId()).getMMOPlayerData().getStatMap(), EquipmentSlot.MAIN_HAND))).getStat("MAX_HEALTH"));
                        case "def" ->
                                String.valueOf(((PlayerMetadata) skillMetadata.getMetadata("SNAPSHOT_STATS").orElse(new PlayerMetadata(PlayerData.get(caster.getUniqueId()).getMMOPlayerData().getStatMap(), EquipmentSlot.MAIN_HAND))).getStat("DEFENSE"));
                        case "em" ->
                                String.valueOf(((PlayerMetadata) skillMetadata.getMetadata("SNAPSHOT_STATS").orElse(new PlayerMetadata(PlayerData.get(caster.getUniqueId()).getMMOPlayerData().getStatMap(), EquipmentSlot.MAIN_HAND))).getStat("AST_ELEMENTAL_MASTERY"));
                        default -> (param.startsWith("stat.")) ?
                                String.valueOf(((PlayerMetadata) skillMetadata.getMetadata("SNAPSHOT_STATS").orElse(new PlayerMetadata(PlayerData.get(caster.getUniqueId()).getMMOPlayerData().getStatMap(), EquipmentSlot.MAIN_HAND))).getStat(param.substring(5).toUpperCase())) :
                                (String) (skillMetadata.getMetadata("SNAPSHOT_DATA_" + param.toLowerCase()).orElse("1"));
                    };
                } else {
                    return switch (param) {
                        case "damage" -> String.valueOf(caster.getDamage());
                        case "hp" -> String.valueOf(caster.getMaxHealth());
                        case "def" -> String.valueOf(new EntityStatManager(caster.getBukkitEntity()).getDoubleStat("DEFENSE"));
                        default -> (String) (skillMetadata.getMetadata("SNAPSHOT_DATA_" + param.toLowerCase()).orElse("1"));
                    };
                }
            }


            return switch (param) {
                case "damage" -> String.valueOf(caster.getDamage());
                case "hp" -> String.valueOf(caster.getMaxHealth());
                case "def" -> String.valueOf(new EntityStatManager(caster.getBukkitEntity()).getDoubleStat("DEFENSE"));
                default -> "1";
            };
        }));
    }
}
