package com.dev.mythiccore.buff.buffs;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

/**
 * The class used to specify the details of Buff.
 */
public abstract class BuffStatus {
    public long duration;
    private final UUID uuid;

    public BuffStatus(long duration) {
        this.duration = duration;
        this.uuid = UUID.randomUUID();
    }

    public abstract List<BuffStatus> getCurrentBuff(List<BuffStatus> allBuff);
    public abstract String getBuffIcon();

    public long getDuration() {
        return duration;
    }
    public UUID getUniqueId() {
        return uuid;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        Class<?> clazz = this.getClass();
        Field[] fields = clazz.getDeclaredFields();
        StringBuilder sb = new StringBuilder();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object fieldValue = field.get(this);
                sb.append(fieldName).append("=").append(fieldValue).append(",");
            }
            sb.append("duration=").append(duration);
        } catch (IllegalAccessException ignored) {}
        return this.getClass().getSimpleName()+"{"+sb+"}";
    }
}
