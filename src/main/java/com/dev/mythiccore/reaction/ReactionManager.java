package com.dev.mythiccore.reaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReactionManager {
    private final HashMap<String, List<ElementalReaction>> reactionMap = new HashMap<>();

    public void registerElementalReaction(ElementalReaction reaction) {

        if (reactionMap.containsKey(reaction.getId())) {
            List<ElementalReaction> reactions = new ArrayList<>(reactionMap.get(reaction.getId()));
            reactions.add(reaction);
            reactionMap.put(reaction.getId(), reactions);
        } else {
            reactionMap.put(reaction.getId(), new ArrayList<>(List.of(reaction)));
        }

    }
    public void unregisterElementalReaction(String reaction_id) {
        reactionMap.remove(reaction_id);
    }
    public HashMap<String, List<ElementalReaction>> getElementalReactions() {
        return reactionMap;
    }
}
