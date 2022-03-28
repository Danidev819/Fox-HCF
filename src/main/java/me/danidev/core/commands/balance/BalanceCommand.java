package me.danidev.core.commands.balance;

import me.danidev.core.Main;
import com.google.common.collect.ImmutableList;
import me.danidev.core.utils.BukkitUtils;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.JavaUtils;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class BalanceCommand implements CommandExecutor, TabCompleter {

    private final ImmutableList<String> COMPLETIONS = ImmutableList.of("take", "negate", "minus", "subtract");
    private final ImmutableList<String> GIVE = ImmutableList.of("give", "add");
    private final ImmutableList<String> TAKE = ImmutableList.of("add", "set", "take");

    public BalanceCommand(Main plugin) {
        plugin.getCommand("balance").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean hasStaffPermission = sender.hasPermission(command.getPermission() + ".staff");
        OfflinePlayer target;
        if (args.length > 0 && hasStaffPermission) {
            target = BukkitUtils.offlinePlayerWithNameOrUUID(args[0]);
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <playerName>");
                return true;
            }
            target = (OfflinePlayer) sender;
        }
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(CC.translate("&cPlayer not found."));
            return true;
        }
        UUID uuid = target.getUniqueId();
        int balance = Main.get().getEconomyManager().getBalance(uuid);
        if (args.length < 2 || !hasStaffPermission) {
            sender.sendMessage(ChatColor.YELLOW + (sender.equals(target) ? "Your balance" : ("Balance of " + target.getName())) + " is " + ChatColor.LIGHT_PURPLE + '$' + balance + ChatColor.YELLOW + '.');
            return true;
        }
        if (GIVE.contains(args[1].toLowerCase())) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + target.getName() + ' ' + args[1] + " <amount>");
                return true;
            }
            Integer amount = JavaUtils.tryParseInt(args[2]);
            if (amount == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a valid number.");
                return true;
            }
            int newBalance = Main.get().getEconomyManager().addBalance(uuid, amount);
            sender.sendMessage(new String[]{ChatColor.YELLOW + "Added " + ChatColor.LIGHT_PURPLE + '$' + JavaUtils.format(amount) + ChatColor.YELLOW + " to balance of " + target.getName() + '.', ChatColor.YELLOW + "Balance of " + target.getName() + " is now " + ChatColor.LIGHT_PURPLE + '$' + newBalance + ChatColor.YELLOW + '.'});
            return true;
        } else if (TAKE.contains(args[1].toLowerCase())) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + target.getName() + ' ' + args[1] + " <amount>");
                return true;
            }
            Integer amount = JavaUtils.tryParseInt(args[2]);
            if (amount == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a valid number.");
                return true;
            }
            int newBalance = Main.get().getEconomyManager().subtractBalance(uuid, amount);
            sender.sendMessage(new String[]{ChatColor.YELLOW + "Taken " + ChatColor.LIGHT_PURPLE + '$' + JavaUtils.format(amount) + ChatColor.YELLOW + " from balance of " + target.getName() + '.', ChatColor.YELLOW + "Balance of " + target.getName() + " is now " + ChatColor.LIGHT_PURPLE + '$' + newBalance + ChatColor.YELLOW + '.'});
            return true;
        } else {
            if (!args[1].equalsIgnoreCase("set")) {
                sender.sendMessage(ChatColor.YELLOW + (sender.equals(target) ? "Your balance" : ("Balance of " + target.getName())) + " is " + ChatColor.LIGHT_PURPLE + '$' + balance + ChatColor.YELLOW + '.');
                return true;
            }
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + target.getName() + ' ' + args[1] + " <amount>");
                return true;
            }
            Integer amount = JavaUtils.tryParseInt(args[2]);
            if (amount == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a valid number.");
                return true;
            }
            int newBalance = Main.get().getEconomyManager().setBalance(uuid, amount);
            sender.sendMessage(ChatColor.YELLOW + "Set balance of " + target.getName() + " to " + ChatColor.LIGHT_PURPLE + '$' + JavaUtils.format(newBalance) + ChatColor.YELLOW + '.');
            return true;
        }
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        return (args.length == 2) ? BukkitUtils.getCompletions(args, COMPLETIONS) : Collections.emptyList();
    }
}
