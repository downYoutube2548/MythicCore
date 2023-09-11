package com.dev.mythiccore.reaction;

import java.util.HashMap;

public class ReactionManager {
    private final HashMap<String, ElementalReaction> reactionMap = new HashMap<>();

    public void registerElementalReaction(ElementalReaction reaction) {
        reactionMap.put(reaction.getId(), reaction);
    }
    public void unregisterElementalReaction(String reaction_id) {
        reactionMap.remove(reaction_id);
    }
    public HashMap<String, ElementalReaction> getElementalReactions() {
        return reactionMap;
    }
}
