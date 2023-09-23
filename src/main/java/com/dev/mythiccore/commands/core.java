package com.dev.mythiccore.commands;

import com.dev.mythiccore.MythicCore;
import com.dev.mythiccore.events.attack_handle.attack_priority.TriggerReaction;
import com.dev.mythiccore.utils.ConfigLoader;
import com.dev.mythiccore.utils.Utils;
import de.tr7zw.changeme.nbtapi.NBTItem;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.damage.DamagePacket;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.element.Element;
import io.lumine.mythic.lib.player.PlayerMetadata;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class core implements CommandExecutor, TabExecutor {

    public static Entity entity;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player player) {
            if (args.length >= 1) {
                if (args[0].equals("apply-aura")) {
                    if (args.length == 4) {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target != null) {
                            List<String> auras = new ArrayList<>(Objects.requireNonNull(MythicCore.getInstance().getConfig().getConfigurationSection("Special-Aura")).getKeys(false));
                            List<String> elements = new ArrayList<>();
                            for (Element element : MythicLib.plugin.getElements().getAll()) {
                                auras.add(element.getId());
                                elements.add(element.getId());
                            }
                            if (auras.contains(args[2])) {
                                double gauge = Double.parseDouble(Utils.splitTextAndNumber(args[3])[0]);
                                String decay_rate = Utils.splitTextAndNumber(args[3])[1];

                                if (Objects.requireNonNull(MythicCore.getInstance().getConfig().getConfigurationSection("General.decay-rate")).getKeys(false).contains(decay_rate)) {

                                    List<String> allow_auras = new ArrayList<>(Objects.requireNonNull(MythicCore.getInstance().getConfig().getConfigurationSection("Special-Aura")).getKeys(false));
                                    allow_auras.addAll(ConfigLoader.getAuraWhitelist());

                                    if (allow_auras.contains(args[2])) MythicCore.getAuraManager().getAura(target.getUniqueId()).addAura(args[2], gauge, decay_rate);
                                    if (gauge > 0) {
                                        if (elements.contains(args[2])) TriggerReaction.triggerReactions(new DamagePacket(0, Element.valueOf(args[2]), DamageType.SKILL), gauge, decay_rate, target, player, new PlayerMetadata(PlayerData.get(player).getStats().getMap(), EquipmentSlot.MAIN_HAND), EntityDamageEvent.DamageCause.CUSTOM);
                                    }
                                    sender.sendMessage(ConfigLoader.getMessage("apply-aura-success", true)
                                            .replace("{aura}", args[2])
                                            .replace("{player}", args[1])
                                            .replace("{gauge}", args[3])
                                    );
                                } else {
                                    sender.sendMessage(ConfigLoader.getMessage("invalid-decay-rate", true));
                                }
                            } else {
                                sender.sendMessage(ConfigLoader.getMessage("invalid-aura", true));
                            }
                        } else {
                            sender.sendMessage(ConfigLoader.getMessage("player-not-found", true));
                        }
                    } else {
                        sender.sendMessage(ConfigLoader.getMessage("syntax-error", true));
                    }
                } else if (args[0].equals("nbt")) {
                    NBTItem nbtItem = new NBTItem(player.getInventory().getItemInMainHand());
                    Bukkit.broadcastMessage(String.valueOf(nbtItem.getLong("MMOITEMS_AST_INTERNAL_COOLDOWN")));
                } else {
                    sender.sendMessage(ConfigLoader.getMessage("syntax-error", true));
                }
            } else {
                sender.sendMessage(ConfigLoader.getMessage("syntax-error", true));
            }
        } else {
            sender.sendMessage(ChatColor.RED+"You must be player to execute this command!");
        }

        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        List<String> output = new ArrayList<>();
        if (args.length == 1) {
            List<String> list = List.of("apply-aura", "nbt");
            output = tabComplete(args[0], list);
        }
        if (args.length > 1) {
            if (args[0].equals("apply-aura")) {
                if (args.length == 2) {
                    List<String> players = new ArrayList<>();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        players.add(player.getName());
                    }
                    output = tabComplete(args[1], players);
                }
                else if (args.length == 3) {
                    List<String> auras = new ArrayList<>(Objects.requireNonNull(MythicCore.getInstance().getConfig().getConfigurationSection("Special-Aura")).getKeys(false));
                    for (Element element : MythicLib.plugin.getElements().getAll()) {
                        auras.add(element.getId());
                    }
                    output = tabComplete(args[2], auras);
                }
                else if (args.length == 4) {
                    output = new ArrayList<>(Objects.requireNonNull(MythicCore.getInstance().getConfig().getConfigurationSection("General.decay-rate")).getKeys(false));
                }
            }
        }

        return output;
    }

    public static List<String> tabComplete(String a, List<String> arg) {
        List<String> matches = new ArrayList<>();
        String search = a.toLowerCase(Locale.ROOT);
        for (String s : arg) {
            if (s.toLowerCase(Locale.ROOT).startsWith(search)) {
                matches.add(s);
            }
        }
        return matches;
    }
}
