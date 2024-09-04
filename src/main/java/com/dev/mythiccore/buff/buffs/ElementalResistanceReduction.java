package com.dev.mythiccore.buff.buffs;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.utils.Utils;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.element.Element;

import java.util.*;

public class ElementalResistanceReduction extends BuffStatus {
    private final double amount;
    private final String element;
    private final String symbol = MythicCore.getInstance().getConfig().getString("Buff-Status.buff.ElementalResistanceReduction.symbol");

    public ElementalResistanceReduction(double amount, long duration, String element) {
        super(duration);
        this.amount = amount;
        this.element = element;
    }

    public double getAmount() {
        return amount;
    }

    public String getElement() {
        return element;
    }

    @Override
    public List<BuffStatus> getCurrentBuff(List<BuffStatus> allDebuff) {
        List<BuffStatus> output = new ArrayList<>();

        // cast List<DebuffStatus> to List<ElementalResistanceReduction>
        List<ElementalResistanceReduction> allElementalRes = allDebuff.stream().map(o -> (ElementalResistanceReduction) o).toList();

        // separate element
        HashMap<String, List<ElementalResistanceReduction>> separatedElement = new HashMap<>();
        for (ElementalResistanceReduction elementalRes : allElementalRes) {
            if (!separatedElement.containsKey(elementalRes.getElement())) {
                List<ElementalResistanceReduction> l = List.of(elementalRes);
                separatedElement.put(elementalRes.getElement(), l);
            } else {
                List<ElementalResistanceReduction> updatedL = new ArrayList<>(separatedElement.get(elementalRes.getElement()));
                updatedL.add(elementalRes);
                separatedElement.put(elementalRes.getElement(), updatedL);
            }
        }

        // store activate debuff of each element to output arrays
        for (String element : separatedElement.keySet()) {
            List<ElementalResistanceReduction> values = new ArrayList<>(separatedElement.get(element));
            values.sort(Comparator.comparingDouble(ElementalResistanceReduction::getAmount).thenComparingDouble(ElementalResistanceReduction::getDuration));
            Collections.reverse(values);
            output.add(values.get(0));
        }

        return output;
    }

    @Override
    public String getBuffIcon() {
        String value = symbol.replace("<amount>", Utils.Format(amount, "#,###.#")).replace("<duration>", String.valueOf(duration/20));
        Element element = MythicLib.plugin.getElements().get(this.element);
        if (element != null) {
            value = value.replace("<element>", element.getLoreIcon()).replace("<color>", element.getColor());
        } else {
            value = value.replace("<color>", "").replace("<element>", this.element);
        }
        return Utils.colorize(value);
    }
}
