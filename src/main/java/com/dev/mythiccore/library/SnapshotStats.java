package com.dev.mythiccore.library;

import java.util.HashMap;
import java.util.Map;

public class SnapshotStats {
    private Map<String, Double> statsMap;

    public SnapshotStats() {
        this.statsMap = new HashMap<>();
    }

    public SnapshotStats setStat(String stat, double value) {
        statsMap.put(stat, value);
        return this;
    }

    public double getStat(String stat) {
        return statsMap.getOrDefault(stat, 0D);
    }
}
