package me.danidev.core.commands;

import me.danidev.core.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.danidev.core.managers.timer.type.LogoutTimer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class LogoutCommand implements CommandExecutor {

	public LogoutCommand(Main plugin) {
		plugin.getCommand("logout").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,String[] args) {
		if (!(sender instanceof Player)) return true;

		Player player = (Player) sender;

		LogoutTimer logoutTimer = Main.get().getTimerManager().getLogoutTimer();

		if (!logoutTimer.setCooldown(player, player.getUniqueId())) {
			sender.sendMessage(ChatColor.RED + "Your Logout timer is already active.");
			return true;
		}

		sender.sendMessage(ChatColor.RED + "Your Logout timer has started.");
		return true;
	}
}
