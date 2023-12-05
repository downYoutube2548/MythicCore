package com.dev.mythiccore.enums;

public enum MobType {

    NULL(-1),
    ENEMY(0),
    PLAYER(1),
    OTHER(2);

    final int id;
    MobType(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
}
