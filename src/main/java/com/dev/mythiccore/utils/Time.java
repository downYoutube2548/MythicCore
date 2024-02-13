package com.dev.mythiccore.utils;

public class Time {
    private long unixTime;
    private Time(long unixTime) {
        this.unixTime = unixTime;
    }

    public long getUnixTime() {
        return unixTime;
    }

    public Time add(long duration) {
        this.unixTime += duration;
        return this;
    }

    public Time remove(long duration) {
        this.unixTime -= duration;
        return this;
    }

    public long subtract(Time time2) {
        return this.unixTime - time2.unixTime;
    }

    public static Time now() {
        return new Time(System.currentTimeMillis());
    }

    public static Time of(long unixTime) {
        return new Time(unixTime);
    }
}
