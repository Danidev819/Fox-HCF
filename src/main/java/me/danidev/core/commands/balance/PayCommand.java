package me.danidev.core.commands.balance;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.JavaUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class PayCommand implements CommandExecutor, TabCompleter {

    public PayCommand(Main plugin) {
        plugin.getCommand("pay").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <playerName> <amount>");
            return true;
        }
        Integer amount = JavaUtils.tryParseInt(args[1]);
        if (amount == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
            return true;
        }
        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "You must send money in positive quantities.");
            return true;
        }
        Player senderPlayer = (Player) sender;
        int senderBalance = ((senderPlayer != null) ? Main.get().getEconomyManager().getBalance(senderPlayer.getUniqueId()) : 1024);
        if (senderBalance < amount) {
            sender.sendMessage(ChatColor.RED + "Insufficient funds.");
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (sender.equals(target)) {
            sender.sendMessage(ChatColor.RED + "You cannot send money to yourself.");
            return true;
        }
        Player targetPlayer = target.getPlayer();
        if (!target.hasPlayedBefore() && targetPlayer == null) {
            sender.sendMessage(CC.translate("&cPlayer not found."));
            return true;
        }
        if (targetPlayer == null) {
            return false;
        }
        if (senderPlayer != null) {
            Main.get().getEconomyManager().subtractBalance(senderPlayer.getUniqueId(), amount);
        }
        Main.get().getEconomyManager().addBalance(targetPlayer.getUniqueId(), amount);
        targetPlayer.sendMessage(ChatColor.YELLOW + sender.getName() + " has sent you " + ChatColor.LIGHT_PURPLE + '$' + amount + ChatColor.YELLOW + '.');
        sender.sendMessage(ChatColor.YELLOW + "You have sent " + ChatColor.LIGHT_PURPLE + '$' + amount + ChatColor.YELLOW + " to " + target.getName() + '.');
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return (args.length == 1) ? null : Collections.emptyList();
    }
}
