package com.dev.mythiccore.stats;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.utils.ConfigLoader;
import net.Indyuce.mmoitems.stat.data.StringData;
import net.Indyuce.mmoitems.stat.type.ChooseStat;
import net.Indyuce.mmoitems.util.StatChoice;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DamageFormulaStat extends ChooseStat {
    public DamageFormulaStat() {
        super("AST_DAMAGE_FORMULA", Material.ARROW, "Damage Calculation Formula", new String[]{"&7Specify the damage formula that is in", "&7MythicCore config.yml"}, new String[]{"all"});

        this.addChoices(Objects.requireNonNull(MythicCore.getInstance().getConfig().getConfigurationSection("Damage-Calculation.damage-calculation-formula")).getKeys(false).stream()
                .map(StatChoice::new)
                .toArray(StatChoice[]::new));
    }

    @NotNull
    public StringData getClearStatData() {
        return new StringData(ConfigLoader.getDefaultDamageCalculation());
    }
}
