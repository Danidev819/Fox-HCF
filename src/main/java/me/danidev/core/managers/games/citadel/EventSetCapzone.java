package me.danidev.core.managers.games.citadel;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

import me.danidev.core.Main;
import me.danidev.core.managers.faction.FactionManager;
import me.danidev.core.managers.faction.claim.Claim;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.games.koth.CaptureZone;
import me.danidev.core.managers.games.koth.faction.CapturableFaction;
import me.danidev.core.managers.games.koth.faction.ConquestFaction;
import me.danidev.core.managers.games.koth.faction.EventFaction;
import me.danidev.core.managers.games.koth.tracker.ConquestTracker;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.World;
import java.util.Collection;

import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import org.apache.commons.lang3.StringUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class EventSetCapzone extends CommandArgument {

	private final Main plugin;

	public EventSetCapzone(Main plugin) {
		super("setcitadelzone", "Sets the capture zone of an event");
		this.plugin = plugin;
		this.permission = "fhcf.command.event.argument." + this.getName();
	}

	public String getUsage(String label) {
		return '/' + label + ' ' + this.getName() + " ";
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can set KOTH arena capture points");
			return true;
		}
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: "
					+ ChatColor.AQUA + this.getUsage(label));
			return true;
		}
		WorldEditPlugin worldEdit = this.plugin.getWorldEdit();
		if (worldEdit == null) {
			sender.sendMessage(ChatColor.RED + "WorldEdit must be installed to set KOTH capture points.");
			return true;
		}
		Selection selection = worldEdit.getSelection((Player) sender);
		if (selection == null) {
			sender.sendMessage(ChatColor.RED + "You must make a WorldEdit selection to do this.");
			return true;
		}
		if (selection.getWidth() < 2 || selection.getLength() < 2) {
			sender.sendMessage(ChatColor.RED + "Capture zones must be at least " + 2 + 'x' + 2 + '.');
			return true;
		}
		Faction faction = this.plugin.getFactionManager().getFaction(args[1]);
		if (!(faction instanceof CapturableFaction)) {
			sender.sendMessage(ChatColor.RED + "There is not a capturable faction named '" + args[1] + "'.");
			return true;
		}
		CapturableFaction capturableFaction = (CapturableFaction) faction;
		Collection<Claim> claims = capturableFaction.getClaims();
		if (claims.isEmpty()) {
			sender.sendMessage(ChatColor.RED + "Capture zones can only be inside the event claim.");
			return true;
		}
		Claim claim = new Claim(faction, selection.getMinimumPoint(), selection.getMaximumPoint());
		World world = claim.getWorld();
		int minimumX = claim.getMinimumX();
		int maximumX = claim.getMaximumX();
		int minimumZ = claim.getMinimumZ();
		int maximumZ = claim.getMaximumZ();
		FactionManager factionManager = this.plugin.getFactionManager();
		for (int x = minimumX; x <= maximumX; ++x) {
			for (int z = minimumZ; z <= maximumZ; ++z) {
				Faction factionAt = factionManager.getFactionAt(world, x, z);
				if (!factionAt.equals(capturableFaction)) {
					sender.sendMessage(ChatColor.RED + "Capture zones can only be inside the event claim.");
					return true;
				}
			}
		}
		CaptureZone captureZone;
		if (capturableFaction instanceof ConquestFaction) {
			if (args.length < 3) {
				sender.sendMessage(
						ChatColor.RED + "Usage: /" + label + ' ' + this.getName() + ' ' + faction.getName() + " ");
				return true;
			}
			ConquestFaction conquestFaction = (ConquestFaction) capturableFaction;
			ConquestFaction.ConquestZone conquestZone = ConquestFaction.ConquestZone.getByName(args[2]);
			if (conquestZone == null) {
				sender.sendMessage(ChatColor.RED + "There is no conquest zone named '" + args[2] + "'.");
				sender.sendMessage(ChatColor.RED + "Did you mean?: "
						+ StringUtils.join(ConquestFaction.ConquestZone.getNames(), ", "));
				return true;
			}
			captureZone = new CaptureZone(conquestZone.getName(), conquestZone.getColor().toString(), claim, ConquestTracker.DEFAULT_CAP_MILLIS);
			conquestFaction.setZone(conquestZone, captureZone);
		}
		else {
			((CitadelFaction) capturableFaction)
					.setCaptureZone(captureZone = new CaptureZone(capturableFaction.getName(), claim,
							CitadelTracker.DEFAULT_CAP_MILLIS));
		}
		sender.sendMessage(ChatColor.YELLOW + "Set capture zone " + captureZone.getDisplayName()
				+ ChatColor.YELLOW + " for faction " + faction.getName()
				+ ChatColor.YELLOW + '.');
		return true;
	}

	@SuppressWarnings("unchecked")
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		switch (args.length) {
		case 2: {
			return (List<String>) this.plugin.getFactionManager().getFactions().stream()
					.filter(faction -> faction instanceof EventFaction);
		}
		case 3: {
			Faction faction2 = this.plugin.getFactionManager().getFaction(args[1]);
			if (faction2 instanceof ConquestFaction) {
				ConquestFaction.ConquestZone[] zones = ConquestFaction.ConquestZone.values();
				List<String> results = new ArrayList<>(zones.length);
				ConquestFaction.ConquestZone[] array;
				for (int length = (array = zones).length, i = 0; i < length; ++i) {
					ConquestFaction.ConquestZone zone = array[i];
					results.add(zone.name());
				}
				return results;
			}
			return Collections.emptyList();
		}
		default: {
			return Collections.emptyList();
		}
		}
	}
}
