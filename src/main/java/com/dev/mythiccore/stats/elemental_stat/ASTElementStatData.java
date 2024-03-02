package com.dev.mythiccore.stats.elemental_stat;

import io.lumine.mythic.lib.element.Element;
import net.Indyuce.mmoitems.stat.data.type.Mergeable;
import net.Indyuce.mmoitems.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ASTElementStatData implements Mergeable<ASTElementStatData> {

    private final Map<Pair<Element, ASTElementStatType>, Double> stats = new LinkedHashMap<>();

    public double getStat(Element element, ASTElementStatType statType) {
        Double found = this.stats.get(Pair.of(element, statType));
        return found == null ? 0.0 : found;
    }

    public Set<Pair<Element, ASTElementStatType>> getKeys() {
        return this.stats.keySet();
    }

    public void setStat(Element element, ASTElementStatType statType, double value) {
        this.stats.put(Pair.of(element, statType), value);
    }

    public boolean isEmpty() {
        return this.stats.isEmpty();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            ASTElementStatData that = (ASTElementStatData) o;
            return this.stats.equals(that.stats);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(this.stats);
    }

    @Override
    public void merge(ASTElementStatData data) {
        data.stats.forEach((key, value) -> this.stats.put(key, value + this.stats.getOrDefault(key, 0.0)));
    }

    @NotNull
    public ASTElementStatData cloneData() {
        ASTElementStatData ret = new ASTElementStatData();

        for (Map.Entry<Pair<Element, ASTElementStatType>, Double> pairDoubleEntry : this.stats.entrySet()) {
            Pair<Element, ASTElementStatType> key = pairDoubleEntry.getKey();
            Double value = pairDoubleEntry.getValue();
            if (value != 0.0) {
                ret.stats.put(key, value);
            }
        }

        return ret;
    }
}
