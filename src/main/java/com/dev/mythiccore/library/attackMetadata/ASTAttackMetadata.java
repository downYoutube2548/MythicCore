package com.dev.mythiccore.library.attackMetadata;

import com.dev.mythiccore.enums.AttackSource;
import com.dev.mythiccore.utils.ConfigLoader;
import io.lumine.mythic.lib.api.stat.provider.StatProvider;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.DamageMetadata;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ASTAttackMetadata extends AttackMetadata implements AstAttackMeta {
    private String cooldown_source;
    private AttackSource attack_source;
    private long internal_cooldown;
    private double gauge_unit;
    private String decay_rate;
    private final String damage_calculation;
    private final double talent_percent;
    private final boolean calculate;
    private final boolean triggerReaction;
    private final String weaponType;

    public ASTAttackMetadata(AttackMetadata parent, String cooldown_source, String damage_calculation, String weaponType, double talent_percent, AttackSource attack_source, boolean calculate, boolean triggerReaction) {
        this(parent.getDamage(), parent.getTarget(), parent.getAttacker(), cooldown_source, ConfigLoader.getInternalCooldown(cooldown_source), damage_calculation, weaponType, talent_percent, attack_source, calculate, triggerReaction);
    }
    public ASTAttackMetadata(@NotNull DamageMetadata damage, @NotNull LivingEntity target, @Nullable StatProvider attacker, String cooldown_source, String damage_calculation, String weaponType, double talent_percent, AttackSource attack_source, boolean calculate, boolean triggerReaction) {
        this(damage, target, attacker, cooldown_source, ConfigLoader.getInternalCooldown(cooldown_source), damage_calculation, weaponType, talent_percent, attack_source, calculate, triggerReaction);
    }
    public ASTAttackMetadata(AttackMetadata parent, String cooldown_source, long internal_cooldown, String damage_calculation, String weaponType, double talent_percent, AttackSource attack_source, boolean calculate, boolean triggerReaction) {
        this(parent.getDamage(), parent.getTarget(), parent.getAttacker(), cooldown_source, internal_cooldown, ConfigLoader.getDefaultGaugeUnit(), ConfigLoader.getDefaultDecayRate(), damage_calculation, weaponType, talent_percent, attack_source, calculate, triggerReaction);
    }
    public ASTAttackMetadata(@NotNull DamageMetadata damage, @NotNull LivingEntity target, @Nullable StatProvider attacker, String cooldown_source, long internal_cooldown, String damage_calculation, String weaponType, double talent_percent, AttackSource attack_source, boolean calculate, boolean triggerReaction) {
        this(damage, target, attacker, cooldown_source, internal_cooldown, ConfigLoader.getDefaultGaugeUnit(), ConfigLoader.getDefaultDecayRate(), damage_calculation, weaponType, talent_percent, attack_source, calculate, triggerReaction);
    }
    public ASTAttackMetadata(AttackMetadata parent, String cooldown_source, double gauge_unit, String decay_rate, String damage_calculation, String weaponType, double talent_percent, AttackSource attack_source, boolean calculate, boolean triggerReaction) {
        this(parent.getDamage(), parent.getTarget(), parent.getAttacker(), cooldown_source, ConfigLoader.getInternalCooldown(cooldown_source), gauge_unit, decay_rate, damage_calculation, weaponType, talent_percent, attack_source, calculate, triggerReaction);
    }
    public ASTAttackMetadata(AttackMetadata parent, String cooldown_source, long internal_cooldown, double gauge_unit, String decay_rate, String damage_calculation, String weaponType, double talent_percent, AttackSource attack_source, boolean calculate, boolean triggerReaction) {
        this(parent.getDamage(), parent.getTarget(), parent.getAttacker(), cooldown_source, internal_cooldown, gauge_unit, decay_rate, damage_calculation, weaponType, talent_percent, attack_source, calculate, triggerReaction);
    }
    public ASTAttackMetadata(@NotNull DamageMetadata damage, @NotNull LivingEntity target, @Nullable StatProvider attacker, String cooldown_source, double gauge_unit, String decay_rate, String damage_calculation, String weaponType, double talent_percent, AttackSource attack_source, boolean calculate, boolean triggerReaction) {
        this(damage, target, attacker, cooldown_source, ConfigLoader.getInternalCooldown(cooldown_source), gauge_unit, decay_rate, damage_calculation, weaponType, talent_percent, attack_source, calculate, triggerReaction);
    }
    public ASTAttackMetadata(@NotNull DamageMetadata damage, @NotNull LivingEntity target, @Nullable StatProvider attacker, String cooldown_source, long internal_cooldown, double gauge_unit, String decay_rate, String damage_calculation, String weaponType, double talent_percent, AttackSource attack_source, boolean calculate, boolean triggerReaction) {
        super(damage, target, attacker);
        this.cooldown_source = cooldown_source;
        this.attack_source = attack_source;
        this.internal_cooldown = internal_cooldown;
        this.gauge_unit = gauge_unit;
        this.decay_rate = decay_rate;
        this.damage_calculation = damage_calculation;
        this.weaponType = weaponType;
        this.talent_percent = talent_percent;
        this.calculate = calculate;
        this.triggerReaction = triggerReaction;
    }

    public String getInternalCooldownSource() {
        return cooldown_source;
    }
    public long getInternalCooldown() {
        return internal_cooldown;
    }
    public String getDamageCalculation() {
        return damage_calculation;
    }
    public double getTalentPercent() {
        return talent_percent;
    }
    public void setInternalCooldown(long duration) {
        this.internal_cooldown = duration;
    }
    public double getGaugeUnit() {
        return gauge_unit;
    }
    public String getDecayRate() {
        return decay_rate;
    }
    public void setGaugeUnit(double gauge_unit) {
        this.gauge_unit = gauge_unit;
    }
    public void setDecayRate(String decay_rate) {
        this.decay_rate = decay_rate;
    }
    public AttackSource getAttackSource() { return attack_source; }
    public void setAttackSource(AttackSource attack_source) { this.attack_source = attack_source; }
    public void setInternalCooldownSource(String attack_source) {
        this.cooldown_source = attack_source;
    }
    public boolean calculate() { return calculate; }
    public boolean isTriggerReaction() {
        return triggerReaction;
    }
    public String getWeaponType() {
        return weaponType;
    }
}
