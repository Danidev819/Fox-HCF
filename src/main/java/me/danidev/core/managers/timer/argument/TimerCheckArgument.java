package me.danidev.core.managers.timer.argument;

import java.util.Collections;
import java.util.List;

import me.danidev.core.Main;
import me.danidev.core.utils.UUIDFetcher;
import me.danidev.core.utils.command.CommandArgument;
import java.util.UUID;

import me.danidev.core.managers.timer.PlayerTimer;
import me.danidev.core.managers.timer.Timer;
import org.apache.commons.lang3.time.DurationFormatUtils;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class TimerCheckArgument extends CommandArgument {

	public TimerCheckArgument() {
		super("check", "Check remaining timer time");
		this.permission = "fhcf.command.timer.argument." + this.getName();
	}

	public String getUsage(String label) {
		return "/" + label + ' ' + this.getName() + " <timerName> <playerName>";
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 3) {
			sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
			return true;
		}
		PlayerTimer temporaryTimer = null;
		for (Timer timer : Main.get().getTimerManager().getTimers()) {
			if (timer instanceof PlayerTimer) {
				if (!timer.getName().equalsIgnoreCase(args[1])) {
					continue;
				}
				temporaryTimer = (PlayerTimer) timer;
				break;
			}
		}
		if (temporaryTimer == null) {
			sender.sendMessage(ChatColor.RED + "Timer '" + args[1] + "' not found.");
			return true;
		}
		PlayerTimer playerTimer = temporaryTimer;
		new BukkitRunnable() {
			public void run() {
				UUID uuid;
				try {
					uuid = UUIDFetcher.getUUIDOf(args[2]);
				} catch (Exception ex) {
					sender.sendMessage(
							ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[2] + ChatColor.GOLD + "' not found.");
					return;
				}
				if (uuid == null) {
					sender.sendMessage(
							ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[2] + ChatColor.GOLD + "' not found.");
					return;
				}
				long remaining = playerTimer.getRemaining(uuid);
				sender.sendMessage(ChatColor.YELLOW + args[2] + " has timer " + playerTimer.getName() + ChatColor.YELLOW
						+ " for another " + DurationFormatUtils.formatDurationWords(remaining, true, true));
			}
		}.runTaskAsynchronously(Main.get());
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return (args.length == 2) ? null : Collections.emptyList();
	}
}
