package com.dev.mythiccore.api;

import com.dev.mythiccore.MythicCore;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderHook extends PlaceholderExpansion {
    @NotNull
    @Override
    public String getAuthor() {
        return "downYoutube2548";
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return "mythiccore";
    }

    @NotNull
    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.equalsIgnoreCase("auras")) {
            if (player.isOnline()) {
                return MythicCore.getAuraManager().getAura(player.getUniqueId()).getAuraIcon();
            }
        }

        return "";
    }
}
