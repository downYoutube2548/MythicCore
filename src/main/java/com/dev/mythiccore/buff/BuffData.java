package com.dev.mythiccore.buff;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.buff.buffs.BuffStatus;

import java.lang.reflect.Field;
import java.util.*;

/**
 * The data set of specified entity about Buff
 */
public class BuffData {

    private final UUID uuid;
    private final List<BuffStatus> totalBuff = new ArrayList<>();

    public BuffData(UUID uuid) {
        this.uuid = uuid;
    }

    public List<BuffStatus> getTotalBuffs() {
        return totalBuff;
    }

    public List<BuffStatus> getActivateBuffs() {
        List<BuffStatus> output = new ArrayList<>();
        Set<String> buffClass = new HashSet<>();
        for (BuffStatus buff : totalBuff) {
            if (buffClass.contains(buff.getClass().getName())) continue;

            List<BuffStatus> allBuff = new ArrayList<>();
            for (BuffStatus d : totalBuff) {
                if (buff.getClass().getName().equals(d.getClass().getName())) {
                    allBuff.add(d);
                }
            }

            buffClass.add(buff.getClass().getName());
            output.addAll(buff.getCurrentBuff(allBuff));
        }
        return output;
    }

    public <T> T getActivateBuff(Class<T> buff, String[] key, Object[] value) {
        ActivatedBuffLoop: for (BuffStatus buffStatus : getActivateBuffs()) {
            if (buffStatus.getClass().equals(buff)) {
                Class<?> clazz = buffStatus.getClass();
                Field[] fields = clazz.getDeclaredFields();
                HashMap<String, Object> data = new HashMap<>();
                try {
                    for (Field field : fields) {
                        field.setAccessible(true);
                        String fieldName = field.getName();
                        Object fieldValue = field.get(buffStatus);
                        data.put(fieldName, fieldValue);
                    }
                    data.put("duration", buffStatus.getDuration());
                } catch (IllegalAccessException ignored) {}

                if (key.length == 0) return buff.cast(buffStatus);
                for (int k = 0; k < key.length; k++) {
                    if (value[k] == null) continue ActivatedBuffLoop;
                    if (!data.containsKey(key[k])) continue ActivatedBuffLoop;
                    if (!data.get(key[k]).equals(value[k])) continue ActivatedBuffLoop;
                }
                return buff.cast(buffStatus);
            }
        }
        return null;
    }

    public void addBuff(BuffStatus buff) {
        totalBuff.add(buff);
        if (!MythicCore.getBuffManager().mapBuffData.containsKey(this.uuid)) MythicCore.getBuffManager().mapBuffData.put(this.uuid, this);
    }

    public void removeBuff(UUID uuid) {
        for (BuffStatus buff : totalBuff) {
            if (buff.getUniqueId() == uuid) {
                totalBuff.remove(buff);
                if (totalBuff.isEmpty()) MythicCore.getBuffManager().mapBuffData.remove(this.uuid);
                break;
            }
        }
    }

    public void reduceDuration(UUID uuid, long duration) {
        for (BuffStatus buff : totalBuff) {
            if (buff.getUniqueId() == uuid) {
                if (buff.getDuration() <= duration) {
                    removeBuff(uuid);
                } else {
                    buff.setDuration(buff.getDuration() - duration);
                }
                break;
            }
        }
    }
}
