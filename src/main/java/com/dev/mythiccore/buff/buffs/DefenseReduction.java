package com.dev.mythiccore.buff.buffs;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DefenseReduction extends BuffStatus {

    private final double amount;
    private final String symbol = MythicCore.getInstance().getConfig().getString("Buff-Status.buff.DefenseReduction.symbol");

    public DefenseReduction(double amount, long duration) {
        super(duration);
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public List<BuffStatus> getCurrentBuff(List<BuffStatus> allDebuff) {
        List<BuffStatus> output = new ArrayList<>();

        List<DefenseReduction> l = new ArrayList<>();
        for (BuffStatus buffStatus : allDebuff) {
            DefenseReduction defenseReduction = (DefenseReduction) buffStatus;

            l.add(defenseReduction);
        }

        l.sort(Comparator.comparingDouble(DefenseReduction::getAmount).thenComparingDouble(DefenseReduction::getDuration));
        Collections.reverse(l);

        output.add(l.get(0));
        return output;
    }

    @Override
    public String getBuffIcon() {
        return Utils.colorize(symbol.replace("<amount>", Utils.Format(amount, "#,###.#")).replace("<duration>", String.valueOf(duration/20)));
    }
}
