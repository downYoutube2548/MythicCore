package com.dev.mythiccore.reaction.reactions;

import com.dev.mythiccore.combat.Combat;
import com.dev.mythiccore.reaction.reaction_type.DoubleAuraReaction;
import com.dev.mythiccore.utils.StatCalculation;
import com.dev.mythiccore.utils.Utils;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.lib.api.stat.provider.StatProvider;
import io.lumine.mythic.lib.damage.DamagePacket;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;

public class Burning extends DoubleAuraReaction {
    public Burning(String id, ConfigurationSection config, String display, String aura1, String aura2, long reaction_frequency, double gauge_unit_tax) {
        super(id, config, display, aura1, aura2, reaction_frequency, gauge_unit_tax);
    }

    @Override
    public void trigger(DamagePacket damage, double gauge_unit, String decay_rate, LivingEntity entity, @Nullable Entity damager, StatProvider stats, EntityDamageEvent.DamageCause damage_cause, Combat.MobType last_mob_type) {

        int level = 1;
        if (damager != null) {
            if (damager instanceof Player player) {
                level = PlayerData.get(player).getLevel();
            } else {
                ActiveMob mythicMob = MythicBukkit.inst().getMobManager().getActiveMob(damager.getUniqueId()).orElse(null);
                if (mythicMob != null) {
                    level = (int) mythicMob.getLevel();
                }
            }
        }

        double resistance_multiplier = StatCalculation.getResistanceMultiplier(entity.getUniqueId(), getConfig().getString("damage-element"));

        String formula = getConfig().getString("damage-formula");
        assert formula != null;
        Expression expression = new ExpressionBuilder(formula)
                .variables("attacker_level", "elemental_mastery", "resistance_multiplier")
                .build()
                .setVariable("attacker_level", level)
                .setVariable("elemental_mastery", stats.getStat("AST_ELEMENTAL_MASTERY"))
                .setVariable("resistance_multiplier", resistance_multiplier);

        double final_damage = expression.evaluate();
        double new_gauge = Double.parseDouble(Utils.splitTextAndNumber(getConfig().getString("damage-gauge-unit", "1A"))[0]);
        String new_decay_rate = Utils.splitTextAndNumber(getConfig().getString("damage-gauge-unit", "1A"))[1];
        damage(final_damage, damager, entity, getConfig().getString("damage-element"), false, new_gauge, new_decay_rate, "BURNING_REACTION", 20, false, damage_cause);

    }
}
