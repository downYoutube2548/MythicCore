package com.dev.mythiccore.events.attack_handle.attack_priority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AttackHandle {
    int priority();
    boolean ignoreCancelled() default false;
}
