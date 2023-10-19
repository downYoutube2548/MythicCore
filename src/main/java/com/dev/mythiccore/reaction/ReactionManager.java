package com.dev.mythiccore.reaction;

import com.dev.mythiccore.reaction.reactions.bloom.DendroCoreReaction;

import java.util.HashMap;

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
}
