package com.dev.mythiccore.buff.buffs;

import java.util.*;

public class ElementalResistanceReduction extends BuffStatus {
    private final double amount;
    private final String element;

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
}
