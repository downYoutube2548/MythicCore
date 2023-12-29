package com.dev.mythiccore.utils;

import org.bukkit.entity.Entity;

public class EntityStatManager {

    private final Entity entity;

    public EntityStatManager(Entity entity) {
        this.entity = entity;
    }
    public double getDoubleStat(String key) {
        String o = get(key, Double.class);
        return o == null ? 0 : Double.parseDouble(o);
    }

    public String getStringStat(String key) {
        String o = get(key, String.class);
        return o == null ? "" : o;
    }

    public boolean getBooleanStat(String key) {
        String o = get(key, Boolean.class);
        return Boolean.parseBoolean(o);
    }

    public int getIntegerStat(String key) {
        String o = get(key, Boolean.class);
        return o == null ? 0 : Integer.parseInt(o);
    }

    public long getLongStat(String key) {
        String o = get(key, Boolean.class);
        return o == null ? 0 : Long.parseLong(o);
    }

    public <T> void set(String key, T value) {
        // remove current tag
        for (String s : entity.getScoreboardTags()) {
            if (s.startsWith("KEYMAP:"+value.getClass().getName()+":"+key+":")) {
                entity.removeScoreboardTag(s);
            }
        }

        // add new tag
        entity.addScoreboardTag("KEYMAP:"+value.getClass().getName()+":"+key+":"+value);
    }

    private <T> String get(String key, Class<T> type) {
        for (String s : entity.getScoreboardTags()) {
            if (s.startsWith("KEYMAP:"+type.getName()+":"+key+":")) {
                return s.substring(9 + type.getName().length() + key.length());
            }
        }
        return null;
    }

    public boolean has(String key, Class<?> type) {
        //Bukkit.broadcastMessage(ChatColor.YELLOW+"Search for: "+"KEYMAP:"+ type.getName()+":"+key+":");
        for (String s : entity.getScoreboardTags()) {
            if (s.startsWith("KEYMAP:"+ type.getName()+":"+key+":")) {
                //Bukkit.broadcastMessage(ChatColor.GREEN+s);
                return true;
            } /*else {
                //Bukkit.broadcastMessage(ChatColor.RED+s);
            }
            */
        }
        return false;
    }
}
