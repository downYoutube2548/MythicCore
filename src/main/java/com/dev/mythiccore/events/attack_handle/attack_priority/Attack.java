package com.dev.mythiccore.events.attack_handle.attack_priority;

import io.lumine.mythic.lib.api.event.AttackEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class Attack implements Listener {

    public List<Object> listeners = new ArrayList<>();

    public void registerAttackEvent(Object listener) {
        listeners.add(listener);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void attack(AttackEvent event) {

        HashMap<Method, Object> mapEventHandlers = new HashMap<>();

        List<Method> methods = new ArrayList<>();

        for (Object listener : listeners) {
            for (Method method : listener.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(AttackHandle.class)) {
                    methods.add(method);
                    mapEventHandlers.put(method, listener);
                }
            }
        }

        methods.sort(Comparator.comparingInt(method -> method.getAnnotation(AttackHandle.class).priority()));

        // Iterate through the methods and invoke the event handlers
        for (Method method : methods) {
            if (method.isAnnotationPresent(AttackHandle.class)) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 1 && parameterTypes[0].isInstance(event)) {
                    if (!method.getAnnotation(AttackHandle.class).ignoreCancelled() && event.isCancelled()) { continue; }
                    try {
                        method.invoke(mapEventHandlers.get(method), event);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
