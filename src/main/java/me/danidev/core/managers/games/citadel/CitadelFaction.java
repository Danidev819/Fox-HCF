package me.danidev.core.managers.games.citadel;

import org.bukkit.entity.Player;
import org.bukkit.Location;

import me.danidev.core.Main;
import me.danidev.core.managers.faction.claim.Claim;
import me.danidev.core.managers.faction.type.ClaimableFaction;
import me.danidev.core.managers.faction.type.PlayerFaction;
import me.danidev.core.managers.games.koth.CaptureZone;
import me.danidev.core.managers.games.koth.EventType;
import me.danidev.core.managers.games.koth.faction.CapturableFaction;
import me.danidev.core.utils.BukkitUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class CitadelFaction extends CapturableFaction implements ConfigurationSerializable {
	
	private CaptureZone captureZone;

	public CitadelFaction(String name) {
		super(name);
	}

	public CitadelFaction(Map<String, Object> map) {
		super(map);
		this.captureZone = (CaptureZone) map.get("captureZone");
	}

	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		map.put("captureZone", this.captureZone);
		return map;
	}

	public List<CaptureZone> getCaptureZones() {
		return ((this.captureZone == null) ? ImmutableList.of() : ImmutableList.of(this.captureZone));
	}

	public EventType getEventType() {
		return EventType.CITADEL;
	}
	
	public void printDetails(CommandSender sender) {
		sender.sendMessage(ChatColor.GOLD + BukkitUtils.STRAIGHT_LINE_DEFAULT);
		sender.sendMessage(this.getDisplayName(sender));
		for (Claim claim : this.claims) {
			Location location = claim.getCenter();
			sender.sendMessage(ChatColor.YELLOW + "  Location: " + ChatColor.RED + '('
					+ ClaimableFaction.ENVIRONMENT_MAPPINGS.get(location.getWorld().getEnvironment()) + ", "
					+ location.getBlockX() + " | " + location.getBlockZ() + ')');
		}
		if (this.captureZone != null) {
			long remainingCaptureMillis = this.captureZone.getRemainingCaptureMillis();
			long defaultCaptureMillis = this.captureZone.getDefaultCaptureMillis();
			if (remainingCaptureMillis > 0L && remainingCaptureMillis != defaultCaptureMillis) {
				sender.sendMessage(ChatColor.YELLOW + "  Remaining Time: " + ChatColor.RED
						+ DurationFormatUtils.formatDurationWords(remainingCaptureMillis, true, true));
			}
			sender.sendMessage(
					ChatColor.YELLOW + "  Capture Delay: " + ChatColor.RED + this.captureZone.getDefaultCaptureWords());
			if (this.captureZone.getCappingPlayer() != null && sender.hasPermission("fhcf.citadel.capper")) {
				Player capping = this.captureZone.getCappingPlayer();
				PlayerFaction playerFaction = Main.get().getFactionManager().getPlayerFaction(capping.getUniqueId());
				String factionTag = "[" + ((playerFaction == null) ? "*" : playerFaction.getName()) + "]";
				sender.sendMessage(ChatColor.YELLOW + "  Current Capper: " + ChatColor.RED + capping.getName()
						+ ChatColor.GOLD + factionTag);
			}
		}
		sender.sendMessage(ChatColor.GOLD + BukkitUtils.STRAIGHT_LINE_DEFAULT);
	}

	public CaptureZone getCaptureZone() {
		return this.captureZone;
	}

	public void setCaptureZone(CaptureZone captureZone) {
		this.captureZone = captureZone;
	}
}
