package com.dev.mythiccore;

import com.dev.mythiccore.aura.Aura;
import com.dev.mythiccore.buff.Buff;
import com.dev.mythiccore.combat.Combat;
import com.dev.mythiccore.commands.core;
import com.dev.mythiccore.cooldown.InternalCooldown;
import com.dev.mythiccore.events.MythicMechanicLoad;
import com.dev.mythiccore.events.PlayerDeath;
import com.dev.mythiccore.events.ProjectileLaunch;
import com.dev.mythiccore.events.attack_handle.AttackModifier;
import com.dev.mythiccore.events.attack_handle.CancelFireTick;
import com.dev.mythiccore.events.attack_handle.RemoveVanillaDamage;
import com.dev.mythiccore.events.attack_handle.attack_priority.ShieldRefutation;
import com.dev.mythiccore.events.attack_handle.attack_priority.TriggerReaction;
import com.dev.mythiccore.events.attack_handle.deal_damage.MiscAttack;
import com.dev.mythiccore.events.attack_handle.deal_damage.MobAttack;
import com.dev.mythiccore.events.attack_handle.deal_damage.PlayerAttack;
import com.dev.mythiccore.listener.AttackEventListener;
import com.dev.mythiccore.reaction.ReactionManager;
import com.dev.mythiccore.reaction.reactions.*;
import com.dev.mythiccore.reaction.reactions.frozen.FreezeActionCanceling;
import com.dev.mythiccore.reaction.reactions.frozen.Frozen;
import com.dev.mythiccore.stats.GaugeUnitStat;
import com.dev.mythiccore.stats.InternalCooldownStat;
import com.dev.mythiccore.utils.ConfigLoader;
import com.dev.mythiccore.visuals.ASTDamageIndicators;
import com.dev.mythiccore.visuals.AuraVisualizer;
import com.google.common.io.ByteStreams;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public final class MythicCore extends JavaPlugin {

    /**
     * API Docs:
     * ExpressionBuilder:
     * <a href="https://www.objecthunter.net/exp4j/apidocs/index.html">...</a>
     */

    // TODO: เลียงตามลำดับ
    //  1. Level Difference Multiplier (Done)
    //  2. Elemental Inflection (Done)
    //  3. Resistance Reduction & Defense Reduction (Done)
    //  4. Entity Elemental Inflection Status (Done)
    //  5. Configurable Damage Equation (Done)
    //  6. Elemental Reaction
    //  7. More...

    private static MythicCore instance;
    private static Aura aura;
    private static Buff buff;
    private static InternalCooldown cooldown;
    private static ReactionManager reactionManager;

    @Override
    public void onEnable() {
        instance = this;
        aura = new Aura();
        buff = new Buff();
        cooldown = new InternalCooldown();
        reactionManager = new ReactionManager();
        loadResource(this, "config.yml");
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        ConfigLoader.loadConfig();
        aura.startTick();
        buff.startTick();
        cooldown.startTick();
        AuraVisualizer.start();

        Objects.requireNonNull(Bukkit.getPluginCommand("mythiccore")).setExecutor(new core());

        //Register EventListener
        Bukkit.getPluginManager().registerEvents(new MythicMechanicLoad(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerAttack(), this);
        Bukkit.getPluginManager().registerEvents(new MobAttack(), this);
        Bukkit.getPluginManager().registerEvents(new MiscAttack(), this);
        Bukkit.getPluginManager().registerEvents(new AttackModifier(), this);
        Bukkit.getPluginManager().registerEvents(new AttackEventListener(), this);
        Bukkit.getPluginManager().registerEvents(new CancelFireTick(), this);
        Bukkit.getPluginManager().registerEvents(new ASTDamageIndicators(getConfig().getConfigurationSection("Indicators")), this);
        Bukkit.getPluginManager().registerEvents(new RemoveVanillaDamage(), this);
        Bukkit.getPluginManager().registerEvents(new Combat(), this);
        Bukkit.getPluginManager().registerEvents(new FreezeActionCanceling(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeath(), this);
        Bukkit.getPluginManager().registerEvents(new ProjectileLaunch(), this);
        Bukkit.getPluginManager().registerEvents(new TriggerReaction(), this);
        Bukkit.getPluginManager().registerEvents(new ShieldRefutation(), this);

        MMOItems.plugin.getStats().register(new GaugeUnitStat());
        MMOItems.plugin.getStats().register(new InternalCooldownStat());

        if (ConfigLoader.isReactionEnable("OVERLOADED")) getReactionManager().registerElementalReaction(new Overloaded("OVERLOADED", ConfigLoader.getReactionDisplay("OVERLOADED"), ConfigLoader.getAuraElement("OVERLOADED"), ConfigLoader.getTriggerElement("OVERLOADED"), ConfigLoader.getGaugeUnitTax("OVERLOADED")));
        if (ConfigLoader.isReactionEnable("REVERSE_OVERLOADED")) getReactionManager().registerElementalReaction(new Overloaded("REVERSE_OVERLOADED", ConfigLoader.getReactionDisplay("REVERSE_OVERLOADED"), ConfigLoader.getAuraElement("REVERSE_OVERLOADED"), ConfigLoader.getTriggerElement("REVERSE_OVERLOADED"), ConfigLoader.getGaugeUnitTax("REVERSE_OVERLOADED")));
        if (ConfigLoader.isReactionEnable("VAPORIZE")) getReactionManager().registerElementalReaction(new Vaporize("VAPORIZE", ConfigLoader.getReactionDisplay("VAPORIZE"), ConfigLoader.getAuraElement("VAPORIZE"), ConfigLoader.getTriggerElement("VAPORIZE"), ConfigLoader.getGaugeUnitTax("VAPORIZE")));
        if (ConfigLoader.isReactionEnable("REVERSE_VAPORIZE")) getReactionManager().registerElementalReaction(new Vaporize("REVERSE_VAPORIZE", ConfigLoader.getReactionDisplay("REVERSE_VAPORIZE"), ConfigLoader.getAuraElement("REVERSE_VAPORIZE"), ConfigLoader.getTriggerElement("REVERSE_VAPORIZE"), ConfigLoader.getGaugeUnitTax("REVERSE_VAPORIZE")));
        if (ConfigLoader.isReactionEnable("MELT")) getReactionManager().registerElementalReaction(new Melt("MELT", ConfigLoader.getReactionDisplay("MELT"), ConfigLoader.getAuraElement("MELT"), ConfigLoader.getTriggerElement("MELT"), ConfigLoader.getGaugeUnitTax("MELT")));
        if (ConfigLoader.isReactionEnable("REVERSE_MELT")) getReactionManager().registerElementalReaction(new Melt("REVERSE_MELT", ConfigLoader.getReactionDisplay("REVERSE_MELT"), ConfigLoader.getAuraElement("REVERSE_MELT"), ConfigLoader.getTriggerElement("REVERSE_MELT"), ConfigLoader.getGaugeUnitTax("REVERSE_MELT")));
        if (ConfigLoader.isReactionEnable("SUPER_CONDUCT")) getReactionManager().registerElementalReaction(new SuperConduct("SUPER_CONDUCT", ConfigLoader.getReactionDisplay("SUPER_CONDUCT"), ConfigLoader.getAuraElement("SUPER_CONDUCT"), ConfigLoader.getTriggerElement("SUPER_CONDUCT"), ConfigLoader.getGaugeUnitTax("SUPER_CONDUCT")));
        if (ConfigLoader.isReactionEnable("REVERSE_SUPER_CONDUCT")) getReactionManager().registerElementalReaction(new SuperConduct("REVERSE_SUPER_CONDUCT", ConfigLoader.getReactionDisplay("REVERSE_SUPER_CONDUCT"), ConfigLoader.getAuraElement("REVERSE_SUPER_CONDUCT"), ConfigLoader.getTriggerElement("REVERSE_SUPER_CONDUCT"), ConfigLoader.getGaugeUnitTax("REVERSE_SUPER_CONDUCT")));
        if (ConfigLoader.isReactionEnable("ELECTRO_CHARGED")) getReactionManager().registerElementalReaction(new ElectroCharged("ELECTRO_CHARGED", ConfigLoader.getReactionDisplay("ELECTRO_CHARGED"), ConfigLoader.getReactionConfig().getString("ELECTRO_CHARGED.first-aura-element"), ConfigLoader.getReactionConfig().getString("ELECTRO_CHARGED.second-aura-element"), ConfigLoader.getReactionFrequency("ELECTRO_CHARGED"), ConfigLoader.getGaugeUnitTax("ELECTRO_CHARGED")));
        if (ConfigLoader.isReactionEnable("FROZEN")) getReactionManager().registerElementalReaction(new Frozen("FROZEN", ConfigLoader.getReactionDisplay("FROZEN"), ConfigLoader.getAuraElement("FROZEN"), ConfigLoader.getTriggerElement("FROZEN"), ConfigLoader.getGaugeUnitTax("FROZEN")));
        if (ConfigLoader.isReactionEnable("REVERSE_FROZEN")) getReactionManager().registerElementalReaction(new Frozen("REVERSE_FROZEN", ConfigLoader.getReactionDisplay("REVERSE_FROZEN"), ConfigLoader.getAuraElement("REVERSE_FROZEN"), ConfigLoader.getTriggerElement("REVERSE_FROZEN"), ConfigLoader.getGaugeUnitTax("REVERSE_FROZEN")));

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mm reload");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mi reload all");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mmocore reload");


    }

    @Override
    public void onDisable() {
        for (TextDisplay textDisplay : AuraVisualizer.mapHologram.values()) {
            textDisplay.remove();
        }
    }

    public static MythicCore getInstance() {
        return instance;
    }
    public static Aura getAuraManager() { return aura; }
    public static Buff getBuffManager() { return buff; }
    public static InternalCooldown getCooldownManager() { return cooldown; }
    public static ReactionManager getReactionManager() { return reactionManager; }

    //What the hell is this?
    private static File loadResource(Plugin plugin, String resource) {
        File folder = plugin.getDataFolder();
        if (!folder.exists())
            folder.mkdir();
        File resourceFile = new File(folder, resource);
        try {
            //if (!resourceFile.exists()) {
            resourceFile.createNewFile();
            try (InputStream in = plugin.getResource(resource);
                 OutputStream out = new FileOutputStream(resourceFile)) {
                ByteStreams.copy(in, out);
            }
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resourceFile;
    }
}
