package com.dev.mythiccore.stats.elemental_stat;

import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import io.lumine.mythic.lib.element.Element;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.stat.type.Previewable;
import net.Indyuce.mmoitems.util.MMOUtils;
import net.Indyuce.mmoitems.util.Pair;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ASTElements extends ItemStat<ASTRandomElementStatData, ASTElementStatData> implements Previewable<ASTRandomElementStatData, ASTElementStatData> {
    public ASTElements() {
        super("AST_ELEMENT", Material.ENDER_EYE, "Elements (MythicCore)", new String[]{"The elements of your item."}, new String[]{"slashing", "piercing", "blunt", "catalyst", "range", "tool", "armor", "gem_stone"});
    }

    public ASTRandomElementStatData whenInitialized(Object object) {
        Validate.isTrue(object instanceof ConfigurationSection, "Must specify a config section");
        return new ASTRandomElementStatData((ConfigurationSection)object);
    }

    @Override
    public void whenClicked(@NotNull EditionInventory inv, @NotNull InventoryClickEvent event) {
        if (event.getAction() == InventoryAction.PICKUP_ALL) {
            (new ASTElementsEdition(inv.getPlayer(), inv.getEdited())).open(inv.getPage());
        }

        if (event.getAction() == InventoryAction.PICKUP_HALF && inv.getEditedSection().contains("ast-element")) {
            inv.getEditedSection().set("ast-element", null);
            inv.registerTemplateEdition();
            inv.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Elements successfully removed.");
        }

    }

    @Override
    public void whenInput(@NotNull EditionInventory inv, @NotNull String message, Object... info) {
        String elementPath = info[0].toString();
        NumericStatFormula formula = new NumericStatFormula(message);
        formula.fillConfigurationSection(inv.getEditedSection(), "ast-element." + elementPath);
        String elementName = elementPath.split("\\.")[0];
        if (inv.getEditedSection().contains("ast-element")) {
            if (inv.getEditedSection().getConfigurationSection("ast-element").contains(elementName) && inv.getEditedSection().getConfigurationSection("ast-element." + elementName).getKeys(false).isEmpty()) {
                inv.getEditedSection().set("ast-element." + elementName, null);
            }

            if (inv.getEditedSection().getConfigurationSection("ast-element").getKeys(false).isEmpty()) {
                inv.getEditedSection().set("ast-element", null);
            }
        }

        inv.registerTemplateEdition();
        inv.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + MMOUtils.caseOnWords(elementPath.replace(".", " ")) + ChatColor.GRAY + " successfully changed to " + ChatColor.GOLD + formula.toString() + ChatColor.GRAY + ".");
    }

    @Override
    public void whenDisplayed(List<String> lore, Optional<ASTRandomElementStatData> statData) {
        if (statData.isPresent()) {
            lore.add(ChatColor.GRAY + "Current Value:");
            ASTRandomElementStatData data = statData.get();
            data.getKeys().forEach((key) -> {
                lore.add(ChatColor.GRAY + "* " + key.getKey().getName() + " " + (key.getValue()).getName() + ": " + ChatColor.RED + data.getStat(key.getKey(), key.getValue()));
            });
        } else {
            lore.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "None");
        }

        lore.add("");
        lore.add(ChatColor.YELLOW + "►" + " Click to access the elements edition menu.");
        lore.add(ChatColor.YELLOW + "►" + " Right click to remove all the elements.");
    }

    @NotNull
    @Override
    public ASTElementStatData getClearStatData() {
        return new ASTElementStatData();
    }

    public void whenApplied(@NotNull ItemStackBuilder item, @NotNull ASTElementStatData data) {
        List<String> lore = new ArrayList<>();

        for (Pair<Element, ASTElementStatType> elementASTElementStatTypePair : data.getKeys()) {
            String format = ItemStat.translate("ast-elemental-" + (elementASTElementStatTypePair.getValue()).lowerCaseName()).replace("{color}", (elementASTElementStatTypePair.getKey()).getColor()).replace("{icon}", (elementASTElementStatTypePair.getKey()).getLoreIcon()).replace("{element}", (elementASTElementStatTypePair.getKey()).getName());
            double value = data.getStat(elementASTElementStatTypePair.getKey(), elementASTElementStatTypePair.getValue());
            lore.add(DoubleStat.formatPath("AST_ELEMENTAL_STAT", format, true, value));
        }

        if (!lore.isEmpty()) {
            item.getLore().insert("ast-elements", lore);
        }

        item.addItemTag(this.getAppliedNBT(data));
    }

    @NotNull
    @Override
    public ArrayList<ItemTag> getAppliedNBT(@NotNull ASTElementStatData data) {
        ArrayList<ItemTag> ret = new ArrayList<>();

        for (Pair<Element, ASTElementStatType> elementASTElementStatTypePair : data.getKeys()) {
            ret.add(new ItemTag("MMOITEMS_AST_" + elementASTElementStatTypePair.getValue().getConcatenatedTagPath(elementASTElementStatTypePair.getKey()), data.getStat(elementASTElementStatTypePair.getKey(), elementASTElementStatTypePair.getValue())));
        }

        return ret;
    }

    @Override
    public void whenLoaded(@NotNull ReadMMOItem mmoitem) {
        ArrayList<ItemTag> relevantTags = new ArrayList<>();

        for (Element element : Element.values()) {
            ASTElementStatType[] var5 = ASTElementStatType.values();

            for (ASTElementStatType statType : var5) {
                String path = "MMOITEMS_AST_" + statType.getConcatenatedTagPath(element);
                if (mmoitem.getNBT().hasTag(path)) {
                    relevantTags.add(ItemTag.getTagAtPath(path, mmoitem.getNBT(), SupportedNBTTagValues.DOUBLE));
                }
            }
        }

        StatData data = this.getLoadedNBT(relevantTags);
        if (data != null) {
            mmoitem.setData(this, data);
        }

    }

    @Nullable
    @Override
    public ASTElementStatData getLoadedNBT(@NotNull ArrayList<ItemTag> storedTags) {
        ASTElementStatData elements = new ASTElementStatData();

        for (Element element : Element.values()) {
            ASTElementStatType[] var5 = ASTElementStatType.values();

            for (ASTElementStatType statType : var5) {
                String path = "MMOITEMS_AST_" + statType.getConcatenatedTagPath(element);
                ItemTag tag = ItemTag.getTagAtPath(path, storedTags);
                if (tag != null) {
                    elements.setStat(element, statType, (Double) tag.getValue());
                }
            }
        }

        return elements.isEmpty() ? null : elements;
    }

    @Override
    public void whenPreviewed(@NotNull ItemStackBuilder item, @NotNull ASTElementStatData currentData, @NotNull ASTRandomElementStatData templateData) throws IllegalArgumentException {

        for (Element element : Element.values()) {
            ASTElementStatType[] var6 = ASTElementStatType.values();

            for (ASTElementStatType statType : var6) {
                NumericStatFormula nsf = templateData.getStat(element, statType);
                double techMinimum = nsf.calculate(0.0, -2.5);
                double techMaximum = nsf.calculate(0.0, 2.5);
                if (techMinimum != 0.0 || techMaximum != 0.0) {
                    String path = element.getId().toLowerCase() + "-" + statType.name().toLowerCase().replace("_", "-");
                    String builtRange;
                    if (SilentNumbers.round(techMinimum, 2) == SilentNumbers.round(techMaximum, 2)) {
                        builtRange = DoubleStat.formatPath(statType.getConcatenatedTagPath(element), ItemStat.translate(path), true, techMinimum);
                    } else {
                        builtRange = DoubleStat.formatPath(statType.getConcatenatedTagPath(element), ItemStat.translate(path), true, techMinimum, techMaximum);
                    }

                    item.getLore().insert(path, builtRange);
                }
            }
        }

        item.addItemTag(this.getAppliedNBT(currentData));
    }
}
