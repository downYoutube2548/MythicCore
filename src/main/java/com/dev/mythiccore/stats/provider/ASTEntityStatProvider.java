package com.dev.mythiccore.stats.provider;

import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.stat.provider.StatProvider;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * The class to use override stupid "EntityStatProvider" class
 * I create this because AttackEvent when use method "getAttacker()" you will get some stupid "EntityStatProvider" class
 * that can only get attacker stat which I don't need What I want is Bukkit "Entity" class,
 * so I create this class and copy the code from "EntityStatProvider" class and add method like "getEntity()" to this class
 */
public class ASTEntityStatProvider implements StatProvider {
    private final Set<NBTItem> equipment = new HashSet<>();
    private final LivingEntity entity;

    public ASTEntityStatProvider(LivingEntity entity) {
        this.entity = entity;
        EntityEquipment equip = entity.getEquipment();
        if (equip != null) {
            ItemStack[] var3 = entity.getEquipment().getArmorContents();

            for (ItemStack equipped : var3) {
                this.registerItem(equipped);
            }

            this.registerItem(entity.getEquipment().getItemInMainHand());
            this.registerItem(entity.getEquipment().getItemInOffHand());
        }
    }

    private void registerItem(ItemStack item) {
        if (item != null && item.getType() != Material.AIR && item.hasItemMeta()) {
            this.equipment.add(NBTItem.get(item));
        }
    }

    public double getStat(String id) {
        double d = 0.0;

        NBTItem nbt;
        for(Iterator<NBTItem> var4 = this.equipment.iterator(); var4.hasNext(); d += nbt.getStat(id)) {
            nbt = var4.next();
        }

        return d;
    }

    public LivingEntity getEntity() {
        return this.entity;
    }
}
