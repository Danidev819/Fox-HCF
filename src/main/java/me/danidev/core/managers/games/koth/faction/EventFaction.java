package me.danidev.core.managers.games.koth.faction;

import java.util.List;

import me.danidev.core.utils.cuboid.Cuboid;
import me.danidev.core.managers.games.koth.CaptureZone;
import me.danidev.core.managers.games.koth.EventType;
import org.bukkit.Location;

import me.danidev.core.managers.faction.claim.Claim;
import me.danidev.core.managers.faction.type.ClaimableFaction;
import me.danidev.core.managers.faction.type.Faction;

import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

import java.util.Map;

public abstract class EventFaction extends ClaimableFaction {
	public EventFaction(final String name) {
		super(name);
		this.setDeathban(true);
	}

	public EventFaction(final Map<String, Object> map) {
		super(map);
		this.setDeathban(true);
	}

	@Override
	public String getDisplayName(final Faction faction) {
		if (this.getEventType() == EventType.KOTH) {
			return new StringBuilder().append(ChatColor.LIGHT_PURPLE).append(ChatColor.BOLD).append(this.getName())
					.append(" KOTH").toString();
		}
		return new StringBuilder().append(ChatColor.DARK_PURPLE).append(ChatColor.BOLD)
				.append(this.getEventType().getDisplayName()).toString();
	}

	@Override
	public String getDisplayName(final CommandSender sender) {
		if (this.getEventType() == EventType.KOTH) {
			return new StringBuilder().append(ChatColor.LIGHT_PURPLE).append(ChatColor.BOLD).append(this.getName())
					.append(" KOTH").toString();
		}
		return new StringBuilder().append(ChatColor.DARK_PURPLE).append(ChatColor.BOLD)
				.append(this.getEventType().getDisplayName()).toString();
	}

	public String getDisplayName1(final Faction faction) {
		if (this.getEventType() == EventType.CITADEL) {
			return String.valueOf(ChatColor.LIGHT_PURPLE.toString()) + this.getName() + ' '
					+ this.getEventType().getDisplayName();
		}
		return ChatColor.DARK_PURPLE + this.getEventType().getDisplayName();
	}

	public void setClaim(final Cuboid cuboid, final CommandSender sender) {
		this.removeClaims(this.getClaims(), sender);
		final Location min = cuboid.getMinimumPoint();
		min.setY(0.0);
		final Location max = cuboid.getMaximumPoint();
		max.setY(256.0);
		this.addClaim(new Claim(this, min, max), sender);
	}

	public abstract EventType getEventType();

	public abstract List<CaptureZone> getCaptureZones();
}
