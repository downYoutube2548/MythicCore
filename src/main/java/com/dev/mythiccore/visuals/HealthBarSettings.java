package com.dev.mythiccore.visuals;

public record HealthBarSettings (
        double heightOffset,
        int hologramDuration,
        String[] lines,
        String prefix,
        String suffix,
        String filler,
        String healthFiller,
        String damagedFiller,
        String separator,
        int width,
        String fillerColor,
        String healthColor,
        String damagedColor,
        boolean enabled,
        boolean display
) {}
