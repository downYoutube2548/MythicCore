package com.dev.mythiccore.library;

import io.lumine.mythic.lib.api.stat.provider.StatProvider;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.DamageMetadata;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ASTAttackMetaData extends AttackMetadata {
    private String attack_source;

    public ASTAttackMetaData(@NotNull DamageMetadata damage, @NotNull LivingEntity target, @Nullable StatProvider attacker) {
        super(damage, target, attacker);
        this.attack_source = "default";
    }

    public ASTAttackMetaData(@NotNull DamageMetadata damage, @NotNull LivingEntity target, @Nullable StatProvider attacker, String attack_source) {
        super(damage, target, attacker);
        this.attack_source = attack_source;
    }

    public String getAttackSource() {
        return attack_source;
    }

    public void setAttackSource(String attack_source) {
        this.attack_source = attack_source;
    }
}
