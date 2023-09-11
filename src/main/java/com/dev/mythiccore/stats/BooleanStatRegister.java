package com.dev.mythiccore.stats;

import net.Indyuce.mmoitems.stat.type.BooleanStat;
import org.bukkit.Material;

public class BooleanStatRegister extends BooleanStat {
    public BooleanStatRegister(String id, Material mat, String name, String[] lore) {
        super(id, mat, name, lore, new String[]{"all"});
    }
}
