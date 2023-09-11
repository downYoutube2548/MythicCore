package com.dev.mythiccore.aura;

public class AuraGauge {
    private final long duration;
    private final String decay_rate;

    public AuraGauge(long duration, String decay_rate) {
        this.duration = duration;
        this.decay_rate = decay_rate;
    }

    public String getDecayRate() { return this.decay_rate; }
    public long getDuration() { return this.duration; }
}
