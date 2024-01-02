package com.dev.mythiccore.reaction;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.aura.AuraData;
import com.dev.mythiccore.enums.AttackSource;
import com.dev.mythiccore.library.attackMetadata.ASTAttackMetadata;
import com.dev.mythiccore.library.ASTEntityStatProvider;
import com.dev.mythiccore.utils.ConfigLoader;
import com.dev.mythiccore.utils.DamageManager;
import com.dev.mythiccore.utils.Utils;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.stat.StatMap;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.element.Element;
import io.lumine.mythic.lib.player.PlayerMetadata;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public abstract class ElementalReaction {

    private final String id;
    private final String display;
    private final ConfigurationSection config;

    public ElementalReaction(String id, ConfigurationSection config, String display) {
        this.id = id;
        this.display = display;
        this.config = config;
    }

    public String getId() {
        return this.id;
    }
    public String getDisplay() {
        return Utils.colorize(this.display);
    }

    public AuraData getAuraData(UUID uuid) {
        return MythicCore.getAuraManager().getAura(uuid);
    }
    public ConfigurationSection getConfig() {
        return config;
    }

    public void damage(double amount, Entity caster, LivingEntity target, boolean knockback, EntityDamageEvent.DamageCause damage_cause) {
         damage(new DamageMetadata(amount, DamageType.DOT) , caster, target, knockback, damage_cause);
    }

    public void damage(double amount, Entity caster, LivingEntity target, String element, boolean damage_calculate, double aura_gauge_unit, String aura_decay_rate, String cooldown_source, long internal_cooldown, boolean knockback, EntityDamageEvent.DamageCause damage_cause) {
        Element e = Objects.requireNonNull(MythicLib.plugin.getElements().get(element));
        DamageMetadata damageMetadata = new DamageMetadata(amount, e, damage_calculate ? DamageType.SKILL : DamageType.DOT);
        damage(damageMetadata, caster, target, aura_gauge_unit, aura_decay_rate, cooldown_source, internal_cooldown, knockback, damage_cause);
    }

    public void damage(double amount, Entity caster, LivingEntity target, @NotNull Element element, boolean damage_calculate, double aura_gauge_unit, String aura_decay_rate, String cooldown_source, long internal_cooldown, boolean knockback, EntityDamageEvent.DamageCause damage_cause) {
        DamageMetadata damageMetadata = new DamageMetadata(amount, element, damage_calculate ? DamageType.SKILL : DamageType.DOT);
        damage(damageMetadata, caster, target, aura_gauge_unit, aura_decay_rate, cooldown_source, internal_cooldown, knockback, damage_cause);
    }

    public void damage(double amount, Entity caster, LivingEntity target, String element, boolean damage_calculate, boolean knockback, EntityDamageEvent.DamageCause damage_cause) {
        Element e = Objects.requireNonNull(MythicLib.plugin.getElements().get(element));
        damage(amount, caster, target, e, damage_calculate, knockback, damage_cause);
    }
    public void damage(double amount, Entity caster, LivingEntity target, @NotNull Element element, boolean damage_calculate, boolean knockback, EntityDamageEvent.DamageCause damage_cause) {
        DamageMetadata damageMetadata = new DamageMetadata(amount, element, damage_calculate ? DamageType.SKILL : DamageType.DOT);
        damage(damageMetadata, caster, target, knockback, damage_cause);
    }

    private void damage(DamageMetadata damage, Entity caster, LivingEntity target, boolean knockback, EntityDamageEvent.DamageCause damage_cause) {

        if (caster instanceof Player) {
            PlayerData playerData = PlayerData.get(caster.getUniqueId());

            StatMap statMap = playerData.getMMOPlayerData().getStatMap();
            PlayerMetadata playerMetadata = new PlayerMetadata(statMap, EquipmentSlot.MAIN_HAND);
            AttackMetadata attack = new ASTAttackMetadata(damage, target, playerMetadata, "0", ConfigLoader.getDefaultDamageCalculation(), 100, AttackSource.REACTION);

            Bukkit.getScheduler().runTask(MythicCore.getInstance(), () -> DamageManager.registerAttack(attack, knockback, true, damage_cause));

        }  else {
            AttackMetadata attack = new ASTAttackMetadata(damage, target, caster != null ? new ASTEntityStatProvider((LivingEntity) caster) : null, "0", ConfigLoader.getDefaultDamageCalculation(), 100, AttackSource.REACTION);
            Bukkit.getScheduler().runTask(MythicCore.getInstance(), () -> DamageManager.registerAttack(attack, knockback, true, damage_cause));
        }
    }

    private void damage(DamageMetadata damage, Entity caster, LivingEntity target, double gauge_unit, String decay_rate, String cooldown_source, long internal_cooldown, boolean knockback, EntityDamageEvent.DamageCause damage_cause) {

        if (caster instanceof Player) {
            PlayerData playerData = PlayerData.get(caster.getUniqueId());

            StatMap statMap = playerData.getMMOPlayerData().getStatMap();
            PlayerMetadata playerMetadata = new PlayerMetadata(statMap, EquipmentSlot.MAIN_HAND);
            AttackMetadata attack = new ASTAttackMetadata(damage, target, playerMetadata, cooldown_source, internal_cooldown, gauge_unit, decay_rate, ConfigLoader.getDefaultDamageCalculation(), 100, AttackSource.SKILL);

            Bukkit.getScheduler().runTask(MythicCore.getInstance(), () -> DamageManager.registerAttack(attack, knockback, true, damage_cause));

        }  else {
            AttackMetadata attack = new ASTAttackMetadata(damage, target, caster != null ? new ASTEntityStatProvider((LivingEntity) caster) : null, cooldown_source, internal_cooldown, gauge_unit, decay_rate, ConfigLoader.getDefaultDamageCalculation(), 100, AttackSource.SKILL);
            Bukkit.getScheduler().runTask(MythicCore.getInstance(), ()-> DamageManager.registerAttack(attack, knockback, true, damage_cause));

        }
    }

    public void spawnParticle(Entity entity, List<String> particles) {
        try {

            for (String p : particles) {
                String[] raw_particle = p.split(":");
                String particle = raw_particle[0];
                double speed = Double.parseDouble(raw_particle[1]);
                int count = Integer.parseInt(raw_particle[2]);

                entity.getWorld().spawnParticle(Particle.valueOf(particle), entity.getLocation().add(0, 0.5, 0), count, 0, 0, 0, speed);
            }

        } catch (NumberFormatException ignored) {}
    }

    public void playSound(Entity entity, List<String> sounds) {
        try {
            for (String s : sounds) {
                String[] raw_sound = s.split(":");
                String sound = raw_sound[0];
                float volume = Float.parseFloat(raw_sound[1]);
                float pitch = Float.parseFloat(raw_sound[2]);

                entity.getWorld().playSound(entity.getLocation(), Sound.valueOf(sound), volume, pitch);
            }
        } catch (NumberFormatException ignored) {}
    }
}
