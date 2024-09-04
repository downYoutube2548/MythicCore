package com.dev.mythiccore.utils;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.stats.BooleanStatRegister;
import com.dev.mythiccore.stats.DoubleStatInternal;
import com.dev.mythiccore.stats.DoubleStatRegister;
import com.dev.mythiccore.visuals.HealthBarSettings;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.element.Element;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ConfigLoader {

    private static String defaultElement;
    private static final Map<String, String> elementalModifier = new HashMap<>();
    private static List<String> auraWhitelist;
    private static final HashMap<String, Map<String, Integer>> reactionPriority = new HashMap<>();

    private static final HashMap<String, DoubleStatRegister> doubleStats = new HashMap<>();
    private static final HashMap<String, BooleanStatRegister> booleanStats = new HashMap<>();

    public static void loadConfig() {
        FileConfiguration config = MythicCore.getInstance().getConfig();

        defaultElement = config.getString("General.default-element");
        auraWhitelist = config.getStringList("General.aura-whitelist");

        for (String damageCause : Objects.requireNonNull(config.getConfigurationSection("Elemental-Modifier")).getKeys(false)) {
            elementalModifier.put(damageCause, config.getString("Elemental-Modifier."+damageCause));
        }

        for (String element : Objects.requireNonNull(config.getConfigurationSection("Reaction-Priority")).getKeys(false)) {
            Map<String, Integer> priority = new HashMap<>();
            int i = 1;
            for (String reaction : config.getStringList("Reaction-Priority."+element)) {
                priority.put(reaction, i);
                i++;
            }
            reactionPriority.put(element, priority);
        }
    }

    public static void reloadConfig() {
        MythicCore.getInstance().reloadConfig();
        loadConfig();
    }

    public static boolean isReactionEnable(String reaction_id) {
        return MythicCore.getInstance().getConfig().getBoolean("Elemental-Reaction." + reaction_id + ".enable");
    }
    public static boolean isDendroCoreReactionEnable(String reaction_id) {
        return MythicCore.getInstance().getConfig().getBoolean("Elemental-Reaction.BLOOM.sub-reaction." + reaction_id + ".enable");
    }

    public static List<String> getReactionPriorityList(String element) {
        return MythicCore.getInstance().getConfig().getStringList("Reaction-Priority."+element);
    }

    public static String getAuraElement(String reaction_id) {
        return MythicCore.getInstance().getConfig().getString("Elemental-Reaction."+reaction_id+".aura-element");
    }

    public static String getReactionDisplay(String reaction_id) {
        return MythicCore.getInstance().getConfig().getString("Elemental-Reaction."+reaction_id+".display");
    }
    public static String getTriggerElement(String reaction_id) {
        return MythicCore.getInstance().getConfig().getString("Elemental-Reaction."+reaction_id+".trigger-element");
    }
    public static double getGaugeUnitTax(String reaction_id) {
        return MythicCore.getInstance().getConfig().getDouble("Elemental-Reaction."+reaction_id+".gauge-unit-tax");
    }
    public static Long getDecayRate(String suffix) {
        return MythicCore.getInstance().getConfig().getLong("General.decay-rate."+suffix, 200);
    }
    public static Double getDefaultGaugeUnit() { return Double.parseDouble(Objects.requireNonNull(MythicCore.getInstance().getConfig().getString("General.default-gauge-unit")).split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0]); }
    public static String getDefaultDecayRate() { return Objects.requireNonNull(MythicCore.getInstance().getConfig().getString("General.default-gauge-unit")).split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[1]; }
    public static @NotNull String getDefaultGauge() { return MythicCore.getInstance().getConfig().getString("General.default-gauge-unit", "1A"); }
    public static String getSpecialAuraIcon(String aura_id) {
        return MythicCore.getInstance().getConfig().getString("Special-Aura."+aura_id+".icon");
    }
    public static String getSpecialAuraColor(String aura_id) {
        return MythicCore.getInstance().getConfig().getString("Special-Aura."+aura_id+".color");
    }

    public static String getDefaultDamageCalculation() {
        return MythicCore.getInstance().getConfig().getString("General.default-damage-calculation");
    }

    public static String getDamageCalculation(String section) {
        return MythicCore.getInstance().getConfig().getString("Damage-Calculation."+section);
    }
    public static Long getInternalCooldown(String source) {
        return MythicCore.getInstance().getConfig().getLong("General.internal-cooldown."+source);
    }
    public static ConfigurationSection getReactionConfig() {
        return MythicCore.getInstance().getConfig().getConfigurationSection("Elemental-Reaction");
    }
    public static long getReactionFrequency(String reaction_id) {
        return MythicCore.getInstance().getConfig().getLong("Elemental-Reaction."+reaction_id+".frequency");
    }
    public static boolean aoeDamageFilterEnable() {
        return MythicCore.getInstance().getConfig().getBoolean("General.aoe-damage-filter");
    }

    public static String getDefaultElement() {
        return defaultElement;
    }
    public static Map<String, String> getElementalModifier() { return elementalModifier; }
    public static List<String> getAuraWhitelist() { return auraWhitelist; }
    public static void registerBooleanStats(ConfigurationSection stats) {
        if (stats != null) {
            for (String stat : stats.getKeys(false)) {
                ConfigurationSection section = stats.getConfigurationSection(stat);
                if (section != null) {
                    BooleanStatRegister booleanStat = new BooleanStatRegister(section.getName(), Material.getMaterial(section.getString("Icon.Material", "STONE")), section.getString("Icon.Name"), section.getStringList("Icon.Lore").toArray(new String[0]));
                    MMOItems.plugin.getStats().register(booleanStat);
                    booleanStats.put(booleanStat.getId(), booleanStat);
                }
            }
        }
    }

    public static String getMessage(String path, boolean prefix) {
        return prefix ? Utils.colorize(MythicCore.getInstance().getConfig().getString("message.prefix")+MythicCore.getInstance().getConfig().getString("message."+path)) : Utils.colorize(MythicCore.getInstance().getConfig().getString(path));
    }

    public static void registerDoubleStats(ConfigurationSection stats) {

        if (stats != null) {

            for (Element element : MythicLib.plugin.getElements().getAll()) {
                DoubleStatInternal doubleStat1 = new DoubleStatInternal("AST_"+element.getId()+"_PERCENT", Material.STONE, element.getName()+" Percent", new String[]{});
                DoubleStatInternal doubleStat2 = new DoubleStatInternal("AST_"+element.getId()+"_DAMAGE_BONUS", Material.STONE, element.getName()+" Damage Bonus", new String[]{});
                DoubleStatInternal doubleStat3 = new DoubleStatInternal("AST_"+element.getId()+"_RESISTANCE", Material.STONE, element.getName()+" Resistance", new String[]{});
                MMOItems.plugin.getStats().register(doubleStat1);
                MMOItems.plugin.getStats().register(doubleStat2);
                MMOItems.plugin.getStats().register(doubleStat3);

            }

            for (String stat : stats.getKeys(false)) {
                ConfigurationSection section = stats.getConfigurationSection(stat);
                if (section != null) {
                    DoubleStatRegister doubleStat = new DoubleStatRegister(section.getName(), Material.getMaterial(section.getString("Icon.Material", "STONE")), section.getString("Icon.Name"), section.getStringList("Icon.Lore").toArray(new String[0]));
                    MMOItems.plugin.getStats().register(doubleStat);
                    doubleStats.put(doubleStat.id, doubleStat);

                }
            }
        }
    }

    @SuppressWarnings("DataFlowIssue")
    public static Map<String, String> getTextReplace() {
        ConfigurationSection config = MythicCore.getInstance().getConfig().getConfigurationSection("Health-Bars.Replacement");
        Map<String, String> replacements = new HashMap<>();
        for (String key : config.getKeys(false)) {
            replacements.put(key, config.getString(key));
        }
        return replacements;
    }

    public static HealthBarSettings getDefaultHealthBar() {
        ConfigurationSection config = MythicCore.getInstance().getConfig().getConfigurationSection("Health-Bars.Default");
        return new HealthBarSettings(
                config.getDouble("HeightOffset"),
                config.getInt("HologramDuration"),
                config.getStringList("Lines").toArray(new String[0]),
                config.getString("Bar.Prefix"),
                config.getString("Bar.Suffix"),
                config.getString("Bar.Filler"),
                config.getString("Bar.HealthFiller"),
                config.getString("Bar.DamagedFiller"),
                config.getString("Bar.SeparateFiller"),
                config.getInt("Bar.Width"),
                config.getString("Bar.FillerColor"),
                config.getString("Bar.HealthColor"),
                config.getString("Bar.DamagedColor"),
                config.getBoolean("Enabled", true),
                config.getBoolean("Display", true)
        );
    }

    public static Map<String, HealthBarSettings> getCustomHealthBars() {
        Map<String, HealthBarSettings> bars = new HashMap<>();
        ConfigurationSection config = MythicCore.getInstance().getConfig().getConfigurationSection("Health-Bars.Custom");
        for (String key : config.getKeys(false)) {
            HealthBarSettings settings = new HealthBarSettings(
                    config.getDouble(key + ".HeightOffset"),
                    config.getInt(key + ".HologramDuration"),
                    config.getStringList(key + ".Lines").toArray(new String[0]),
                    config.getString(key + ".Bar.Prefix"),
                    config.getString(key + ".Bar.Suffix"),
                    config.getString(key + ".Bar.Filler"),
                    config.getString(key + ".Bar.HealthFiller"),
                    config.getString(key + ".Bar.DamagedFiller"),
                    config.getString(key + ".Bar.SeparateFiller"),
                    config.getInt(key + ".Bar.Width"),
                    config.getString(key + ".Bar.FillerColor"),
                    config.getString(key + ".Bar.HealthColor"),
                    config.getString(key + ".Bar.DamagedColor"),
                    config.getBoolean(key + ".Enabled", true),
                    config.getBoolean(key + ".Display", true)
            );
            bars.put(key, settings);
        }
        return bars;
    }

    public static int getHealthBarUpdateRate() {
        return MythicCore.getInstance().getConfig().getInt("Health-Bars.Default.UpdateRate", 1);
    }
}
