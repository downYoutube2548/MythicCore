package com.dev.mythiccore.events;

import com.dev.mythiccore.mythic.mechanics.apply.*;
import com.dev.mythiccore.mythic.mechanics.modify.SetDefense;
import com.dev.mythiccore.mythic.mechanics.modify.SetElementalDamage;
import com.dev.mythiccore.mythic.mechanics.modify.SetResistance;
import com.dev.mythiccore.mythic.placeholders.SnapshotPlaceholder;
import com.dev.mythiccore.mythic.targeters.SnapshotTargeter;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.bukkit.events.MythicReloadedEvent;
import io.lumine.mythic.bukkit.events.MythicTargeterLoadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MythicLoad implements Listener {

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
        else if (event.getMechanicName().equalsIgnoreCase("snapshot"))	{
            event.register(new Snapshot(event.getConfig()));
        }
        else if (event.getMechanicName().equalsIgnoreCase("clearAura"))	{
            event.register(new ClearAura(event.getConfig()));
        }
        else if (event.getMechanicName().equalsIgnoreCase("applyAura"))	{
            event.register(new ApplyAura(event.getConfig()));
        }
    }

    @EventHandler
    public void onTargeterLoad(MythicTargeterLoadEvent event) {
        if(event.getTargeterName().equalsIgnoreCase("snapshot")) {
            event.register(new SnapshotTargeter(event.getContainer().getManager(), event.getConfig()));
        }
    }

    @EventHandler
    public void mythicReload(MythicReloadedEvent event) {
        SnapshotPlaceholder.register();
    }
}
