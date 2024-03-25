package com.dev.mythiccore.api;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.events.hp_bar.HpBar;
import com.dev.mythiccore.utils.ConfigLoader;
import com.dev.mythiccore.utils.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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
        if (params.equalsIgnoreCase("health_bar")) {
            if (player.isOnline()) {
                return HpBar.getHpBar(player.getPlayer());
            }
        }
        if (params.startsWith("stat_format_")){
            if (player.isOnline()) {
                String stat = params.substring(12).toUpperCase();
                PlayerData playerData = PlayerData.get(player);
                return Utils.Format(playerData.getStats().getStat(stat));
            }
        }
        if (params.startsWith("stat_")){
            if (player.isOnline()) {
                String stat = params.substring(5).toUpperCase();
                PlayerData playerData = PlayerData.get(player);
                return String.valueOf(playerData.getStats().getStat(stat));
            }
        }
        if (params.startsWith("placeholder_")) {
            String key = params.substring(12);

            String value = MythicCore.getInstance().getConfig().getString("Placeholders." + key);

            String formula = value == null ? "0" : PlaceholderAPI.setPlaceholders(player, value);
            Expression expression = new ExpressionBuilder(formula).build();

            return Utils.Format(expression.evaluate());
        }

        return "";
    }
}
