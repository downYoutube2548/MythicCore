package com.dev.mythiccore.stats;

import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.stat.type.InternalStat;
import org.bukkit.Material;

public class DoubleStatInternal extends DoubleStat implements InternalStat {
    public DoubleStatInternal(String id, Material mat, String name, String[] lore) {
        super(id, mat, name, lore);
    }
}
