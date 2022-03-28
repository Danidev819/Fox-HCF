package me.danidev.core.commands.teleport;

import me.danidev.core.Main;
import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BackCommand implements CommandExecutor {

	public BackCommand(Main plugin) {
		plugin.getCommand("back").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) return true;

		Player player = (Player) sender;

		Location location = Main.get().getUserManager().getBaseUser(player.getUniqueId()).getBackLocation();

		if (location == null) {
			sender.sendMessage(ChatColor.RED + player.getName() + " doesn't have a back location.");
			return true;
		}

		player.teleport(location);
		sender.sendMessage(ChatColor.YELLOW + "Teleported to back location of " + player.getName() + ".");
		return true;
	}
}
