package me.danidev.core.utils.command;

import me.danidev.core.utils.BukkitUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.*;

public class CommandWrapper implements CommandExecutor, TabCompleter {

	private final Collection<CommandArgument> arguments;

	public CommandWrapper(final Collection<CommandArgument> arguments) {
		this.arguments = arguments;
	}

	public static void printUsage(final CommandSender sender, final String label,
			final Collection<CommandArgument> arguments) {
		sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
		sender.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + WordUtils.capitalizeFully(label) + " Help");
		int amount = 0;
		for (final CommandArgument argument : arguments) {
			final String permission = argument.getPermission();
			if (permission != null && !sender.hasPermission(permission)) {
				continue;
			}
			sender.sendMessage(ChatColor.YELLOW + argument.getUsage(label) + ChatColor.GRAY + " - " + ChatColor.WHITE
					+ argument.getDescription());
			++amount;
		}
		if (amount > 0) {
			sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
		}
	}

	public static CommandArgument matchArgument(final String id, final CommandSender sender,
			final Collection<CommandArgument> arguments) {
		for (final CommandArgument argument : arguments) {
			final String permission = argument.getPermission();
			if (permission == null || sender.hasPermission(permission)) {
				if (!argument.getName().equalsIgnoreCase(id) && !Arrays.asList(argument.getAliases()).contains(id)) {
					continue;
				}
				return argument;
			}
		}
		return null;
	}

	public static List<String> getAccessibleArgumentNames(final CommandSender sender,
			final Collection<CommandArgument> arguments) {
		final ArrayList<String> results = new ArrayList<String>();
		for (final CommandArgument argument : arguments) {
			final String permission = argument.getPermission();
			if (permission != null && !sender.hasPermission(permission)) {
				continue;
			}
			results.add(argument.getName());
		}
		return results;
	}

	public boolean onCommand(final CommandSender sender, final Command command, final String label,
			final String[] args) {
		if (args.length < 1) {
			printUsage(sender, label, this.arguments);
			return true;
		}
		final CommandArgument argument = matchArgument(args[0], sender, this.arguments);
		if (argument == null) {
			printUsage(sender, label, this.arguments);
			return true;
		}
		return argument.onCommand(sender, command, label, args);
	}

	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label,
			final String[] args) {
		if (!(sender instanceof Player)) {
			return Collections.emptyList();
		}
		List<String> results;
		if (args.length == 1) {
			results = getAccessibleArgumentNames(sender, this.arguments);
		} else {
			final CommandArgument argument = matchArgument(args[0], sender, this.arguments);
			if (argument == null) {
				return Collections.emptyList();
			}
			results = argument.onTabComplete(sender, command, label, args);
			if (results == null) {
				return null;
			}
		}
		return BukkitUtils.getCompletions(args, results);
	}

	@SuppressWarnings("serial")
	public static class ArgumentComparator implements Comparator<CommandArgument>, Serializable {
		public int compare(final CommandArgument primaryArgument, final CommandArgument secondaryArgument) {
			return secondaryArgument.getName().compareTo(primaryArgument.getName());
		}
	}
}
