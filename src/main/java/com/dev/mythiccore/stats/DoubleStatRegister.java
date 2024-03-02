package com.dev.mythiccore.stats;

import net.Indyuce.mmoitems.stat.type.DoubleStat;
import org.bukkit.Material;

public class DoubleStatRegister extends DoubleStat {

    public String id;
    public DoubleStatRegister(String id, Material mat, String name, String[] lore) {
        super(id, mat, name, lore);
        this.id = id;
    }
}
