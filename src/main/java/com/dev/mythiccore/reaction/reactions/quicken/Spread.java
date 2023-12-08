package com.dev.mythiccore.reaction.reactions.quicken;

import com.dev.mythiccore.reaction.reaction_type.TriggerAuraReaction;
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

public class Spread extends TriggerAuraReaction {
    public Spread(String id, ConfigurationSection config, String display, String aura, String trigger, double gauge_unit_tax) {
        super(id, config, display, aura, trigger, gauge_unit_tax);
    }

    @Override
    public boolean trigger(DamagePacket damage, double gauge_unit, String decay_rate, LivingEntity entity, @Nullable Entity damager, StatProvider stats, EntityDamageEvent.DamageCause damage_cause) {
        int attacker_level = 1;
        double elemental_mastery = 0;
        double spread_bonus = 0;

        if (damager != null) {
            if (damager instanceof Player player) {
                PlayerData playerData = PlayerData.get(player);

                elemental_mastery = stats.getStat("AST_ELEMENTAL_MASTERY");
                spread_bonus = stats.getStat("AST_SPREAD_BONUS");
                attacker_level = playerData.getLevel();
            } else {
                ActiveMob mythicMob = MythicBukkit.inst().getMobManager().getActiveMob(damager.getUniqueId()).orElse(null);
                attacker_level = (mythicMob != null) ? (int) mythicMob.getLevel() : 1;
            }
        }

        String formula = getConfig().getString("damage-formula");
        assert formula != null;
        Expression expression = new ExpressionBuilder(formula)
                .variables("raw_damage", "attacker_level", "elemental_mastery", "spread_bonus")
                .build()
                .setVariable("raw_damage", damage.getValue())
                .setVariable("attacker_level", attacker_level)
                .setVariable("elemental_mastery", elemental_mastery)
                .setVariable("spread_bonus", spread_bonus);

        damage.setValue(expression.evaluate());

        spawnParticle(entity, getConfig().getStringList("particle"));
        playSound(entity, getConfig().getStringList("sound"));

        return true;
    }
}
