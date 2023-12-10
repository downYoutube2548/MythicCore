package com.dev.mythiccore.events.attack_handle;

import com.dev.mythiccore.library.ASTEntityStatProvider;
import com.dev.mythiccore.utils.ConfigLoader;
import de.tr7zw.nbtapi.NBTItem;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.lib.api.event.AttackEvent;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.damage.DamagePacket;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.damage.ProjectileAttackMetadata;
import io.lumine.mythic.lib.element.Element;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;

/**
 * This class use to modify damage element of each damage cause
 * and set element of non-element damage to default element
 * and operation of disable regular damage will happen here
 */
public class AttackModifier implements Listener {

    /** Call when any entity attack
     * Use to set ALL non-elemental damage to default element
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityAttack(AttackEvent event) {

        for (DamagePacket packet : event.getDamage().getPackets()) {
            if (Arrays.asList(packet.getTypes()).contains(DamageType.DOT)) return;
        }

        Element defaultElement = Objects.requireNonNull(Element.valueOf(ConfigLoader.getDefaultElement()));

        // if damage doesn't have element
        if (event.getDamage().collectElements().size() == 0) {
            // if attacker is player -> check if player's weapon is disabled regular damage -> if yes then set regular damage to 0
            if (event instanceof PlayerAttackEvent e) {
                Player attacker = e.getAttacker().getPlayer();
                ItemStack item = event.getAttack() instanceof ProjectileAttackMetadata projectile ? (ItemStack) (projectile.getProjectile().getMetadata("ATTACK_WEAPON").get(0).value()) : attacker.getInventory().getItem(e.getAttacker().getActionHand().toBukkit());
                if (item != null && !item.getType().equals(Material.AIR)) {
                    NBTItem nbt = new NBTItem(item);
                    String damage_formula = ConfigLoader.getDefaultDamageCalculation();
                    if (!nbt.getString("MMOITEMS_AST_DAMAGE_FORMULA").equals("")) {
                        damage_formula = nbt.getString("MMOITEMS_AST_DAMAGE_FORMULA");
                    }

                    double damage = event.getDamage().getDamage();

                    boolean stop = false;
                    byte disable_regular_damage = nbt.getByte("MMOITEMS_AST_DISABLE_REGULAR_DAMAGE");
                    if (disable_regular_damage == 1) {
                        event.getDamage().getPackets().clear();
                        stop = true;
                    }

                    for (Element element : Element.values()) {
                        if (e.getAttacker().getStat("AST_"+element.getId()+"_PERCENT") > 0) {
                            double base = Double.parseDouble(PlaceholderAPI.setPlaceholders(attacker, ConfigLoader.getDamageCalculation("damage-calculation-formula." + damage_formula + ".base")));

                            double attack_speed_multiplier = damage / e.getAttacker().getStat("ATTACK_DAMAGE");

                            event.getDamage().add(base * attack_speed_multiplier, element, DamageType.PHYSICAL);
                            stop = true;
                        }
                    }
                    if (stop) return;
                }

            // if attacker is mob -> check if mob is disable to deal regular damage -> if yes then set regular damage to 0
            } else {
                try {
                    LivingEntity attacker = null;
                    if (event.getAttack().getAttacker() instanceof ASTEntityStatProvider statProvider) {
                        attacker = statProvider.getEntity();
                    } else {
                        if (event.toBukkit() instanceof EntityDamageByEntityEvent e) {
                            if (e.getDamager() instanceof LivingEntity livingEntity) attacker = livingEntity;
                            else attacker = (LivingEntity) ((Projectile) e.getDamager()).getShooter();
                        }
                    }

                    if (attacker != null) {
                        ActiveMob attackerMythicMob = MythicBukkit.inst().getMobManager().getActiveMob(attacker.getUniqueId()).orElse(null);

                        if (attackerMythicMob != null && attackerMythicMob.getVariables().has("AST_ELEMENTAL_DAMAGE_AMOUNT") && attackerMythicMob.getVariables().has("AST_ELEMENTAL_DAMAGE_ELEMENT") && attackerMythicMob.getVariables().has("AST_ELEMENTAL_DAMAGE_GAUGE_UNIT") && attackerMythicMob.getVariables().has("AST_ELEMENTAL_DAMAGE_COOLDOWN_SOURCE")) {

                            VariableRegistry variables = attackerMythicMob.getVariables();
                            double damage_amount = variables.getFloat("AST_ELEMENTAL_DAMAGE_AMOUNT");
                            Element element = Element.valueOf(variables.getString("AST_ELEMENTAL_DAMAGE_ELEMENT"));
                            if (element == null) return;

                            event.getDamage().getPackets().get(0).setTypes(new DamageType[]{DamageType.SKILL});
                            event.getDamage().getPackets().get(0).setElement(element);
                            event.getDamage().getPackets().get(0).setValue(damage_amount);

                            return;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // set to specify element if the damage cause is have elemental modifier (in config "Elemental-Modifier")
            Element element;
            if (ConfigLoader.getElementalModifier().containsKey(event.toBukkit().getCause().name())) {
                element = Element.valueOf(ConfigLoader.getElementalModifier().get(event.toBukkit().getCause().name()));
            }
            else {
                element = defaultElement;
            }
            event.getDamage().getPackets().get(0).setElement(element);

        }

        // if have more than 1 element
        else {
            if (event.getDamage().getPackets().size() == 1) { return; }

            for (DamagePacket packet : event.getDamage().getPackets()) {

                // if damage is non-element then do same as above
                if (packet.getElement() == null) {

                    if (event instanceof PlayerAttackEvent e) {
                        Player attacker = e.getAttacker().getPlayer();
                        ItemStack item = event.getAttack() instanceof ProjectileAttackMetadata projectile ? (ItemStack) (projectile.getProjectile().getMetadata("ATTACK_WEAPON").get(0).value()) : attacker.getInventory().getItem(e.getAttacker().getActionHand().toBukkit());
                        if (item != null && !item.getType().equals(Material.AIR)) {
                            NBTItem nbt = new NBTItem(item);
                            byte disable_regular_damage = nbt.getByte("MMOITEMS_AST_DISABLE_REGULAR_DAMAGE");
                            if (disable_regular_damage == 1) {
                                event.getDamage().getPackets().remove(packet);
                                return;
                            }
                        }
                    }

                    packet.setElement(defaultElement);
                }
            }
        }
    }
}
