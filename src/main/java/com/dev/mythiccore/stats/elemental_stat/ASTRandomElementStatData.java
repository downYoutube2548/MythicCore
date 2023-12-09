package com.dev.mythiccore.stats.elemental_stat;

import io.lumine.mythic.lib.element.Element;
import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import net.Indyuce.mmoitems.stat.data.random.UpdatableRandomStatData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.util.Pair;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ASTRandomElementStatData implements RandomStatData<ASTElementStatData>, UpdatableRandomStatData<ASTElementStatData> {
    private final Map<Pair<Element, ASTElementStatType>, NumericStatFormula> stats = new LinkedHashMap<>();

    public ASTRandomElementStatData(ConfigurationSection config) {
        Validate.notNull(config, "Config cannot be null");

        for (Element element : Element.values()) {
            ASTElementStatType[] var4 = ASTElementStatType.values();

            for (ASTElementStatType statType : var4) {
                String path = statType.getConcatenatedConfigPath(element);
                if (config.contains(path)) {
                    this.stats.put(Pair.of(element, statType), new NumericStatFormula(Objects.requireNonNull(config.get(path))));
                }
            }
        }
    }

    public boolean hasStat(Element element, ASTElementStatType statType) {
        return this.stats.containsKey(Pair.of(element, statType));
    }

    @NotNull
    public NumericStatFormula getStat(Element element, ASTElementStatType statType) {
        return this.stats.getOrDefault(Pair.of(element, statType), NumericStatFormula.ZERO);
    }

    public Set<Pair<Element, ASTElementStatType>> getKeys() {
        return this.stats.keySet();
    }

    public void setStat(Element element, ASTElementStatType statType, NumericStatFormula formula) {
        this.stats.put(Pair.of(element, statType), formula);
    }

    public ASTElementStatData randomize(MMOItemBuilder builder) {
        ASTElementStatData elements = new ASTElementStatData();
        this.stats.forEach((key, value) -> {
            elements.setStat(key.getKey(), key.getValue(), value.calculate(builder.getLevel()));
        });
        return elements;
    }

    @NotNull
    @Override
    public ASTElementStatData reroll(@NotNull ItemStat stat, @NotNull ASTElementStatData original, int determinedItemLevel) {
        ASTElementStatData elements = new ASTElementStatData();

        for (Element element : Element.values()) {
            ASTElementStatType[] var8 = ASTElementStatType.values();

            for (ASTElementStatType statType : var8) {
                NumericStatFormula currentTemplateData = this.getStat(element, statType);
                DoubleData itemData = new DoubleData(original.getStat(element, statType));
                DoubleData result = currentTemplateData.reroll(stat, itemData, determinedItemLevel);
                elements.setStat(element, statType, result.getValue());
            }
        }

        return elements;
    }
}
