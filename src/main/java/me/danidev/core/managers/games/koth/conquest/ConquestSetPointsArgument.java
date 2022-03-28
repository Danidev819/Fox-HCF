package me.danidev.core.managers.games.koth.conquest;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.JavaUtils;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.faction.type.PlayerFaction;
import me.danidev.core.managers.games.koth.EventType;
import me.danidev.core.managers.games.koth.tracker.ConquestTracker;

public class ConquestSetPointsArgument extends CommandArgument {

	public ConquestSetPointsArgument() {
		super("setpoints", "Sets the points of a faction in the Conquest event");
		this.permission = "fhcf.command.conquest.argument.setpoints";
	}

	public String getUsage(String label) {
		return "/" + label + ' ' + this.getName() + " <factionName> <amount>";
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 3) {
			sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
			return true;
		}
		Faction faction = Main.get().getFactionManager().getFaction(args[1]);

		if (!(faction instanceof PlayerFaction)) {
			sender.sendMessage(ChatColor.RED + "Faction " + args[1] + " is either not found or is not a player faction.");
			return true;
		}

		Integer amount = JavaUtils.tryParseInt(args[2]);

		if (amount == null) {
			sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a number.");
			return true;
		}
		if (amount > 300) {
			sender.sendMessage(ChatColor.RED + "Maximum points for Conquest is " + 100 + '.');
			return true;
		}

		PlayerFaction playerFaction = (PlayerFaction) faction;
		((ConquestTracker) EventType.CONQUEST.getEventTracker()).setPoints(playerFaction, amount);
		sender.sendMessage(CC.translate("&eSet the points of faction &9" + playerFaction.getName() + " &eto &c" + amount + "&e."));
		return true;
	}
}
