package me.danidev.core.managers.faction.type;

import me.danidev.core.Main;
import me.danidev.core.utils.BukkitUtils;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.service.ConfigurationService;

import java.util.List;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import java.util.Map;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class RoadFaction extends ClaimableFaction implements ConfigurationSerializable {

	public RoadFaction(String name) {
		super(name);
	}

	public RoadFaction(Map<String, Object> map) {
		super(map);
	}

	public String getDisplayName(CommandSender sender) {
		return ConfigurationService.ROAD_COLOR + this.getName().replace("st", "st ").replace("th", "th ");
	}

	@Override
	public void printDetails(CommandSender sender) {
		List<String> toSend = new ArrayList<>();

		for (String string : Main.get().getMainConfig().getStringList("FACTION_GENERAL.SHOW.ROAD_FACTION")) {
			string = string.replace("%LINE%", BukkitUtils.STRAIGHT_LINE_DEFAULT);
			string = string.replace("%FACTION%", this.getDisplayName(sender));
			toSend.add(string);
		}
		for (String message : toSend) {
			sender.sendMessage(CC.translate(message));
		}
	}

	public static class NorthRoadFaction extends RoadFaction implements ConfigurationSerializable {
		public NorthRoadFaction() {
			super("NorthRoad");
			for (World world : Bukkit.getWorlds()) {
				World.Environment environment = world.getEnvironment();
				if (environment != World.Environment.THE_END) {
					int borderSize = ConfigurationService.BORDER_SIZES.get(environment);
					double n = ConfigurationService.SPAWN_RADIUS.get(environment) + 1.0;
				}
			}
		}

		public NorthRoadFaction(Map<String, Object> map) {
			super(map);
		}
	}

	public static class EastRoadFaction extends RoadFaction implements ConfigurationSerializable {
		public EastRoadFaction() {
			super("EastRoad");
			for (World world : Bukkit.getWorlds()) {
				World.Environment environment = world.getEnvironment();
				if (environment != World.Environment.THE_END) {
					int borderSize = ConfigurationService.BORDER_SIZES.get(environment);
					double n = ConfigurationService.SPAWN_RADIUS.get(environment) + 1.0;
				}
			}
		}

		public EastRoadFaction(Map<String, Object> map) {
			super(map);
		}
	}

	public static class SouthRoadFaction extends RoadFaction implements ConfigurationSerializable {
		public SouthRoadFaction() {
			super("SouthRoad");
			for (World world : Bukkit.getWorlds()) {
				World.Environment environment = world.getEnvironment();
				if (environment != World.Environment.THE_END) {
					int borderSize = ConfigurationService.BORDER_SIZES.get(environment);
					double n = ConfigurationService.SPAWN_RADIUS.get(environment) + 1.0;
				}
			}
		}

		public SouthRoadFaction(Map<String, Object> map) {
			super(map);
		}
	}

	public static class WestRoadFaction extends RoadFaction implements ConfigurationSerializable {
		public WestRoadFaction() {
			super("WestRoad");
			for (World world : Bukkit.getWorlds()) {
				World.Environment environment = world.getEnvironment();
				if (environment != World.Environment.THE_END) {
					int borderSize = ConfigurationService.BORDER_SIZES.get(environment);
					double n = ConfigurationService.SPAWN_RADIUS.get(environment) + 1.0;
				}
			}
		}

		public WestRoadFaction(Map<String, Object> map) {
			super(map);
		}
	}
}
