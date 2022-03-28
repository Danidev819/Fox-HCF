package me.danidev.core.commands.lives.argument;

import me.danidev.core.utils.CC;
import me.danidev.core.utils.service.ConfigurationService;
import me.danidev.core.utils.JavaUtils;
import me.danidev.core.utils.command.CommandArgument;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class LivesSetDeathbanTimeArgument extends CommandArgument {

	public LivesSetDeathbanTimeArgument() {
		super("setdeathbantime", "Sets the base deathban time");
		this.permission = "fhcf.command.lives.argument." + this.getName();
	}

	@Override
	public String getUsage(String label) {
		return "/" + label + ' ' + this.getName() + " <time>";
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
			return true;
		}

		long duration = JavaUtils.parse(args[1]);

		if (duration == -1L) {
			sender.sendMessage(ChatColor.RED + "Invalid duration, use the correct format: 10m 1s");
			return true;
		}

		ConfigurationService.DEATHBAN_DURATION = duration;
		sender.sendMessage(CC.translate("&eBase death-ban time set to " + DurationFormatUtils.formatDurationWords(duration, true, true) + "."));
		return true;
	}
}
