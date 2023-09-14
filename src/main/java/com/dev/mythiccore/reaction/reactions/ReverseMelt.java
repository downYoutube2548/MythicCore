package com.dev.mythiccore.reaction.reactions;

import com.dev.mythiccore.reaction.reaction_type.TriggerAuraReaction;
import com.dev.mythiccore.utils.ConfigLoader;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.lib.damage.DamagePacket;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.stats.PlayerStats;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;

public class ReverseMelt extends TriggerAuraReaction {
    public ReverseMelt() {
        super("REVERSE_MELT", ConfigLoader.getReactionDisplay("REVERSE_MELT"), ConfigLoader.getAuraElement("REVERSE_MELT"), ConfigLoader.getTriggerElement("REVERSE_MELT"), ConfigLoader.getGaugeUnitTax("REVERSE_MELT"));
    }

    @Override
    public void trigger(DamagePacket damage, double gauge_unit, String decay_rate, @Nullable Entity damager, LivingEntity entity, EntityDamageEvent.DamageCause damage_cause) {

        int attacker_level = 1;
        double elemental_mastery = 0;

        if (damager != null) {
            if (damager instanceof Player player) {
                PlayerData playerData = PlayerData.get(player);
                PlayerStats playerStats = playerData.getStats();

                elemental_mastery = playerStats.getStat("AST_ELEMENTAL_MASTERY");
                attacker_level = playerData.getLevel();
            } else {
                ActiveMob mythicMob = MythicBukkit.inst().getMobManager().getActiveMob(damager.getUniqueId()).orElse(null);
                attacker_level = (mythicMob != null) ? (int) mythicMob.getLevel() : 1;
            }
        }

        String formula = getConfig().getString("damage-formula");
        assert formula != null;
        Expression expression = new ExpressionBuilder(formula)
                .variables("raw_damage", "attacker_level", "elemental_mastery")
                .build()
                .setVariable("raw_damage", damage.getValue())
                .setVariable("attacker_level", attacker_level)
                .setVariable("elemental_mastery", elemental_mastery);

        damage.setValue(expression.evaluate());
    }
}
