package me.danidev.core.commands;

import me.danidev.core.Main;
import me.danidev.core.managers.timer.type.ProtectionTimer;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.DurationFormatter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class PvPTimerCommand implements CommandExecutor {

    public PvPTimerCommand(Main plugin) {
        plugin.getCommand("pvptimer").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        if (Main.get().isKitMap()) return true;

        Player player = (Player) sender;

        if (args.length < 1) {
            sender.sendMessage(CC.translate("&7------------------------------"));
            sender.sendMessage(CC.translate("&6&lPvPTimer Commands"));
            sender.sendMessage(CC.translate(""));
            sender.sendMessage(CC.translate("&e/" + label + " enable &7- &fEnable your PvP Timer."));
            sender.sendMessage(CC.translate("&e/" + label + " time &7- &fCheck your time of PvP Timer."));
            sender.sendMessage(CC.translate("&7------------------------------"));
            return true;
        }

        ProtectionTimer pvpTimer = Main.get().getTimerManager().protectionTimer;

        if (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("remove")) {
            if (pvpTimer.getRemaining(player) > 0L) {
                player.sendMessage(ChatColor.RED + "Your " + pvpTimer.getName() + ChatColor.RED + " timer is now off.");
                pvpTimer.clearCooldown(player);
                return true;
            }

            if (pvpTimer.getLegible().remove(player.getUniqueId())) {
                player.sendMessage(ChatColor.YELLOW + "You will no longer be legible for your " + pvpTimer.getName() + ChatColor.YELLOW + " when you leave spawn.");
                return true;
            }
            sender.sendMessage(ChatColor.RED + "Your " + pvpTimer.getName() + ChatColor.RED + " timer is currently not active.");
        }
        else if (args[0].equalsIgnoreCase("remaining") || args[0].equalsIgnoreCase("time") || args[0].equalsIgnoreCase("check")) {
            long remaining = pvpTimer.getRemaining(player);

            if (remaining <= 0L) {
                sender.sendMessage(ChatColor.RED + "Your " + pvpTimer.getName() + ChatColor.RED + " timer is currently not active.");
                return true;
            }
            sender.sendMessage(ChatColor.YELLOW + "Your " + pvpTimer.getName() + ChatColor.YELLOW + " timer is active for another " + ChatColor.BOLD + DurationFormatter.getRemaining(remaining, true, false) + ChatColor.YELLOW + (pvpTimer.isPaused(player) ? " and is currently paused" : "") + '.');
        }
        else {
            sender.sendMessage(CC.translate("&7------------------------------"));
            sender.sendMessage(CC.translate("&6&lPvPTimer Commands"));
            sender.sendMessage(CC.translate(""));
            sender.sendMessage(CC.translate("&e/" + label + " enable &7- &fEnable your PvP Timer."));
            sender.sendMessage(CC.translate("&e/" + label + " time &7- &fCheck your time of PvP Timer."));
            sender.sendMessage(CC.translate("&7------------------------------"));
        }
        return true;
    }
}
