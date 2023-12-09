package com.dev.mythiccore.events;

import com.dev.mythiccore.mythic_mechanics.apply.ElementalDamage;
import com.dev.mythiccore.mythic_mechanics.apply.ElementalShield;
import com.dev.mythiccore.mythic_mechanics.apply.ReduceDefense;
import com.dev.mythiccore.mythic_mechanics.apply.ReduceResistance;
import com.dev.mythiccore.mythic_mechanics.modify.SetDefense;
import com.dev.mythiccore.mythic_mechanics.modify.SetElementalDamage;
import com.dev.mythiccore.mythic_mechanics.modify.SetResistance;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MythicMechanicLoad implements Listener {

    @EventHandler
    public void onMythicMechanicLoad(MythicMechanicLoadEvent event) {
        if(event.getMechanicName().equalsIgnoreCase("elemental_damage")) {
            event.register(new ElementalDamage(event.getConfig()));
        }
        else if (event.getMechanicName().equalsIgnoreCase("reduce_defense"))	{
            event.register(new ReduceDefense(event.getConfig()));
        }
        else if (event.getMechanicName().equalsIgnoreCase("reduce_resistance"))	{
            event.register(new ReduceResistance(event.getConfig()));
        }
        else if (event.getMechanicName().equalsIgnoreCase("elemental_shield"))	{
            event.register(new ElementalShield(event.getConfig()));
        }
        else if (event.getMechanicName().equalsIgnoreCase("set_elemental_damage"))	{
            event.register(new SetElementalDamage(event.getConfig()));
        }
        else if (event.getMechanicName().equalsIgnoreCase("set_defense"))	{
            event.register(new SetDefense(event.getConfig()));
        }
        else if (event.getMechanicName().equalsIgnoreCase("set_resistance"))	{
            event.register(new SetResistance(event.getConfig()));
        }
    }
}
