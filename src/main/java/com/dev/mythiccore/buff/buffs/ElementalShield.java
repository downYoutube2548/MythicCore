package com.dev.mythiccore.buff.buffs;

import java.util.*;

public class ElementalShield extends BuffStatus {

    private double amount;
    private final String element;

    public ElementalShield(double amount, String element, long duration) {
        super(duration);
        this.amount = amount;
        this.element = element;
    }

    public String getElement() {
        return element;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public List<BuffStatus> getCurrentBuff(List<BuffStatus> allDebuff) {
        List<BuffStatus> output = new ArrayList<>();

        // cast List<DebuffStatus> to List<ElementalShield>
        List<ElementalShield> allElementalRes = new ArrayList<>();
        for (BuffStatus debuff : allDebuff) {
            if (debuff instanceof ElementalShield) {
                allElementalRes.add((ElementalShield) debuff);
            }
        }

        // separate element
        HashMap<String, List<ElementalShield>> separatedElement = new HashMap<>();
        for (ElementalShield elementalRes : allElementalRes) {
            if (!separatedElement.containsKey(elementalRes.getElement())) {
                List<ElementalShield> l = List.of(elementalRes);
                separatedElement.put(elementalRes.getElement(), l);
            } else {
                List<ElementalShield> updatedL = new ArrayList<>(separatedElement.get(elementalRes.getElement()));
                updatedL.add(elementalRes);
                separatedElement.put(elementalRes.getElement(), updatedL);
            }
        }

        // store activate shield of each element to output arrays
        for (String element : separatedElement.keySet()) {
            List<ElementalShield> values = new ArrayList<>(separatedElement.get(element));
            values.sort(Comparator.comparingDouble(ElementalShield::getAmount).thenComparingDouble(ElementalShield::getDuration));
            Collections.reverse(values);
            output.add(values.get(0));
        }

        return output;
    }
}
