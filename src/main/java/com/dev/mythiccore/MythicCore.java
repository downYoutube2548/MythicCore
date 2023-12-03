package com.dev.mythiccore;

import com.dev.mythiccore.aura.Aura;
import com.dev.mythiccore.buff.Buff;
import com.dev.mythiccore.combat.Combat;
import com.dev.mythiccore.commands.core;
import com.dev.mythiccore.cooldown.InternalCooldown;
import com.dev.mythiccore.events.ChunkUnload;
import com.dev.mythiccore.events.MythicMechanicLoad;
import com.dev.mythiccore.events.PlayerDeath;
import com.dev.mythiccore.events.ProjectileLaunch;
import com.dev.mythiccore.events.attack_handle.*;
import com.dev.mythiccore.events.attack_handle.deal_damage.MiscAttack;
import com.dev.mythiccore.events.attack_handle.deal_damage.MobAttack;
import com.dev.mythiccore.events.attack_handle.deal_damage.PlayerAttack;
import com.dev.mythiccore.listener.AttackEventListener;
import com.dev.mythiccore.reaction.ReactionManager;
import com.dev.mythiccore.reaction.reactions.*;
import com.dev.mythiccore.reaction.reactions.bloom.Bloom;
import com.dev.mythiccore.reaction.reactions.bloom.DendroCore;
import com.dev.mythiccore.reaction.reactions.bloom.DendroCoreManager;
import com.dev.mythiccore.reaction.reactions.bloom.DendroCoreTrigger;
import com.dev.mythiccore.reaction.reactions.bloom.sub_reaction.Burgeon;
import com.dev.mythiccore.reaction.reactions.bloom.sub_reaction.HyperBloom;
import com.dev.mythiccore.reaction.reactions.frozen.FreezeEffect;
import com.dev.mythiccore.reaction.reactions.frozen.Frozen;
import com.dev.mythiccore.reaction.reactions.quicken.Quicken;
import com.dev.mythiccore.reaction.reactions.quicken.Spread;
import com.dev.mythiccore.stats.GaugeUnitStat;
import com.dev.mythiccore.stats.InternalCooldownStat;
import com.dev.mythiccore.utils.ConfigLoader;
import com.dev.mythiccore.visuals.ASTDamageIndicators;
import com.dev.mythiccore.visuals.AuraVisualizer;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
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
//        loadResource(this, "config.yml");
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        ConfigLoader.loadConfig();
        aura.startTick();
        buff.startTick();
        cooldown.startTick();
        FreezeEffect.effectApplier();
        AuraVisualizer.start();
        DendroCoreManager.dendroCoreTick();

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
        Bukkit.getPluginManager().registerEvents(new PlayerDeath(), this);
        Bukkit.getPluginManager().registerEvents(new ProjectileLaunch(), this);
        Bukkit.getPluginManager().registerEvents(new TriggerReaction(), this);
        Bukkit.getPluginManager().registerEvents(new ShieldRefutation(), this);
        Bukkit.getPluginManager().registerEvents(new DendroCoreTrigger(), this);
        Bukkit.getPluginManager().registerEvents(new ChunkUnload(), this);

        MMOItems.plugin.getStats().register(new GaugeUnitStat());
        MMOItems.plugin.getStats().register(new InternalCooldownStat());

        if (ConfigLoader.isReactionEnable("OVERLOADED")) {
            getReactionManager().registerElementalReaction(new Overloaded("OVERLOADED", ConfigLoader.getReactionConfig().getConfigurationSection("OVERLOADED"), ConfigLoader.getReactionDisplay("OVERLOADED"), ConfigLoader.getAuraElement("OVERLOADED"), ConfigLoader.getTriggerElement("OVERLOADED"), ConfigLoader.getGaugeUnitTax("OVERLOADED")));
            getReactionManager().registerElementalReaction(new Overloaded("REVERSE_OVERLOADED", ConfigLoader.getReactionConfig().getConfigurationSection("OVERLOADED"), ConfigLoader.getReactionDisplay("OVERLOADED"), ConfigLoader.getTriggerElement("OVERLOADED"), ConfigLoader.getAuraElement("OVERLOADED"), ConfigLoader.getGaugeUnitTax("OVERLOADED")));
        }
        if (ConfigLoader.isReactionEnable("VAPORIZE")) getReactionManager().registerElementalReaction(new Vaporize("VAPORIZE", ConfigLoader.getReactionConfig().getConfigurationSection("VAPORIZE"), ConfigLoader.getReactionDisplay("VAPORIZE"), ConfigLoader.getAuraElement("VAPORIZE"), ConfigLoader.getTriggerElement("VAPORIZE"), ConfigLoader.getGaugeUnitTax("VAPORIZE")));
        if (ConfigLoader.isReactionEnable("REVERSE_VAPORIZE")) getReactionManager().registerElementalReaction(new Vaporize("REVERSE_VAPORIZE", ConfigLoader.getReactionConfig().getConfigurationSection("REVERSE_VAPORIZE"), ConfigLoader.getReactionDisplay("REVERSE_VAPORIZE"), ConfigLoader.getAuraElement("REVERSE_VAPORIZE"), ConfigLoader.getTriggerElement("REVERSE_VAPORIZE"), ConfigLoader.getGaugeUnitTax("REVERSE_VAPORIZE")));
        if (ConfigLoader.isReactionEnable("MELT")) getReactionManager().registerElementalReaction(new Melt("MELT", ConfigLoader.getReactionConfig().getConfigurationSection("MELT"), ConfigLoader.getReactionDisplay("MELT"), ConfigLoader.getAuraElement("MELT"), ConfigLoader.getTriggerElement("MELT"), ConfigLoader.getGaugeUnitTax("MELT")));
        if (ConfigLoader.isReactionEnable("REVERSE_MELT")) {
            getReactionManager().registerElementalReaction(new Melt("REVERSE_MELT", ConfigLoader.getReactionConfig().getConfigurationSection("REVERSE_MELT"), ConfigLoader.getReactionDisplay("REVERSE_MELT"), ConfigLoader.getAuraElement("REVERSE_MELT"), ConfigLoader.getTriggerElement("REVERSE_MELT"), ConfigLoader.getGaugeUnitTax("REVERSE_MELT")));
            getReactionManager().registerElementalReaction(new Melt("FROZEN_MELT", ConfigLoader.getReactionConfig().getConfigurationSection("REVERSE_MELT"), ConfigLoader.getReactionDisplay("REVERSE_MELT"), ConfigLoader.getReactionConfig().getString("REVERSE_MELT.special-aura"), ConfigLoader.getTriggerElement("REVERSE_MELT"), ConfigLoader.getGaugeUnitTax("REVERSE_MELT")));
        }
        if (ConfigLoader.isReactionEnable("SUPER_CONDUCT")) {
            getReactionManager().registerElementalReaction(new SuperConduct("SUPER_CONDUCT", ConfigLoader.getReactionConfig().getConfigurationSection("SUPER_CONDUCT"), ConfigLoader.getReactionDisplay("SUPER_CONDUCT"), ConfigLoader.getAuraElement("SUPER_CONDUCT"), ConfigLoader.getTriggerElement("SUPER_CONDUCT"), ConfigLoader.getGaugeUnitTax("SUPER_CONDUCT")));
            getReactionManager().registerElementalReaction(new SuperConduct("REVERSE_SUPER_CONDUCT", ConfigLoader.getReactionConfig().getConfigurationSection("SUPER_CONDUCT"), ConfigLoader.getReactionDisplay("SUPER_CONDUCT"), ConfigLoader.getTriggerElement("SUPER_CONDUCT"), ConfigLoader.getAuraElement("SUPER_CONDUCT"), ConfigLoader.getGaugeUnitTax("SUPER_CONDUCT")));
            getReactionManager().registerElementalReaction(new SuperConduct("FROZEN_SUPER_CONDUCT", ConfigLoader.getReactionConfig().getConfigurationSection("SUPER_CONDUCT"), ConfigLoader.getReactionDisplay("SUPER_CONDUCT"), ConfigLoader.getReactionConfig().getString("SUPER_CONDUCT.special-aura"), ConfigLoader.getTriggerElement("SUPER_CONDUCT"), ConfigLoader.getGaugeUnitTax("SUPER_CONDUCT")));
        }
        if (ConfigLoader.isReactionEnable("ELECTRO_CHARGED")) getReactionManager().registerElementalReaction(new ElectroCharged("ELECTRO_CHARGED", ConfigLoader.getReactionConfig().getConfigurationSection("ELECTRO_CHARGED"), ConfigLoader.getReactionDisplay("ELECTRO_CHARGED"), ConfigLoader.getReactionConfig().getString("ELECTRO_CHARGED.first-aura-element"), ConfigLoader.getReactionConfig().getString("ELECTRO_CHARGED.second-aura-element"), ConfigLoader.getReactionFrequency("ELECTRO_CHARGED"), ConfigLoader.getGaugeUnitTax("ELECTRO_CHARGED")));
        if (ConfigLoader.isReactionEnable("FROZEN")) {
            getReactionManager().registerElementalReaction(new Frozen("FROZEN", ConfigLoader.getReactionConfig().getConfigurationSection("FROZEN"), ConfigLoader.getReactionDisplay("FROZEN"), ConfigLoader.getAuraElement("FROZEN"), ConfigLoader.getTriggerElement("FROZEN"), ConfigLoader.getGaugeUnitTax("FROZEN")));
            getReactionManager().registerElementalReaction(new Frozen("REVERSE_FROZEN", ConfigLoader.getReactionConfig().getConfigurationSection("FROZEN"), ConfigLoader.getReactionDisplay("FROZEN"), ConfigLoader.getTriggerElement("FROZEN"), ConfigLoader.getAuraElement("FROZEN"), ConfigLoader.getGaugeUnitTax("FROZEN")));
        }
        if (ConfigLoader.isReactionEnable("BURNING")) {
            getReactionManager().registerElementalReaction(new Burning("BURNING", ConfigLoader.getReactionConfig().getConfigurationSection("BURNING"), ConfigLoader.getReactionDisplay("BURNING"), ConfigLoader.getReactionConfig().getString("BURNING.first-aura-element"), ConfigLoader.getReactionConfig().getString("BURNING.second-aura-element"), ConfigLoader.getReactionFrequency("BURNING"), ConfigLoader.getGaugeUnitTax("BURNING")));
            getReactionManager().registerElementalReaction(new Burning("QUICKEN_BURNING", ConfigLoader.getReactionConfig().getConfigurationSection("BURNING"), ConfigLoader.getReactionDisplay("BURNING"), ConfigLoader.getReactionConfig().getString("BURNING.first-aura-element"), ConfigLoader.getReactionConfig().getString("BURNING.special-aura"), ConfigLoader.getReactionFrequency("BURNING"), ConfigLoader.getGaugeUnitTax("BURNING")));
        }
        if (ConfigLoader.isReactionEnable("BLOOM")) {
            getReactionManager().registerElementalReaction(new Bloom("BLOOM", ConfigLoader.getReactionConfig().getConfigurationSection("BLOOM"), ConfigLoader.getReactionDisplay("BLOOM"), ConfigLoader.getAuraElement("BLOOM"), ConfigLoader.getTriggerElement("BLOOM"), ConfigLoader.getGaugeUnitTax("BLOOM")));
            getReactionManager().registerElementalReaction(new Bloom("REVERSE_BLOOM", ConfigLoader.getReactionConfig().getConfigurationSection("BLOOM"), ConfigLoader.getReactionDisplay("BLOOM"), ConfigLoader.getTriggerElement("BLOOM"), ConfigLoader.getAuraElement("BLOOM"), ConfigLoader.getGaugeUnitTax("BLOOM")));
            getReactionManager().registerElementalReaction(new Bloom("QUICKEN_BLOOM", ConfigLoader.getReactionConfig().getConfigurationSection("BLOOM"), ConfigLoader.getReactionDisplay("BLOOM"), ConfigLoader.getReactionConfig().getString("BLOOM.special-aura"), ConfigLoader.getAuraElement("BLOOM"), ConfigLoader.getGaugeUnitTax("BLOOM")));
            if (ConfigLoader.isDendroCoreReactionEnable("HYPERBLOOM")) getReactionManager().registerDendroCoreReaction(new HyperBloom("HYPERBLOOM", ConfigLoader.getReactionConfig().getConfigurationSection("BLOOM.sub-reaction.HYPERBLOOM"), ConfigLoader.getReactionConfig().getString("BLOOM.sub-reaction.HYPERBLOOM.display"), ConfigLoader.getReactionConfig().getString("BLOOM.sub-reaction.HYPERBLOOM.trigger-element")));
            if (ConfigLoader.isDendroCoreReactionEnable("BURGEON")) getReactionManager().registerDendroCoreReaction(new Burgeon("BURGEON", ConfigLoader.getReactionConfig().getConfigurationSection("BLOOM.sub-reaction.BURGEON"), ConfigLoader.getReactionConfig().getString("BLOOM.sub-reaction.BURGEON.display"), ConfigLoader.getReactionConfig().getString("BLOOM.sub-reaction.BURGEON.trigger-element")));
        }
        if (ConfigLoader.isReactionEnable("SWIRL")) {
            for (String can_swirl : MythicCore.getInstance().getConfig().getStringList("Elemental-Reaction.SWIRL.can-swirl")) {
                getReactionManager().registerElementalReaction(new Swirl("SWIRL_"+can_swirl, ConfigLoader.getReactionConfig().getConfigurationSection("SWIRL"), ConfigLoader.getReactionDisplay("SWIRL"), can_swirl, ConfigLoader.getTriggerElement("SWIRL"), ConfigLoader.getGaugeUnitTax("SWIRL")));
            }
        }
        if (ConfigLoader.isReactionEnable("QUICKEN")) {
            getReactionManager().registerElementalReaction(new Quicken("QUICKEN", ConfigLoader.getReactionConfig().getConfigurationSection("QUICKEN"), ConfigLoader.getReactionDisplay("QUICKEN"), ConfigLoader.getAuraElement("QUICKEN"), ConfigLoader.getTriggerElement("QUICKEN"), ConfigLoader.getGaugeUnitTax("QUICKEN")));
            getReactionManager().registerElementalReaction(new Quicken("REVERSE_QUICKEN", ConfigLoader.getReactionConfig().getConfigurationSection("QUICKEN"), ConfigLoader.getReactionDisplay("QUICKEN"), ConfigLoader.getTriggerElement("QUICKEN"), ConfigLoader.getAuraElement("QUICKEN"), ConfigLoader.getGaugeUnitTax("QUICKEN")));
        }
        if (ConfigLoader.isReactionEnable("SPREAD")) getReactionManager().registerElementalReaction(new Spread("SPREAD", ConfigLoader.getReactionConfig().getConfigurationSection("SPREAD"), ConfigLoader.getReactionDisplay("SPREAD"), ConfigLoader.getAuraElement("SPREAD"), ConfigLoader.getTriggerElement("SPREAD"), ConfigLoader.getGaugeUnitTax("SPREAD")));
        if (ConfigLoader.isReactionEnable("AGGRAVATE")) getReactionManager().registerElementalReaction(new Spread("AGGRAVATE", ConfigLoader.getReactionConfig().getConfigurationSection("AGGRAVATE"), ConfigLoader.getReactionDisplay("AGGRAVATE"), ConfigLoader.getAuraElement("AGGRAVATE"), ConfigLoader.getTriggerElement("AGGRAVATE"), ConfigLoader.getGaugeUnitTax("AGGRAVATE")));

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mm reload");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mi reload all");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mmocore reload");

    }

    @Override
    public void onDisable() {
        for (TextDisplay textDisplay : AuraVisualizer.mapHologram.values()) {
            textDisplay.remove();
        }
        for (List<DendroCore> dendroCores : DendroCoreManager.dendroCoreInChunk.values()) {
            for (DendroCore dendroCore : dendroCores) {
                dendroCore.getDendroCore().remove();
            }
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
//    private static File loadResource(Plugin plugin, String resource) {
//        File folder = plugin.getDataFolder();
//        if (!folder.exists())
//            folder.mkdir();
//        File resourceFile = new File(folder, resource);
//        try {
//            //if (!resourceFile.exists()) {
//            resourceFile.createNewFile();
//            try (InputStream in = plugin.getResource(resource);
//                 OutputStream out = new FileOutputStream(resourceFile)) {
//                ByteStreams.copy(in, out);
//            }
//            //}
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return resourceFile;
//    }
}
