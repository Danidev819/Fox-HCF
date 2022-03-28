package me.danidev.core.managers.games.koth.argument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.danidev.core.Main;
import me.danidev.core.managers.games.citadel.CitadelFaction;
import me.danidev.core.managers.games.koth.faction.ConquestFaction;
import me.danidev.core.managers.games.koth.faction.KothFaction;
import me.danidev.core.utils.command.CommandArgument;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.games.koth.EventType;
import org.apache.commons.lang3.text.WordUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class EventCreateArgument extends CommandArgument {
	
	private Main plugin;

	public EventCreateArgument(Main plugin) {
		super("create", "Defines a new event");
		this.plugin = plugin;
		this.permission = "fhcf.commands.event.argument." + this.getName();
	}

	public String getUsage(String label) {
		return "/" + label + ' ' + this.getName() + " <eventName> <Conquest|KOTH|CITADEL>";
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 3) {
			sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: "
					+ ChatColor.AQUA + this.getUsage(label));
			return true;
		}

		Faction faction = this.plugin.getFactionManager().getFaction(args[1]);

		if (faction != null) {
			sender.sendMessage(ChatColor.RED + "There is already a faction named " + args[1] + '.');
			return true;
		}
		String upperCase3;
		String upperCase2 = upperCase3 = args[2].toUpperCase();
		Label_0280: {
			String s;
			switch (s = upperCase2) {
			case "KOTH": {
				faction = new KothFaction(args[1]);
				break Label_0280;
			}
			case "CONQUEST": {
				faction = new ConquestFaction(args[1]);
				break Label_0280;
			}
			case "CITADEL": {
				faction = new CitadelFaction(args[1]);
				break Label_0280;
			}
			default:
				break;
			}
			sender.sendMessage(this.getUsage(label));
			return true;
		}
		this.plugin.getFactionManager().createFaction(faction, sender);
		sender.sendMessage(
				ChatColor.YELLOW + "Created event faction " + ChatColor.WHITE + faction.getDisplayName(sender)
						+ ChatColor.YELLOW + " with others " + WordUtils.capitalizeFully(args[2]) + '.');
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label,
			String[] args) {
		if (args.length != 3) {
			return Collections.emptyList();
		}
		EventType[] eventTypes = EventType.values();
		ArrayList<String> results = new ArrayList<String>(eventTypes.length);
		EventType[] array;
		for (int length = (array = eventTypes).length, i = 0; i < length; ++i) {
			EventType eventType = array[i];
			results.add(eventType.name());
		}
		return results;
	}
}
