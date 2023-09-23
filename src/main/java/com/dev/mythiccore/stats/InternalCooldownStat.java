package com.dev.mythiccore.stats;

import com.dev.mythiccore.MythicCore;
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

public class InternalCooldownStat extends StringStat {
    public InternalCooldownStat() {
        super("AST_INTERNAL_COOLDOWN", Material.CLOCK, "Internal Cooldown", new String[]{"&7Aura Generation Cooldown and", "&7Elemental Reaction."}, new String[]{"all"});
    }


    @Override
    public void whenApplied(@NotNull ItemStackBuilder item, @NotNull StringData data) {

        String cooldown_source;
        long internal_cooldown;

        if (data.toString().matches("^\\d+$")) {
            cooldown_source = item.getMMOItem().getId();
            internal_cooldown = Long.parseLong(data.toString());
        } else {
            Validate.isTrue(Objects.requireNonNull(MythicCore.getInstance().getConfig().getConfigurationSection("General.internal-cooldown")).getKeys(false).contains(data.toString()), "Could not find internal cooldown '" + data + "'");
            cooldown_source = data.toString();
            internal_cooldown = -1;
        }

        item.addItemTag(new ItemTag("MMOITEMS_AST_INTERNAL_COOLDOWN", cooldown_source+" "+internal_cooldown));
        item.getLore().insert("ast-internal-cooldown", MMOItems.plugin.getLanguage().getStatFormat(this.getPath()).replace("{value}", data.toString()));
    }

    @Override
    public void whenInput(@NotNull EditionInventory inv, @NotNull String message, Object... info) {

        Validate.isTrue(message.matches("^\\d+$") || Objects.requireNonNull(MythicCore.getInstance().getConfig().getConfigurationSection("General.internal-cooldown")).getKeys(false).contains(message), "Could not find internal cooldown '" + message + "'");

        inv.getEditedSection().set("ast-internal-cooldown", message);
        inv.registerTemplateEdition();
        inv.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Internal Cooldown successfully changed to " + message + ".");
    }
}
