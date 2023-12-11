package com.dev.mythiccore.listener;

import com.dev.mythiccore.enums.AttackSource;
import com.dev.mythiccore.library.ASTAttackMetadata;
import com.dev.mythiccore.library.ASTEntityStatProvider;
import com.dev.mythiccore.library.ASTProjectileAttackMetadata;
import com.dev.mythiccore.listener.events.attack.MiscAttackEvent;
import com.dev.mythiccore.listener.events.attack.MobAttackEvent;
import com.dev.mythiccore.listener.events.attack.PlayerAttackEvent;
import com.dev.mythiccore.utils.ConfigLoader;
import com.dev.mythiccore.utils.Utils;
import de.tr7zw.nbtapi.NBTItem;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.lib.api.event.AttackEvent;
import io.lumine.mythic.lib.api.stat.provider.EntityStatProvider;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.DamagePacket;
import io.lumine.mythic.lib.damage.ProjectileAttackMetadata;
import io.lumine.mythic.lib.player.PlayerMetadata;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

/**
 * The class to call "MiscAttackEvent" and "MobAttackEvent"
 */
public class AttackEventListener implements Listener {

    @EventHandler(
            priority = EventPriority.HIGH,
            ignoreCancelled = true
    )
    public void registerEvents(AttackEvent event) {

        if (event.getAttack() instanceof ASTAttackMetadata astAttack) {

            if (astAttack.getAttacker() instanceof ASTEntityStatProvider) {
                Bukkit.getPluginManager().callEvent(new MobAttackEvent((EntityDamageByEntityEvent) event.toBukkit(), astAttack));
            } else if (astAttack.getAttacker() instanceof PlayerMetadata) {
                Bukkit.getPluginManager().callEvent(new PlayerAttackEvent((EntityDamageByEntityEvent) event.toBukkit(), astAttack));
            }

        } else {

            if (event.getAttack().getAttacker() instanceof EntityStatProvider) {
                // mob attack

                LivingEntity real_attacker;
                if (((EntityDamageByEntityEvent) event.toBukkit()).getDamager() instanceof LivingEntity attacker) {
                    real_attacker = attacker;
                } else {
                    real_attacker = (LivingEntity) ((Projectile) ((EntityDamageByEntityEvent) event.toBukkit()).getDamager()).getShooter();
                }
                if (real_attacker == null) return;

                double gauge_unit = ConfigLoader.getDefaultGaugeUnit();
                String decay_rate = ConfigLoader.getDefaultDecayRate();
                String cooldown_source = "default";
                long internal_cooldown = ConfigLoader.getInternalCooldown("default");
                String damage_calculation = ConfigLoader.getDefaultDamageCalculation();
                double talent_percent = 100;

                ActiveMob mythicMob = MythicBukkit.inst().getMobManager().getActiveMob(real_attacker.getUniqueId()).orElse(null);
                if (mythicMob != null && mythicMob.getVariables().has("AST_ELEMENTAL_DAMAGE_AMOUNT") && mythicMob.getVariables().has("AST_ELEMENTAL_DAMAGE_ELEMENT") && mythicMob.getVariables().has("AST_ELEMENTAL_DAMAGE_GAUGE_UNIT") && mythicMob.getVariables().has("AST_ELEMENTAL_DAMAGE_COOLDOWN_SOURCE") && mythicMob.getVariables().has("AST_ELEMENTAL_DAMAGE_INTERNAL_COOLDOWN") && mythicMob.getVariables().has("AST_ELEMENTAL_DAMAGE_FORMULA") && mythicMob.getVariables().has("AST_ELEMENTAL_DAMAGE_PERCENT")) {
                    VariableRegistry variables = mythicMob.getVariables();
                    gauge_unit = Double.parseDouble(Utils.splitTextAndNumber(variables.getString("AST_ELEMENTAL_DAMAGE_GAUGE_UNIT"))[0]);
                    decay_rate = Utils.splitTextAndNumber(variables.getString("AST_ELEMENTAL_DAMAGE_GAUGE_UNIT"))[1];
                    cooldown_source = variables.getString("AST_ELEMENTAL_DAMAGE_COOLDOWN_SOURCE");
                    internal_cooldown = variables.getInt("AST_ELEMENTAL_DAMAGE_INTERNAL_COOLDOWN");
                    damage_calculation = variables.getString("AST_ELEMENTAL_DAMAGE_FORMULA");
                    talent_percent = variables.getFloat("AST_ELEMENTAL_DAMAGE_PERCENT");
                }

                AttackMetadata astAttack = (event.getAttack() instanceof ProjectileAttackMetadata) ? new ASTProjectileAttackMetadata((ProjectileAttackMetadata) event.getAttack(), cooldown_source, internal_cooldown, gauge_unit, decay_rate, damage_calculation, talent_percent, AttackSource.NORMAL) : new ASTAttackMetadata(event.getAttack(), cooldown_source, internal_cooldown, gauge_unit, decay_rate, damage_calculation, talent_percent, AttackSource.NORMAL);
                Bukkit.getPluginManager().callEvent(new MobAttackEvent((EntityDamageByEntityEvent) event.toBukkit(), astAttack));

            } else if (event.getAttack().getAttacker() instanceof PlayerMetadata playerMetadata) {
                // player attack

                double gauge_unit = ConfigLoader.getDefaultGaugeUnit();
                String decay_rate = ConfigLoader.getDefaultDecayRate();
                String cooldown_source = "default";
                long internal_cooldown = ConfigLoader.getInternalCooldown("default");
                String damage_formula = ConfigLoader.getDefaultDamageCalculation();

                ItemStack player_weapon = (event.getAttack() instanceof ProjectileAttackMetadata projectile) ? (ItemStack) projectile.getProjectile().getMetadata("ATTACK_WEAPON").get(0).value() : playerMetadata.getPlayer().getInventory().getItem(playerMetadata.getActionHand().toBukkit());
                if (player_weapon != null && !player_weapon.getType().equals(Material.AIR)) {
                    NBTItem nbt = new NBTItem(player_weapon);

                    if (!nbt.getString("MMOITEMS_AST_GAUGE_UNIT").equals("")) {
                        gauge_unit = Double.parseDouble(Utils.splitTextAndNumber(nbt.getString("MMOITEMS_AST_GAUGE_UNIT"))[0]);
                        decay_rate = Utils.splitTextAndNumber(nbt.getString("MMOITEMS_AST_GAUGE_UNIT"))[1];
                    }

                    if (!nbt.getString("MMOITEMS_AST_INTERNAL_COOLDOWN").equals("")) {
                        cooldown_source = nbt.getString("MMOITEMS_AST_INTERNAL_COOLDOWN").split(" ")[0];
                        long raw_internal_cooldown = Long.parseLong(nbt.getString("MMOITEMS_AST_INTERNAL_COOLDOWN").split(" ")[1]);
                        internal_cooldown = raw_internal_cooldown < 0 ? ConfigLoader.getInternalCooldown(cooldown_source) : raw_internal_cooldown;
                    }

                    if (!nbt.getString("MMOITEMS_AST_DAMAGE_FORMULA").equals("")) {
                        damage_formula = nbt.getString("MMOITEMS_AST_DAMAGE_FORMULA");
                    }
                }

                AttackMetadata astAttack = (event.getAttack() instanceof ProjectileAttackMetadata) ? new ASTProjectileAttackMetadata((ProjectileAttackMetadata) event.getAttack(), cooldown_source, internal_cooldown, gauge_unit, decay_rate, damage_formula, 100, AttackSource.NORMAL) : new ASTAttackMetadata(event.getAttack(), cooldown_source, internal_cooldown, gauge_unit, decay_rate, damage_formula, 100, AttackSource.NORMAL);
                Bukkit.getPluginManager().callEvent(new PlayerAttackEvent((EntityDamageByEntityEvent) event.toBukkit(), astAttack));

            } else {
                // misc attack

                Bukkit.getPluginManager().callEvent(new MiscAttackEvent(event.toBukkit(), event.getAttack()));

            }
        }
    }
}
