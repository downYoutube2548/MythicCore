package com.dev.mythiccore.reaction;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.reaction.reactions.*;
import com.dev.mythiccore.reaction.reactions.bloom.Bloom;
import com.dev.mythiccore.reaction.reactions.bloom.DendroCoreReaction;
import com.dev.mythiccore.reaction.reactions.bloom.sub_reaction.Burgeon;
import com.dev.mythiccore.reaction.reactions.bloom.sub_reaction.HyperBloom;
import com.dev.mythiccore.reaction.reactions.frozen.Frozen;
import com.dev.mythiccore.reaction.reactions.quicken.Aggravate;
import com.dev.mythiccore.reaction.reactions.quicken.Quicken;
import com.dev.mythiccore.reaction.reactions.quicken.Spread;
import com.dev.mythiccore.utils.ConfigLoader;

import java.util.HashMap;

import static com.dev.mythiccore.MythicCore.getReactionManager;

public class ReactionManager {
    private final HashMap<String, ElementalReaction> reactionMap = new HashMap<>();
    private final HashMap<String, DendroCoreReaction> dendroCoreReactionMap = new HashMap<>();

    public void registerElementalReaction(ElementalReaction reaction) {
        reactionMap.put(reaction.getId(), reaction);
    }
    public void unregisterElementalReaction(String reaction_id) {
        reactionMap.remove(reaction_id);
    }
    public HashMap<String, ElementalReaction> getElementalReactions() {
        return reactionMap;
    }

    public void registerDendroCoreReaction(DendroCoreReaction reaction) {
        dendroCoreReactionMap.put(reaction.getId(), reaction);
    }
    public void unregisterDendroCoreReaction(String reaction_id) {
        dendroCoreReactionMap.remove(reaction_id);
    }
    public HashMap<String, DendroCoreReaction> getDendroCoreReactions() {
        return dendroCoreReactionMap;
    }

    public void clearReactionMap() {
        reactionMap.clear();
    }
    public void clearDendroCoreReactionMap() {
        dendroCoreReactionMap.clear();
    }

    public static void registerDefaultReactions() {
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
        if (ConfigLoader.isReactionEnable("AGGRAVATE")) getReactionManager().registerElementalReaction(new Aggravate("AGGRAVATE", ConfigLoader.getReactionConfig().getConfigurationSection("AGGRAVATE"), ConfigLoader.getReactionDisplay("AGGRAVATE"), ConfigLoader.getAuraElement("AGGRAVATE"), ConfigLoader.getTriggerElement("AGGRAVATE"), ConfigLoader.getGaugeUnitTax("AGGRAVATE")));
        if (ConfigLoader.isReactionEnable("RESONANCE")) getReactionManager().registerElementalReaction(new Resonance("RESONANCE", ConfigLoader.getReactionConfig().getConfigurationSection("RESONANCE"), ConfigLoader.getReactionDisplay("RESONANCE"), ConfigLoader.getTriggerElement("RESONANCE"), ConfigLoader.getGaugeUnitTax("RESONANCE")));
    }
}
