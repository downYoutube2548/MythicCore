package com.dev.mythiccore.stats;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.utils.Utils;
import io.lumine.mythic.lib.api.item.ItemTag;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.StringData;
import net.Indyuce.mmoitems.stat.type.StringStat;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class GaugeUnitStat extends StringStat {
    public GaugeUnitStat() {
        super("AST_GAUGE_UNIT", Material.GLOWSTONE_DUST, "Gauge Unit", new String[]{"&7Specify Aura Gauge and", "&7Decay Rate"}, new String[]{"all"});
    }

    @Override
    public void whenApplied(@NotNull ItemStackBuilder item, @NotNull StringData data) {

        Validate.isTrue(data.toString().matches("^\\d+(\\.\\d+)?[A-Za-z_0-9]+$"), "Invalid format please use '<Gauge Unit><Decay Rate Suffix>' Example: '1.0A'.");
        String decay_rate = Utils.splitTextAndNumber(data.toString())[1];
        Validate.isTrue(Objects.requireNonNull(MythicCore.getInstance().getConfig().getConfigurationSection("General.decay-rate")).getKeys(false).contains(decay_rate), "Could not find decay rate '" + decay_rate + "'");

        item.addItemTag(new ItemTag("MMOITEMS_AST_GAUGE_UNIT", data.toString()));
        item.getLore().insert("ast-gauge-unit", MMOItems.plugin.getLanguage().getStatFormat(this.getPath()).replace("{value}", data.toString()));
    }

    @Override
    public void whenInput(@NotNull EditionInventory inv, @NotNull String message, Object... info) {

        Validate.isTrue(message.matches("^\\d+(\\.\\d+)?[A-Za-z_0-9]+$"), "Invalid format please use '<Gauge Unit><Decay Rate Suffix>' Example: '1.0A'.");
        String decay_rate = Utils.splitTextAndNumber(message)[1];
        Validate.isTrue(Objects.requireNonNull(MythicCore.getInstance().getConfig().getConfigurationSection("General.decay-rate")).getKeys(false).contains(decay_rate), "Couldn't find the decay rate '" + decay_rate + "'.");
        inv.getEditedSection().set("ast-gauge-unit", message);
        inv.registerTemplateEdition();
        inv.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Gauge Unit successfully changed to " + message + ".");
    }
}
