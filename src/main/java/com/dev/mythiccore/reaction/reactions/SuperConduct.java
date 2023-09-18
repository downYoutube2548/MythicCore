package com.dev.mythiccore.reaction.reactions;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.buff.buffs.ElementalResistanceReduction;
import com.dev.mythiccore.reaction.reaction_type.TriggerAuraReaction;
import com.dev.mythiccore.utils.StatCalculation;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.lib.damage.DamagePacket;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.stats.PlayerStats;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SuperConduct extends TriggerAuraReaction {

    public SuperConduct(String id, String display, String aura, String trigger, double gauge_unit_tax) {
        super(id, display, aura, trigger, gauge_unit_tax);
    }

    @Override
    public void trigger(DamagePacket damage, double gauge_unit, String decay_rate, LivingEntity entity, @Nullable Entity damager, EntityDamageEvent.DamageCause damage_cause) {
        int attacker_level = 1;
        double elemental_mastery = 0;
        double resistance_multiplier = StatCalculation.getResistanceMultiplier(entity.getUniqueId(), getConfig().getString("damage-element"));

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
                .variables("attacker_level", "elemental_mastery", "resistance_multiplier")
                .build()
                .setVariable("attacker_level", attacker_level)
                .setVariable("elemental_mastery", elemental_mastery)
                .setVariable("resistance_multiplier", resistance_multiplier);

        double final_damage = expression.evaluate();

        double aoe_radius = getConfig().getDouble("aoe-radius");
        List<Entity> aoe_entities = new ArrayList<>(entity.getNearbyEntities(aoe_radius, aoe_radius, aoe_radius));
        aoe_entities.add(entity);
        for (Entity aoe_entity : aoe_entities) {
            if (aoe_entity == damager || aoe_entity.isInvulnerable() || (aoe_entity instanceof Player player && (player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR)))) continue;
            if (aoe_entity instanceof LivingEntity aoe_living_entity && !aoe_living_entity.isInvulnerable()) {
                damage(final_damage, damager, aoe_living_entity, getConfig().getString("damage-element"), false, true, damage_cause);
                MythicCore.getBuffManager().getBuff(aoe_living_entity.getUniqueId()).addBuff(new ElementalResistanceReduction(getConfig().getDouble("resistance-reduction"), getConfig().getLong("resistance-reduction-duration"), getConfig().getString("resistance-reduction-element")));
            }
        }
    }
}