package me.danidev.core.commands.inventory;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.JavaUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveCommand implements CommandExecutor {
	
    public GiveCommand(Main plugin) {
    	plugin.getCommand("give").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player> <item> <amount>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(CC.translate("&cPlayer not found."));
            return true;
        }

        ItemStack itemStack = Main.get().getItemDb().getItem(args[1]);

        if (itemStack == null) {
            sender.sendMessage(CC.translate("Item or ID not found."));
            return true;
        }

        Integer amount = JavaUtils.tryParseInt(args[2]);

        if (amount == null) {
            sender.sendMessage(CC.translate("&c'" + args[2] + "' is not a number."));
            return true;
        }
        if (amount <= 0) {
            sender.sendMessage(CC.translate("&cItem amount must be positive."));
            return true;
        }

        itemStack.setAmount(amount);
        target.getInventory().addItem(itemStack);
        return true;
    }
}
