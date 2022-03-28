package me.danidev.core.managers.timer.type.sotw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import me.danidev.core.Main;
import me.danidev.core.utils.BukkitUtils;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.JavaUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;
@SuppressWarnings("unchecked")
public class SOTWCommand implements CommandExecutor, TabCompleter {


    private static List<String> COMPLETIONS = ImmutableList.of("start", "end");

    private Main plugin;
    public static ArrayList<UUID> enabled;

    public SOTWCommand(Main plugin) {
        plugin.getCommand("sotw").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player)sender;
        if (!sender.hasPermission("fhcf.command.sotw.enable")) {
            if (args.length != 1 || !args[0].equalsIgnoreCase("enable")) {
                sender.sendMessage(CC.translate("&cUsage: /sotw enable"));
                return true;
            }
            if (Main.get().getSotwTimer() == null || Main.get().getSotwTimer().getSotwRunnable() == null) {
                sender.sendMessage(CC.translate(Main.get().getLangConfig().getString("SOTW-ENABLE-NO-SOTW")));
                return true;
            }
            if (enabled.contains(sender)) {
                sender.sendMessage(CC.translate(Main.get().getLangConfig().getString("SOTW-ALREADY-EMABLED")));
                return true;
            }
            enabled.add(player.getUniqueId());
            player.sendMessage(CC.translate(Main.get().getLangConfig().getString("SOTW-ENABLED")));
            return true;
        }
        if (!sender.hasPermission("fhcf.command.sotw.admin")) {
            sender.sendMessage(ChatColor.RED + "No Permission.");
            return true;
        }
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("start")) {
                if (args.length < 2) {
                    sender.sendMessage(
                            ChatColor.RED + "Usage: /" + label + " " + args[0].toLowerCase() + " <duration>");
                    return true;
                }

                long duration = JavaUtils.parse(args[1]);

                if (duration == -1L) {
                    sender.sendMessage(ChatColor.RED + "'" + args[0] + "' is an invalid duration.");
                    return true;
                }

                if (duration < 1000L) {
                    sender.sendMessage(ChatColor.RED + "SOTW protection time must last for at least 20 ticks.");
                    return true;
                }
                
                if (Main.get().getSotwTimer().getSotwRunnable() != null) {
                    sender.sendMessage(
                            ChatColor.RED + "SOTW protection is already enabled, use /" + label + " cancel to end it.");
                    return true;
                }

                Main.get().getSotwTimer().start(duration);
                sender.sendMessage(ChatColor.RED + "Started SOTW protection for " + DurationFormatUtils.formatDurationWords(duration, true, true) + ".");
                return true;
            }

            if (args[0].equalsIgnoreCase("end") || args[0].equalsIgnoreCase("cancel")) {
                if (Main.get().getSotwTimer().cancel()) {
                    sender.sendMessage(ChatColor.RED + "Cancelled SOTW protection.");
                    return true;
                }

                sender.sendMessage(ChatColor.RED + "SOTW protection is not active.");
                return true;
            }else if (args[0].equalsIgnoreCase("enable")) {
                if (Main.get().getSotwTimer().getSotwRunnable() == null) {
                    sender.sendMessage(CC.translate("&cSotw isnt active"));
                    return true;
                }
                if (enabled.contains(((Player) sender).getUniqueId())) {
                    sender.sendMessage(CC.translate("&cYou have already enabled your SOTW."));
                    return true;
                }
                enabled.add(player.getUniqueId());
                player.sendMessage(CC.translate("&cYour SOTW is now Enabled"));
                return true;
            }
        }

        sender.sendMessage(CC.translate("&7&m---------------------------------------------"));
        sender.sendMessage(CC.translate("&b/sotw start <duration> &7- &fStart SOTW Timer."));
        sender.sendMessage(CC.translate("&b/sotw end &7- &fDisable SOTW Timer."));
        sender.sendMessage(CC.translate("&b/sotw enable &7- &fEnable SOTW Timer."));
        sender.sendMessage(CC.translate("&7&m---------------------------------------------"));
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label,
                                      final String[] args) {
        return (args.length == 1) ? BukkitUtils.getCompletions(args, SOTWCommand.COMPLETIONS) : Collections.emptyList();
    }

    static {
        COMPLETIONS = (List) ImmutableList.of((Object) "start", (Object) "end");
        SOTWCommand.enabled = new ArrayList<UUID>();
    }
}
