package com.dev.mythiccore.library.attackMetadata;

import com.dev.mythiccore.enums.AttackSource;

import java.util.HashMap;
import java.util.Optional;

public interface AstAttackMeta {

    HashMap<String, Object> metadata = new HashMap<>();

    default void setMetadata(String key, Object value) {
        metadata.put(key, value);
    }

    default Optional<Object> getMetadata(String key) {
        return Optional.ofNullable(metadata.getOrDefault(key, null));
    }

    default boolean hasMetadata(String key) {
        return metadata.containsKey(key);
    }

    String getInternalCooldownSource();
    long getInternalCooldown();
    String getDamageCalculation();
    double getTalentPercent();
    void setInternalCooldown(long duration);
    double getGaugeUnit();
    String getDecayRate();
    void setGaugeUnit(double gauge_unit);
    void setDecayRate(String decay_rate);
    AttackSource getAttackSource();
    void setAttackSource(AttackSource attack_source);
    void setInternalCooldownSource(String attack_source);
    boolean calculate();
    boolean isTriggerReaction();
    String getWeaponType();
}
