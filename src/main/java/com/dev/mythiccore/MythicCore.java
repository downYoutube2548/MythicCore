package com.dev.mythiccore;

import com.dev.mythiccore.api.PlaceholderHook;
import com.dev.mythiccore.aura.Aura;
import com.dev.mythiccore.buff.Buff;
import com.dev.mythiccore.combat.Combat;
import com.dev.mythiccore.commands.CoreCommand;
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
import com.dev.mythiccore.reaction.reactions.bloom.DendroCore;
import com.dev.mythiccore.reaction.reactions.bloom.DendroCoreManager;
import com.dev.mythiccore.reaction.reactions.bloom.DendroCoreTrigger;
import com.dev.mythiccore.reaction.reactions.frozen.FreezeEffect;
import com.dev.mythiccore.stats.DamageFormulaStat;
import com.dev.mythiccore.stats.GaugeUnitStat;
import com.dev.mythiccore.stats.InternalCooldownStat;
import com.dev.mythiccore.stats.elemental_stat.ASTElements;
import com.dev.mythiccore.utils.ConfigLoader;
import com.dev.mythiccore.visuals.AuraVisualizer;
import com.dev.mythiccore.visuals.DamageIndicatorEvent;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
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


    public static ASTElements elementStat;
    private static MythicCore instance;
    private static Aura aura;
    private static Buff buff;
    private static InternalCooldown cooldown;
    private static ReactionManager reactionManager;
    private PlaceholderExpansion placeholderExpansion;

    @Override
    public void onEnable() {

        instance = this;
        aura = new Aura();
        buff = new Buff();
        cooldown = new InternalCooldown();
        reactionManager = new ReactionManager();
        elementStat = new ASTElements();

//        loadResource(this, "config.yml");
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        ConfigLoader.loadConfig(true);
        aura.startTick();
        buff.startTick();
        cooldown.startTick();
        FreezeEffect.effectApplier();
        AuraVisualizer.start();
        DendroCoreManager.dendroCoreTick();

        Objects.requireNonNull(Bukkit.getPluginCommand("mythiccore")).setExecutor(new CoreCommand());

        //Register EventListener
        Bukkit.getPluginManager().registerEvents(new MythicMechanicLoad(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerAttack(), this);
        Bukkit.getPluginManager().registerEvents(new MobAttack(), this);
        Bukkit.getPluginManager().registerEvents(new MiscAttack(), this);
        Bukkit.getPluginManager().registerEvents(new AttackModifier(), this);
        Bukkit.getPluginManager().registerEvents(new AttackEventListener(), this);
        Bukkit.getPluginManager().registerEvents(new CancelFireTick(), this);
        Bukkit.getPluginManager().registerEvents(new DamageIndicatorEvent(), this);
        Bukkit.getPluginManager().registerEvents(new RemoveVanillaDamage(), this);
        Bukkit.getPluginManager().registerEvents(new Combat(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeath(), this);
        Bukkit.getPluginManager().registerEvents(new ProjectileLaunch(), this);
        Bukkit.getPluginManager().registerEvents(new TriggerReaction(), this);
        Bukkit.getPluginManager().registerEvents(new ShieldRefutation(), this);
        Bukkit.getPluginManager().registerEvents(new DendroCoreTrigger(), this);
        Bukkit.getPluginManager().registerEvents(new ChunkUnload(), this);

        MMOItems.plugin.getStats().register(elementStat);
        MMOItems.plugin.getStats().register(new GaugeUnitStat());
        MMOItems.plugin.getStats().register(new InternalCooldownStat());
        MMOItems.plugin.getStats().register(new DamageFormulaStat());

        ReactionManager.registerDefaultReactions();

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mm reload");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mi reload all");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mmocore reload");

        placeholderExpansion = new PlaceholderHook();
        placeholderExpansion.register();

    }

    @Override
    public void onDisable() {

        placeholderExpansion.unregister();

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

}
