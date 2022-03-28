package me.danidev.core.commands.inventory;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.JavaUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ItemCommand implements CommandExecutor {
	
	public ItemCommand(Main plugin) {
		plugin.getCommand("item").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) return true;

		Player player = (Player) sender;

		if (args.length < 2) {
			player.sendMessage(CC.translate("&cUsage: /" + label + " <item> <amount>"));
			return true;
		}

		ItemStack itemStack = Main.get().getItemDb().getItem(args[0]);

		if (itemStack == null) {
			player.sendMessage(CC.translate("Item or ID not found."));
			return true;
		}

		Integer amount = JavaUtils.tryParseInt(args[1]);

		if (amount == null) {
			player.sendMessage(CC.translate("&c'" + args[1] + "' is not a number."));
			return true;
		}
		if (amount <= 0) {
			player.sendMessage(CC.translate("&cItem amount must be positive."));
			return true;
		}

		itemStack.setAmount(amount);
		player.getInventory().addItem(itemStack);
		return true;
	}
}
