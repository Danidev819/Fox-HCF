package me.danidev.core.commands.inventory;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.item.ItemBuilder;

import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SkullCommand implements CommandExecutor {
	
	public SkullCommand(Main plugin) {
		plugin.getCommand("skull").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) return true;

		Player player = (Player) sender;

		if (args.length < 1) {
			sender.sendMessage(CC.translate("&cUsage: /" + label + " <player>"));
			return true;
		}

		player.getInventory().addItem(getSkull(args[0]));
		return true;
	}

	private ItemStack getSkull(String owner) {
		return new ItemBuilder(Material.SKULL_ITEM)
				.data(3)
				.owner(owner)
				.build();
	}
}
