package com.dev.mythiccore.stats;

import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class DoubleStatRegister extends DoubleStat {

    public String id;
    public DoubleStatRegister(String id, Material mat, String name, String[] lore) {
        super(id, mat, name, lore);
        this.id = id;
    }


    @Override
    public void whenApplied(@NotNull ItemStackBuilder item, @NotNull DoubleData data) {
        double value = data.getValue();
        String path = id.toLowerCase().replace("_", "-");
        String format = ItemStat.translate(path);
        item.getLore().insert(path, format.replace("{value}",""+value));
        item.addItemTag(getAppliedNBT(data));
    }
}
