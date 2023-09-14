package com.dev.mythiccore;

import com.dev.mythiccore.aura.Aura;
import com.dev.mythiccore.buff.Buff;
import com.dev.mythiccore.commands.core;
import com.dev.mythiccore.cooldown.InternalCooldown;
import com.dev.mythiccore.events.MythicMechanicLoad;
import com.dev.mythiccore.events.attack_handle.AttackModifier;
import com.dev.mythiccore.events.attack_handle.CancelFireTick;
import com.dev.mythiccore.events.attack_handle.RemoveVanillaDamage;
import com.dev.mythiccore.events.attack_handle.attack_priority.Attack;
import com.dev.mythiccore.events.attack_handle.attack_priority.ShieldRefutation;
import com.dev.mythiccore.events.attack_handle.attack_priority.TriggerReaction;
import com.dev.mythiccore.events.attack_handle.deal_damage.MiscAttack;
import com.dev.mythiccore.events.attack_handle.deal_damage.MobAttack;
import com.dev.mythiccore.events.attack_handle.deal_damage.PlayerAttack;
import com.dev.mythiccore.listener.AttackEventListener;
import com.dev.mythiccore.reaction.ReactionManager;
import com.dev.mythiccore.reaction.reactions.*;
import com.dev.mythiccore.utils.ConfigLoader;
import com.dev.mythiccore.visuals.ASTDamageIndicators;
import com.dev.mythiccore.visuals.AuraVisualizer;
import com.google.common.io.ByteStreams;
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
    //  4. Entity Elemental Inflection Status
    //  5. Elemental Reaction
    //  6. Configurable Damage Equation (Done)
    //  7. More...

    private static MythicCore instance;
    private static Aura aura;
    private static Buff buff;
    private static InternalCooldown cooldown;
    private static Attack attack;
    private static ReactionManager reactionManager;

    @Override
    public void onEnable() {
        instance = this;
        aura = new Aura();
        buff = new Buff();
        cooldown = new InternalCooldown();
        attack = new Attack();
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
        Bukkit.getPluginManager().registerEvents(getAttack(), this);

        getAttack().registerAttackEvent(new ShieldRefutation());
        getAttack().registerAttackEvent(new TriggerReaction());

        getReactionManager().registerElementalReaction(new Overloaded());
        getReactionManager().registerElementalReaction(new ReverseOverloaded());
        getReactionManager().registerElementalReaction(new Vaporize());
        getReactionManager().registerElementalReaction(new ReverseVaporize());
        getReactionManager().registerElementalReaction(new Melt());
        getReactionManager().registerElementalReaction(new ReverseMelt());

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
    public static Attack getAttack() { return attack; }
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
