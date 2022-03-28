package me.danidev.core.commands.inventory;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.JavaUtils;
import org.bukkit.inventory.ItemStack;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MoreCommand implements CommandExecutor {
	
	public MoreCommand(Main plugin) {
		plugin.getCommand("more").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) return true;

		Player player = (Player) sender;

		if (args.length < 1) {
			player.sendMessage(CC.translate("&cUsage: /" + label + " <amount>"));
			return true;
		}

		ItemStack itemStack = player.getItemInHand();

		if (itemStack == null || itemStack.getType() == Material.AIR) {
			sender.sendMessage(CC.translate("&cYou need hold any item."));
			return true;
		}

		Integer amount = JavaUtils.tryParseInt(args[0]);

		if (amount == null) {
			sender.sendMessage(CC.translate("&c'" + args[0] + "' is not a number."));
			return true;
		}
		if (amount <= 0) {
			sender.sendMessage(CC.translate("&cItem amount must be positive."));
			return true;
		}

		itemStack.setAmount(amount);
		return true;
	}
}
