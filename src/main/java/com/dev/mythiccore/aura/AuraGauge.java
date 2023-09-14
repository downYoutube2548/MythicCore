package com.dev.mythiccore.aura;

public class AuraGauge {
    private final long duration;
    private final String decay_rate;
    private final double gauge_unit;

    public AuraGauge(long duration, double gauge_unit, String decay_rate) {
        this.duration = duration;
        this.decay_rate = decay_rate;
        this.gauge_unit = gauge_unit;
    }

    public String getDecayRate() { return this.decay_rate; }
    public long getDuration() { return this.duration; }
    public double getGaugeUnit() { return this.gauge_unit; }
}
