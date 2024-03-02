package com.dev.mythiccore.stats;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.stat.data.StringData;
import net.Indyuce.mmoitems.stat.type.ChooseStat;
import net.Indyuce.mmoitems.util.StatChoice;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class WeaponTypeStat extends ChooseStat {

    public WeaponTypeStat() {
        super("AST_WEAPON_TYPE", Material.TRIDENT, "Weapon Type", new String[]{"&7Choose the weapon type"}, new String[]{"all"});

        this.addChoices(new StatChoice("default"));
        this.addChoices(new StatChoice("NONE"));
        this.addChoices(MMOItems.plugin.getTypes().getAll().stream()
                .map(type -> new StatChoice(type.getId().toUpperCase()))
                .toArray(StatChoice[]::new));
    }

    @NotNull
    public StringData getClearStatData() {
        return new StringData("default");
    }
}
