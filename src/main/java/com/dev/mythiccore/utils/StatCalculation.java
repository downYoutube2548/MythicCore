package com.dev.mythiccore.utils;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.buff.buffs.DefenseReduction;
import com.dev.mythiccore.buff.buffs.ElementalResistanceReduction;
import com.dev.mythiccore.enums.VictimType;
import com.dev.mythiccore.library.ASTEntityStatProvider;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.lib.api.stat.provider.StatProvider;
import io.lumine.mythic.lib.damage.DamagePacket;
import io.lumine.mythic.lib.player.PlayerMetadata;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.stats.PlayerStats;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class StatCalculation {

    public static double getFinalDamage(UUID victim, String damage_formula, DamagePacket damage_packet, double weapon_bonus) {
        String formula = ConfigLoader.getDamageCalculation("final-damage");

        double totalDamage = getTotalDamage(damage_formula, damage_packet.getValue(), 100, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        double defense = getDefenseMultiplier(victim);
        double resistance = damage_packet.getElement() != null ? getResistanceMultiplier(victim, damage_packet.getElement().getId()) : 1;
        double level = getLevelDifferentMultiplier(victim, victim);

        //Bukkit.broadcastMessage(totalDamage+"\n"+defense+"\n"+resistance+"\n"+level);

        Expression expression = new ExpressionBuilder(formula)
                .variables("total_damage", "defense_multiplier", "resistance_multiplier", "level_multiplier", "weapon_bonus")
                .build()
                .setVariable("total_damage", totalDamage)
                .setVariable("defense_multiplier", defense)
                .setVariable("resistance_multiplier", resistance)
                .setVariable("level_multiplier", level)
                .setVariable("weapon_bonus", weapon_bonus);

        return expression.evaluate();
    }

    public static double getFinalDamage(StatProvider attacker, UUID victim, String damage_formula, double talent_percent, DamagePacket damage_packet, boolean crit, double weapon_bonus) {

        double total_damage = getTotalDamage(attacker, damage_formula, talent_percent, damage_packet, crit);
        double defense_multiplier = getDefenseMultiplier(attacker, victim);
        double resistance_multiplier = damage_packet.getElement() != null ? getResistanceMultiplier(victim, damage_packet.getElement().getId()) : 1;
        double level_multiplier = getLevelDifferentMultiplier(attacker, victim);

        //Bukkit.broadcastMessage(total_damage+" "+defense_multiplier+" "+resistance_multiplier+" "+level_multiplier);

        String formula = ConfigLoader.getDamageCalculation("final-damage");
        Expression expression = new ExpressionBuilder(formula)
                .variables("total_damage", "defense_multiplier", "resistance_multiplier", "level_multiplier", "weapon_bonus")
                .build()
                .setVariable("total_damage", total_damage)
                .setVariable("defense_multiplier", defense_multiplier)
                .setVariable("resistance_multiplier", resistance_multiplier)
                .setVariable("level_multiplier", level_multiplier)
                .setVariable("weapon_bonus", weapon_bonus);

        return expression.evaluate();
    }

    public static double getFinalDamage(UUID attacker, UUID victim, String damage_formula, double talent_percent, DamagePacket damage_packet, boolean crit) {
        String formula = ConfigLoader.getDamageCalculation("final-damage");
        Expression expression = new ExpressionBuilder(formula)
                .variables("total_damage", "defense_multiplier", "resistance_multiplier", "level_multiplier")
                .build()
                .setVariable("total_damage", getTotalDamage(attacker, damage_formula, talent_percent, damage_packet, crit))
                .setVariable("defense_multiplier", getDefenseMultiplier(attacker, victim))
                .setVariable("resistance_multiplier", damage_packet.getElement() != null ? getResistanceMultiplier(victim, damage_packet.getElement().getId()) : 1)
                .setVariable("level_multiplier", getLevelDifferentMultiplier(attacker, victim));

        return expression.evaluate();
    }

    public static double getTotalDamage(StatProvider attacker, String damage_formula, double talent_percent, DamagePacket damage_packet, boolean crit) {
        double damage_amount = damage_packet.getValue();
        double attack_buff_percent = 0;
        double attack_buff = 0;
        double defense_buff = 0;
        double defense_buff_percent = 0;
        double health_buff = 0;
        double health_buff_percent = 0;
        double elemental_mastery = 0;
        double elemental_damage_bonus = 0;
        double all_elemental_damage_bonus = 0;

        if (attacker instanceof PlayerMetadata playerStats) {

            damage_amount = crit ? damage_packet.getValue() * (1 + (playerStats.getStat("AST_CRITICAL_DAMAGE")/100)) : damage_packet.getValue();
            attack_buff = playerStats.getStat("AST_ATTACK_DAMAGE_BUFF");
            attack_buff_percent = playerStats.getStat("AST_ATTACK_DAMAGE_BUFF_PERCENT");
            defense_buff = playerStats.getStat("AST_DEFENSE_BUFF");
            defense_buff_percent = playerStats.getStat("AST_DEFENSE_BUFF_PERCENT");
            health_buff = playerStats.getStat("AST_MAX_HEALTH_BUFF");
            health_buff_percent = playerStats.getStat("AST_MAX_HEALTH_BUFF_PERCENT");
            elemental_mastery = playerStats.getStat("AST_ELEMENTAL_MASTERY");
            elemental_damage_bonus = (damage_packet.getElement() == null) ? 0 : playerStats.getStat("AST_"+damage_packet.getElement().getId()+"_DAMAGE_BONUS");
            all_elemental_damage_bonus = playerStats.getStat("AST_ALL_ELEMENTAL_DAMAGE_BONUS");
        }
        return getTotalDamage(damage_formula, damage_amount, talent_percent, attack_buff_percent, attack_buff, defense_buff, defense_buff_percent, health_buff, health_buff_percent, elemental_mastery, elemental_damage_bonus, all_elemental_damage_bonus);
    }

    public static double getTotalDamage(UUID uuid, String damage_formula, double talent_percent, DamagePacket damage_packet, boolean crit) {
        Entity entity = Bukkit.getEntity(uuid);
        if (entity == null) return 0;
        if (!entity.isValid()) return 0;

        double damage_amount = damage_packet.getValue();
        double attack_buff_percent = 0;
        double attack_buff = 0;
        double defense_buff = 0;
        double defense_buff_percent = 0;
        double health_buff = 0;
        double health_buff_percent = 0;
        double elemental_mastery = 0;
        double elemental_damage_bonus = 0;
        double all_elemental_damage_bonus = 0;

        if (entity instanceof Player player) {
            PlayerData playerData = PlayerData.get(player);
            PlayerStats playerStats = playerData.getStats();

            damage_amount = crit ? damage_packet.getValue() * (1 + (playerStats.getStat("AST_CRITICAL_DAMAGE")/100)) : damage_packet.getValue();
            attack_buff = playerStats.getStat("AST_ATTACK_DAMAGE_BUFF");
            attack_buff_percent = playerStats.getStat("AST_ATTACK_DAMAGE_BUFF_PERCENT");
            defense_buff = playerStats.getStat("AST_DEFENSE_BUFF");
            defense_buff_percent = playerStats.getStat("AST_DEFENSE_BUFF_PERCENT");
            health_buff = playerStats.getStat("AST_MAX_HEALTH_BUFF");
            health_buff_percent = playerStats.getStat("AST_MAX_HEALTH_BUFF_PERCENT");
            elemental_mastery = playerStats.getStat("AST_ELEMENTAL_MASTERY");
            elemental_damage_bonus = (damage_packet.getElement() == null) ? 0 : playerStats.getStat("AST_"+damage_packet.getElement().getId()+"_DAMAGE_BONUS");
            all_elemental_damage_bonus = playerStats.getStat("AST_ALL_ELEMENTAL_DAMAGE_BONUS");
        }
        return getTotalDamage(damage_formula, damage_amount, talent_percent, attack_buff_percent, attack_buff, defense_buff, defense_buff_percent, health_buff, health_buff_percent, elemental_mastery, elemental_damage_bonus, all_elemental_damage_bonus);
    }

    public static double getTotalDamage(String damage_formula, double damage_amount, double talent_percent, double attack_buff_percent, double attack_buff, double defense_buff, double defense_buff_percent, double health_buff, double health_buff_percent, double elemental_mastery, double elemental_damage_bonus, double all_elemental_damage_bonus) {

        String formula = ConfigLoader.getDamageCalculation("damage-calculation-formula."+damage_formula+".formula");
        Expression expression = new ExpressionBuilder(formula)
                .variables("damage", "talent_percent", "attack_buff_percent", "attack_buff", "defense_buff", "defense_buff_percent", "health_buff", "health_buff_percent", "elemental_mastery", "elemental_damage_bonus", "all_elemental_damage_bonus")
                .build()
                .setVariable("damage", damage_amount)
                .setVariable("talent_percent", talent_percent)
                .setVariable("attack_buff_percent", attack_buff_percent)
                .setVariable("attack_buff", attack_buff)
                .setVariable("defense_buff", defense_buff)
                .setVariable("defense_buff_percent", defense_buff_percent)
                .setVariable("health_buff", health_buff)
                .setVariable("health_buff_percent", health_buff_percent)
                .setVariable("elemental_mastery", elemental_mastery)
                .setVariable("elemental_damage_bonus", elemental_damage_bonus)
                .setVariable("all_elemental_damage_bonus", all_elemental_damage_bonus);

        return expression.evaluate();
    }

    public static double getDefenseMultiplier(UUID victim) {
        return getDefenseMultiplier(victim, 0, 1);
    }

    public static double getDefenseMultiplier(StatProvider attacker, UUID victim) {

        double ignore_defense = 0;
        int level = 1;
        if (attacker instanceof PlayerMetadata playerStats) {

            ignore_defense = playerStats.getStat("AST_IGNORE_DEFENSE");
            level = PlayerData.get(playerStats.getPlayer()).getLevel();
        } else if (attacker instanceof ASTEntityStatProvider entityStatProvider) {
            ActiveMob mythicMob = MythicBukkit.inst().getMobManager().getActiveMob(entityStatProvider.getEntity().getUniqueId()).orElse(null);
            level = mythicMob != null ? (int) mythicMob.getLevel() : 1 ;
        }

        return getDefenseMultiplier(victim, ignore_defense, level);
    }

    public static double getDefenseMultiplier(UUID attacker, UUID victim) {
        Entity entity = Bukkit.getEntity(attacker);
        if (entity == null) return 0;
        if (!entity.isValid()) return 0;

        double ignore_defense = 0;
        int level;
        if (entity instanceof Player player) {
            PlayerData playerData = PlayerData.get(player);
            PlayerStats playerStats = playerData.getStats();

            ignore_defense = playerStats.getStat("AST_IGNORE_DEFENSE");
            level = playerData.getLevel();
        } else {
            ActiveMob mythicMob = MythicBukkit.inst().getMobManager().getActiveMob(attacker).orElse(null);
            level = mythicMob != null ? (int) mythicMob.getLevel() : 1 ;
        }

        return getDefenseMultiplier(victim, ignore_defense, level);
    }

    public static double getDefenseMultiplier(UUID uuid, double ignore_defense, int attacker_level) {
        Entity entity = Bukkit.getEntity(uuid);
        if (entity == null) return 0;
        if (!entity.isValid()) return 0;

        double defense = getDefense(uuid);
        //Bukkit.broadcastMessage("DEFENSE: "+defense);
        return getDefenseMultiplier(defense, ignore_defense, attacker_level);
    }

    public static double getDefenseMultiplier(double defense, double ignore_defense, int attacker_level) {

        String formula = ConfigLoader.getDamageCalculation("defense-multiplier");
        Expression expression = new ExpressionBuilder(formula)
                .variables("attacker_ignore_defense", "victim_defense", "attacker_level")
                .build()
                .setVariable("attacker_ignore_defense", ignore_defense)
                .setVariable("victim_defense", defense)
                .setVariable("attacker_level", attacker_level);

        return expression.evaluate();
    }

    public static double getResistanceMultiplier(UUID uuid, String element) {
        Entity entity = Bukkit.getEntity(uuid);
        if (entity == null) return 0;
        if (!entity.isValid()) return 0;

        double elemental_resistance = getResistance(uuid, element);

        return getResistanceMultiplier(elemental_resistance);
    }

    public static double getResistanceMultiplier(Entity entity, String element) {
        double elemental_resistance = getResistance(entity, element);
        return getResistanceMultiplier(elemental_resistance);
    }

    public static double getResistanceMultiplier(double elemental_resistance) {

        String formula = "0";
        for (String s : Objects.requireNonNull(MythicCore.getInstance().getConfig().getConfigurationSection("Damage-Calculation.resistance-multiplier")).getKeys(false)) {
            if (s.contains("<=")) {
                if (elemental_resistance <= Double.parseDouble(s.split("<=")[1])) {
                    formula = ConfigLoader.getDamageCalculation("resistance-multiplier."+s);
                    break;
                }
            } else if (s.contains(">=")) {
                if (elemental_resistance >= Double.parseDouble(s.split(">=")[1])) {
                    formula = ConfigLoader.getDamageCalculation("resistance-multiplier."+s);
                    break;
                }
            } else if (s.contains("<")) {
                if (elemental_resistance < Double.parseDouble(s.split("<")[1])) {
                    formula = ConfigLoader.getDamageCalculation("resistance-multiplier."+s);
                    break;
                }
            } else if (s.contains(">")) {
                if (elemental_resistance > Double.parseDouble(s.split(">")[1])) {
                    formula = ConfigLoader.getDamageCalculation("resistance-multiplier."+s);
                    break;
                }
            } else if (s.contains("!=")) {
                if (elemental_resistance != Double.parseDouble(s.split("!=")[1])) {
                    formula = ConfigLoader.getDamageCalculation("resistance-multiplier."+s);
                    break;
                }
            } else if (s.contains("=")) {
                if (elemental_resistance == Double.parseDouble(s.split("=")[1])) {
                    formula = ConfigLoader.getDamageCalculation("resistance-multiplier."+s);
                    break;
                }
            }
        }
        Expression expression = new ExpressionBuilder(formula)
                .variables("elemental_resistance")
                .build()
                .setVariable("elemental_resistance", elemental_resistance);

        return expression.evaluate();
    }

    public static double getLevelDifferentMultiplier(UUID victim) {
        return getLevelDifferentMultiplier(1, victim);
    }

    public static double getLevelDifferentMultiplier(int attacker_level, UUID victim) {
        Entity entity = Bukkit.getEntity(victim);
        if (entity == null) return 0;
        if (!entity.isValid()) return 0;

        int level;
        if (entity instanceof Player player) {
            PlayerData playerData = PlayerData.get(player);
            level = playerData.getLevel();
        } else {
            ActiveMob mythicMob = MythicBukkit.inst().getMobManager().getActiveMob(victim).orElse(null);
            level = mythicMob != null ? (int) mythicMob.getLevel() : 1;
        }

        //Bukkit.broadcastMessage("LEVEL: "+level);

        return getLevelDifferentMultiplier(entity instanceof Player ? VictimType.PLAYER : VictimType.MOB, attacker_level, level);
    }

    public static double getLevelDifferentMultiplier(StatProvider attacker, UUID victim) {
        if (attacker instanceof PlayerMetadata playerMetadata) {
            return getLevelDifferentMultiplier(playerMetadata.getPlayer().getUniqueId(), victim);
        } else if (attacker instanceof ASTEntityStatProvider entityStatProvider) {
            return getLevelDifferentMultiplier(entityStatProvider.getEntity().getUniqueId(), victim);
        } else {
            return getLevelDifferentMultiplier(victim);
        }
    }

    public static double getLevelDifferentMultiplier(UUID attacker, UUID victim) {

        Entity entity1 = Bukkit.getEntity(attacker);
        if (entity1 == null) return 0;
        if (!entity1.isValid()) return 0;

        Entity entity2 = Bukkit.getEntity(victim);
        if (entity2 == null) return 0;
        if (!entity2.isValid()) return 0;

        int attacker_level;
        int victim_level;
        if (entity1 instanceof Player player) {
            PlayerData playerData = PlayerData.get(player);
            attacker_level = playerData.getLevel();
        } else {
            ActiveMob mythicMob = MythicBukkit.inst().getMobManager().getActiveMob(attacker).orElse(null);
            attacker_level = mythicMob != null ? (int) mythicMob.getLevel() : 1;
        }
        if (entity2 instanceof Player player) {
            PlayerData playerData = PlayerData.get(player);
            victim_level = playerData.getLevel();
        } else {
            ActiveMob mythicMob = MythicBukkit.inst().getMobManager().getActiveMob(victim).orElse(null);
            victim_level = mythicMob != null ? (int) mythicMob.getLevel() : 1;
        }

        return getLevelDifferentMultiplier(entity2 instanceof Player ? VictimType.PLAYER : VictimType.MOB, attacker_level, victim_level);
    }

    public static double getLevelDifferentMultiplier(VictimType victimType, int attacker_level, int victim_level) {

        String formula = victimType.equals(VictimType.PLAYER) ? ConfigLoader.getDamageCalculation("level-multiplier.player") : ConfigLoader.getDamageCalculation("level-multiplier.mob");
        Expression expression = new ExpressionBuilder(formula)
                .variables("attacker_level", "victim_level")
                .build()
                .setVariable("attacker_level", attacker_level)
                .setVariable("victim_level", victim_level);

        return expression.evaluate();
    }

    public static double getResistance(Entity entity, String element) {

        ElementalResistanceReduction er = MythicCore.getBuffManager().getBuff(entity.getUniqueId()).getActivateBuff(ElementalResistanceReduction.class, new String[]{"element"}, new String[]{element});
        double elemental_resistance;

        if (entity instanceof Player player) {
            PlayerData playerData = PlayerData.get(player);
            PlayerStats playerStats = playerData.getStats();

            elemental_resistance = playerStats.getStat("AST_"+element+"_RESISTANCE") + playerStats.getStat("AST_ALL_ELEMENTAL_RESISTANCE");
        } else {
            EntityStatManager entityStat = new EntityStatManager(entity);
            elemental_resistance = entityStat.getDoubleStat("AST_"+element+"_RESISTANCE") + entityStat.getDoubleStat("AST_ALL_ELEMENTAL_RESISTANCE");
        }

        return getResistance(elemental_resistance, er == null ? 0 : er.getAmount());
    }

    public static double getResistance(UUID uuid, String element) {
        Entity entity = Bukkit.getEntity(uuid);
        if (entity == null) return 0;
        if (!entity.isValid()) return 0;

        ElementalResistanceReduction er = MythicCore.getBuffManager().getBuff(uuid).getActivateBuff(ElementalResistanceReduction.class, new String[]{"element"}, new String[]{element});
        double elemental_resistance;

        if (entity instanceof Player player) {
            PlayerData playerData = PlayerData.get(player);
            PlayerStats playerStats = playerData.getStats();

            elemental_resistance = playerStats.getStat("AST_"+element+"_RESISTANCE") + playerStats.getStat("AST_ALL_ELEMENTAL_RESISTANCE");
        } else {
            EntityStatManager entityStat = new EntityStatManager(entity);
            elemental_resistance = entityStat.getDoubleStat("AST_"+element+"_RESISTANCE") + entityStat.getDoubleStat("AST_ALL_ELEMENTAL_RESISTANCE");
        }

        return getResistance(elemental_resistance, er == null ? 0 : er.getAmount());
    }

    public static double getResistance(double pure_resistance, double resistance_reduction) {

        String formula = ConfigLoader.getDamageCalculation("total-resistance");
        Expression expression = new ExpressionBuilder(formula)
                .variables("pure_resistance", "resistance_reduction")
                .build()
                .setVariable("pure_resistance", pure_resistance)
                .setVariable("resistance_reduction", resistance_reduction);

        return expression.evaluate();
    }

    public static double getDefense(UUID uuid) {
        Entity entity = Bukkit.getEntity(uuid);
        if (entity == null) return 0;
        if (!entity.isValid()) return 0;

        DefenseReduction dr = MythicCore.getBuffManager().getBuff(uuid).getActivateBuff(DefenseReduction.class, new String[]{}, new String[]{});
        double defense;

        if (entity instanceof Player player) {
            PlayerData playerData = PlayerData.get(player);
            PlayerStats playerStats = playerData.getStats();

            defense = playerStats.getStat("DEFENSE");
        } else {
            EntityStatManager entityStat = new EntityStatManager(entity);
            defense = entityStat.getDoubleStat("DEFENSE");
        }

        return defense - (dr == null ? 0 : dr.getAmount()/100 * defense);
    }
}

