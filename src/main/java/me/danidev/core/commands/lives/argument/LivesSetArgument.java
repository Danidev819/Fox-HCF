package me.danidev.core.commands.lives.argument;

import me.danidev.core.Main;
import me.danidev.core.utils.BukkitUtils;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.JavaUtils;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.OfflinePlayer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class LivesSetArgument extends CommandArgument {

	public LivesSetArgument() {
		super("set", "Set the lives of a player");
		this.permission = "fhcf.command.lives.argument." + this.getName();
	}

	@Override
	public String getUsage(String label) {
		return "/" + label + ' ' + this.getName() + " <player> <amount>";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 3) {
			sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
			return true;
		}

		Integer amount = JavaUtils.tryParseInt(args[2]);

		if (amount == null) {
			sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a number.");
			return true;
		}

		OfflinePlayer target = BukkitUtils.offlinePlayerWithNameOrUUID(args[1]);

		if (!target.hasPlayedBefore() && !target.isOnline()) {
			sender.sendMessage(CC.translate("&cPlayer not found."));
			return true;
		}

		Main.get().getLivesManager().setLives(target.getUniqueId(), amount);
		sender.sendMessage(ChatColor.YELLOW + target.getName() + " now has " + ChatColor.GOLD + amount
				+ ChatColor.YELLOW + ((amount == 1) ? " life" : " lives") + '.');
		return true;
	}
}
