package me.danidev.core.managers.games.koth.faction;

import java.util.ArrayList;

import me.danidev.core.utils.BukkitUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList;
import java.util.List;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;

import me.danidev.core.managers.games.koth.CaptureZone;
import me.danidev.core.managers.games.koth.EventType;
import org.bukkit.Location;

import me.danidev.core.managers.faction.claim.Claim;
import me.danidev.core.managers.faction.type.ClaimableFaction;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.EnumMap;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class ConquestFaction extends CapturableFaction implements ConfigurationSerializable {
	private final EnumMap<ConquestZone, CaptureZone> captureZones;

	public ConquestFaction(final String name) {
		super(name);
		this.captureZones = new EnumMap<ConquestZone, CaptureZone>(ConquestZone.class);
	}

	public ConquestFaction(final Map<String, Object> map) {
		super(map);
		this.captureZones = new EnumMap<ConquestZone, CaptureZone>(ConquestZone.class);
		Object object;
		if ((object = map.get("red")) instanceof CaptureZone) {
			this.captureZones.put(ConquestZone.RED, (CaptureZone) object);
		}
		if ((object = map.get("green")) instanceof CaptureZone) {
			this.captureZones.put(ConquestZone.GREEN, (CaptureZone) object);
		}
		if ((object = map.get("blue")) instanceof CaptureZone) {
			this.captureZones.put(ConquestZone.BLUE, (CaptureZone) object);
		}
		if ((object = map.get("yellow")) instanceof CaptureZone) {
			this.captureZones.put(ConquestZone.YELLOW, (CaptureZone) object);
		}
	}

	public Map<String, Object> serialize() {
		final Map<String, Object> map = super.serialize();
		for (final Map.Entry<ConquestZone, CaptureZone> entry : this.captureZones.entrySet()) {
			map.put(entry.getKey().name().toLowerCase(), entry.getValue());
		}
		return map;
	}

	public EventType getEventType() {
		return EventType.CONQUEST;
	}

	public ConquestZone getZone(final CaptureZone captureZone) {
		for (final Map.Entry<ConquestZone, CaptureZone> captureZoneEntry : this.captureZones.entrySet()) {
			if (captureZoneEntry.getValue() == captureZone) {
				return captureZoneEntry.getKey();
			}
		}
		return null;
	}

	public void printDetails(final CommandSender sender) {
		sender.sendMessage(ChatColor.GOLD + BukkitUtils.STRAIGHT_LINE_DEFAULT);
		sender.sendMessage(this.getDisplayName(sender));
		for (final Claim claim : this.claims) {
			final Location location = claim.getCenter();
			sender.sendMessage(ChatColor.YELLOW + "  Location: " + ChatColor.RED + '('
					+ (String) ClaimableFaction.ENVIRONMENT_MAPPINGS.get(location.getWorld().getEnvironment()) + ", "
					+ location.getBlockX() + " | " + location.getBlockZ() + ')');
		}
		sender.sendMessage(ChatColor.GOLD + BukkitUtils.STRAIGHT_LINE_DEFAULT);
	}

	public void setZone(final ConquestZone conquestZone, final CaptureZone captureZone) {
		switch (conquestZone) {
		case RED: {
			this.captureZones.put(ConquestZone.RED, captureZone);
			break;
		}
		case BLUE: {
			this.captureZones.put(ConquestZone.BLUE, captureZone);
			break;
		}
		case GREEN: {
			this.captureZones.put(ConquestZone.GREEN, captureZone);
			break;
		}
		case YELLOW: {
			this.captureZones.put(ConquestZone.YELLOW, captureZone);
			break;
		}
		default: {
			throw new AssertionError("Unsupported operation");
		}
		}
	}

	public CaptureZone getRed() {
		return this.captureZones.get(ConquestZone.RED);
	}

	public CaptureZone getGreen() {
		return this.captureZones.get(ConquestZone.GREEN);
	}

	public CaptureZone getBlue() {
		return this.captureZones.get(ConquestZone.BLUE);
	}

	public CaptureZone getYellow() {
		return this.captureZones.get(ConquestZone.YELLOW);
	}

	public Collection<ConquestZone> getConquestZones() {
		return (Collection<ConquestZone>) ImmutableSet.copyOf((Collection) this.captureZones.keySet());
	}

	public List<CaptureZone> getCaptureZones() {
		return (List<CaptureZone>) ImmutableList.copyOf((Collection) this.captureZones.values());
	}

	public enum ConquestZone {
		RED("RED", 0, ChatColor.RED, "Red"), BLUE("BLUE", 1, ChatColor.AQUA, "Blue"),
		YELLOW("YELLOW", 2, ChatColor.YELLOW, "Yellow"), GREEN("GREEN", 3, ChatColor.GREEN, "Green");

		private static final Map<String, ConquestZone> BY_NAME;
		private final String name;
		private final ChatColor color;

		static {
			final ImmutableMap.Builder<String, ConquestZone> builder = ImmutableMap.builder();
			ConquestZone[] values;
			for (int length = (values = values()).length, i = 0; i < length; ++i) {
				final ConquestZone zone = values[i];
				builder.put(zone.name().toUpperCase(), zone);
			}
			BY_NAME = (Map) builder.build();
		}

		private ConquestZone(final String s, final int n, final ChatColor color, final String name) {
			this.color = color;
			this.name = name;
		}

		public static ConquestZone getByName(final String name) {
			return ConquestZone.BY_NAME.get(name.toUpperCase());
		}

		public static Collection<String> getNames() {
			return new ArrayList<String>(ConquestZone.BY_NAME.keySet());
		}

		public ChatColor getColor() {
			return this.color;
		}

		public String getName() {
			return this.name;
		}

		public String getDisplayName() {
			return String.valueOf(this.color.toString()) + this.name;
		}
	}
}
