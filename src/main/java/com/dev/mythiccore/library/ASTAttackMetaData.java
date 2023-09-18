package com.dev.mythiccore.library;

import io.lumine.mythic.lib.api.stat.provider.StatProvider;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.DamageMetadata;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ASTAttackMetaData extends AttackMetadata {
    private String cooldown_source;
    private AttackSource attack_source;

    public ASTAttackMetaData(@NotNull DamageMetadata damage, @NotNull LivingEntity target, @Nullable StatProvider attacker, String cooldown_source, AttackSource attack_source) {
        super(damage, target, attacker);
        this.cooldown_source = cooldown_source;
        this.attack_source = attack_source;
    }

    public String getInternalCooldownSource() {
        return cooldown_source;
    }
    public AttackSource getAttackSource() { return attack_source; }
    public void setAttackSource(AttackSource attack_source) { this.attack_source = attack_source; }
    public void setInternalCooldownSource(String attack_source) {
        this.cooldown_source = attack_source;
    }
}
