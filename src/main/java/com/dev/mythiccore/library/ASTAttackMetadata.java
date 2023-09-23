package com.dev.mythiccore.library;

import com.dev.mythiccore.utils.ConfigLoader;
import io.lumine.mythic.lib.api.stat.provider.StatProvider;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.DamageMetadata;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ASTAttackMetadata extends AttackMetadata {
    private String cooldown_source;
    private AttackSource attack_source;
    private long internal_cooldown;
    private double gauge_unit;
    private String decay_rate;

    public ASTAttackMetadata(AttackMetadata parent, String cooldown_source, AttackSource attack_source) {
        this(parent.getDamage(), parent.getTarget(), parent.getAttacker(), cooldown_source, ConfigLoader.getInternalCooldown(cooldown_source), attack_source);
    }
    public ASTAttackMetadata(@NotNull DamageMetadata damage, @NotNull LivingEntity target, @Nullable StatProvider attacker, String cooldown_source, AttackSource attack_source) {
        this(damage, target, attacker, cooldown_source, ConfigLoader.getInternalCooldown(cooldown_source), attack_source);
    }
    public ASTAttackMetadata(AttackMetadata parent, String cooldown_source, long internal_cooldown, AttackSource attack_source) {
        this(parent.getDamage(), parent.getTarget(), parent.getAttacker(), cooldown_source, internal_cooldown, ConfigLoader.getDefaultGaugeUnit(), ConfigLoader.getDefaultDecayRate(), attack_source);
    }
    public ASTAttackMetadata(@NotNull DamageMetadata damage, @NotNull LivingEntity target, @Nullable StatProvider attacker, String cooldown_source, long internal_cooldown, AttackSource attack_source) {
        this(damage, target, attacker, cooldown_source, internal_cooldown, ConfigLoader.getDefaultGaugeUnit(), ConfigLoader.getDefaultDecayRate(), attack_source);
    }
    public ASTAttackMetadata(AttackMetadata parent, String cooldown_source, double gauge_unit, String decay_rate, AttackSource attack_source) {
        this(parent.getDamage(), parent.getTarget(), parent.getAttacker(), cooldown_source, ConfigLoader.getInternalCooldown(cooldown_source), gauge_unit, decay_rate, attack_source);
    }
    public ASTAttackMetadata(AttackMetadata parent, String cooldown_source, long internal_cooldown, double gauge_unit, String decay_rate, AttackSource attack_source) {
        this(parent.getDamage(), parent.getTarget(), parent.getAttacker(), cooldown_source, internal_cooldown, gauge_unit, decay_rate, attack_source);
    }
    public ASTAttackMetadata(@NotNull DamageMetadata damage, @NotNull LivingEntity target, @Nullable StatProvider attacker, String cooldown_source, double gauge_unit, String decay_rate, AttackSource attack_source) {
        this(damage, target, attacker, cooldown_source, ConfigLoader.getInternalCooldown(cooldown_source), gauge_unit, decay_rate, attack_source);
    }
    public ASTAttackMetadata(@NotNull DamageMetadata damage, @NotNull LivingEntity target, @Nullable StatProvider attacker, String cooldown_source, long internal_cooldown, double gauge_unit, String decay_rate, AttackSource attack_source) {
        super(damage, target, attacker);
        this.cooldown_source = cooldown_source;
        this.attack_source = attack_source;
        this.internal_cooldown = internal_cooldown;
        this.gauge_unit = gauge_unit;
        this.decay_rate = decay_rate;
    }

    public String getInternalCooldownSource() {
        return cooldown_source;
    }
    public long getInternalCooldown() {
        return internal_cooldown;
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
}
