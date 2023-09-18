package com.dev.mythiccore.reaction.reactions;

import com.dev.mythiccore.library.SnapshotStats;
import com.dev.mythiccore.reaction.reaction_type.DoubleAuraReaction;
import com.dev.mythiccore.utils.StatCalculation;
import io.lumine.mythic.lib.damage.DamagePacket;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;

public class ElectroCharged extends DoubleAuraReaction {

    public ElectroCharged(String id, String display, String aura1, String aura2, long reaction_frequency, double gauge_unit_tax) {
        super(id, display, aura1, aura2, reaction_frequency, gauge_unit_tax);
    }

    @Override
    public void trigger(DamagePacket damage, double gauge_unit, String decay_rate, LivingEntity entity, @Nullable Entity damager, EntityDamageEvent.DamageCause damage_cause, SnapshotStats stats) {

        double resistance_multiplier = StatCalculation.getResistanceMultiplier(entity.getUniqueId(), getConfig().getString("damage-element"));

        String formula = getConfig().getString("damage-formula");
        assert formula != null;
        Expression expression = new ExpressionBuilder(formula)
                .variables("attacker_level", "elemental_mastery", "resistance_multiplier")
                .build()
                .setVariable("attacker_level", stats.getStat("LEVEL"))
                .setVariable("elemental_mastery", stats.getStat("AST_ELEMENTAL_MASTERY"))
                .setVariable("resistance_multiplier", resistance_multiplier);

        double final_damage = expression.evaluate();
        damage(final_damage, damager, entity, getConfig().getString("damage-element"), false, false, damage_cause);
    }
}
