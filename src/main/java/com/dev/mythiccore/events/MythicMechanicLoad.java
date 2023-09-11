package com.dev.mythiccore.events;

import com.dev.mythiccore.mechanics.apply.elemental_damage;
import com.dev.mythiccore.mechanics.apply.elemental_shield;
import com.dev.mythiccore.mechanics.apply.reduce_defense;
import com.dev.mythiccore.mechanics.apply.reduce_resistance;
import com.dev.mythiccore.mechanics.stats.set_defense;
import com.dev.mythiccore.mechanics.stats.set_elemental_damage;
import com.dev.mythiccore.mechanics.stats.set_resistance;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MythicMechanicLoad implements Listener {

    @EventHandler
    public void onMythicMechanicLoad(MythicMechanicLoadEvent event) {
        if(event.getMechanicName().equalsIgnoreCase("elemental_damage")) {
            event.register(new elemental_damage(event.getConfig()));
        }
        else if (event.getMechanicName().equalsIgnoreCase("reduce_defense"))	{
            event.register(new reduce_defense(event.getConfig()));
        }
        else if (event.getMechanicName().equalsIgnoreCase("reduce_resistance"))	{
            event.register(new reduce_resistance(event.getConfig()));
        }
        else if (event.getMechanicName().equalsIgnoreCase("elemental_shield"))	{
            event.register(new elemental_shield(event.getConfig()));
        }
        else if (event.getMechanicName().equalsIgnoreCase("set_elemental_damage"))	{
            event.register(new set_elemental_damage(event.getConfig()));
        }
        else if (event.getMechanicName().equalsIgnoreCase("set_defense"))	{
            event.register(new set_defense(event.getConfig()));
        }
        else if (event.getMechanicName().equalsIgnoreCase("set_resistance"))	{
            event.register(new set_resistance(event.getConfig()));
        }
    }
}
