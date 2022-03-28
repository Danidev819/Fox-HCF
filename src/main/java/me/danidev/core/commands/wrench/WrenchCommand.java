package me.danidev.core.commands.wrench;

import me.danidev.core.Main;
import me.danidev.core.utils.JavaUtils;
import me.danidev.core.managers.wrench.Wrench;

import com.google.common.base.Optional;

import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class WrenchCommand implements CommandExecutor {
    
    public WrenchCommand(Main plugin) {
        plugin.getCommand("wrench").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /" + label + " <spawn|setspawners|setendframes>");
            return true;
        }

        if (args[0].equalsIgnoreCase("spawn")) {
            ItemStack wrench = new Wrench().getItemIfPresent();

            player.getInventory().addItem(wrench);
            sender.sendMessage(ChatColor.YELLOW + "You have given yourself a " + wrench.getItemMeta().getDisplayName() + ChatColor.YELLOW + '.');
            return true;
        }

        Optional<Wrench> crowbarOptional = Wrench.fromStack(player.getItemInHand());

        if (!crowbarOptional.isPresent()) {
            sender.sendMessage(ChatColor.RED + "You are not holding a Wrench.");
            return true;
        }

        if (args[0].equalsIgnoreCase("setspawners")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + args[0].toLowerCase() + " <amount>");
                return true;
            }

            Integer amount = JavaUtils.tryParseInt(args[1]);

            if (amount == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a number.");
                return true;
            }
            if (amount < 0) {
                sender.sendMessage(ChatColor.RED + "You cannot set Spawner uses to an amount less than " + 0 + '.');
                return true;
            }
            if (amount > 1) {
                sender.sendMessage(ChatColor.RED + "Wrench have maximum Spawner uses of " + 1 + '.');
                return true;
            }

            Wrench wrench = crowbarOptional.get();
            wrench.setSpawnerUses(amount);
            player.setItemInHand(wrench.getItemIfPresent());
            sender.sendMessage(ChatColor.YELLOW + "Set Spawner uses of held Wrench to " + amount + '.');
        }
        else if (args[0].equalsIgnoreCase("setendframes")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + args[0].toLowerCase() + " <amount>");
                return true;
            }

            Integer amount = JavaUtils.tryParseInt(args[1]);

            if (amount == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a number.");
                return true;
            }
            if (amount < 0) {
                sender.sendMessage(ChatColor.RED + "You cannot set End Frame uses to an amount less than " + 0 + '.');
                return true;
            }
            if (amount > 6) {
                sender.sendMessage(ChatColor.RED + "Wrench have maximum End Frame uses of " + 1 + '.');
                return true;
            }

            Wrench wrench = crowbarOptional.get();
            wrench.setEndFrameUses(amount);
            player.setItemInHand(wrench.getItemIfPresent());
            sender.sendMessage(ChatColor.YELLOW + "Set End Frame uses of held Wrench to " + amount + '.');
        }
        else {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <spawn|setspawners|setendframes>");
        }
        return true;
    }
}
