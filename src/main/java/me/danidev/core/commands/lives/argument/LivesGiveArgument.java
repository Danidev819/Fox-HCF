package me.danidev.core.commands.lives.argument;

import me.danidev.core.Main;
import me.danidev.core.managers.eotw.EOTWHandler;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.JavaUtils;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class LivesGiveArgument extends CommandArgument {

	public LivesGiveArgument() {
		super("give", "Help someone out by giving them live(s)");
		this.permission = "fhcf.command.lives.argument." + this.getName();
	}

	@Override
	public String getUsage(String label) {
		return "/" + label + " " + this.getName() + " <player> <amount>";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 3) {
			sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
			return true;
		}
		EOTWHandler.EotwRunnable eotwRunnable = Main.get().getEotwHandler().getRunnable();
		if (eotwRunnable != null) {
			sender.sendMessage(CC.translate("&cYou are not use this commands in EOTW."));
			return true;
		}
		Integer amount = JavaUtils.tryParseInt(args[2]);
		if (amount == null) {
			sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a number.");
			return true;
		}
		if (amount <= 0) {
			sender.sendMessage(ChatColor.RED + "The amount of lives must be positive.");
			return true;
		}
		OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
		if (!target.hasPlayedBefore() && !target.isOnline()) {
			sender.sendMessage(CC.translate("&cPlayer not found."));
			return true;
		}
		Player onlineTarget = target.getPlayer();
		if (sender instanceof Player && !sender.hasPermission("fhcf.commands.lives.argument.give.bypass")) {
			Player player = (Player) sender;
			int ownedLives = Main.get().getLivesManager().getLives(player.getUniqueId());
			if (amount > ownedLives) {
				sender.sendMessage(ChatColor.RED + "You tried to give " + target.getName() + ' ' + amount
						+ " lives, but you only have " + ownedLives + '.');
				return true;
			}
			Main.get().getLivesManager().takeLives(player.getUniqueId(), amount);
		}
		int targetLives = Main.get().getLivesManager().getLives(target.getUniqueId());
		Main.get().getLivesManager().addLives(target.getUniqueId(), amount);
		sender.sendMessage(ChatColor.YELLOW + "You have sent " + ChatColor.GOLD + target.getName() + ChatColor.YELLOW
				+ ' ' + amount + ' ' + ((amount == 1) ? "life" : "lives") + '.');
		if (onlineTarget != null) {
			onlineTarget.sendMessage(ChatColor.GOLD + sender.getName() + ChatColor.YELLOW + " has sent you "
					+ ChatColor.GOLD + amount + ' ' + ((amount == 1) ? "life" : "lives") + '.');
		}
		return true;
	}
}
