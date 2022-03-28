package me.danidev.core.managers.games.koth.faction;

import me.danidev.core.Main;
import me.danidev.core.managers.games.koth.EventType;
import lombok.Getter;
import me.danidev.core.managers.games.koth.CaptureZone;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import me.danidev.core.managers.faction.claim.Claim;
import me.danidev.core.managers.faction.type.PlayerFaction;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class KothFaction extends CapturableFaction implements ConfigurationSerializable {
	
    private CaptureZone captureZone;

    public KothFaction(final String name) {
        super(name);
        this.setDeathban(true);
    }
    
    public KothFaction(final Map<String, Object> map) {
        super(map);
        this.setDeathban(true);
        this.captureZone = (CaptureZone) map.get("captureZone");
    }

    public Map<String, Object> serialize() {
        final Map<String, Object> map = super.serialize();
        map.put("captureZone", this.captureZone);
        return map;
    }
    
    @SuppressWarnings("unchecked")
	public List<CaptureZone> getCaptureZones() {
        return (List<CaptureZone>)((this.captureZone == null) ? ImmutableList.of() : ImmutableList.of(this.captureZone));
    }
    
    public EventType getEventType() {
        return EventType.KOTH;
    }
    
	public void printDetails(final CommandSender sender) {
        sender.sendMessage(String.valueOf(ChatColor.GRAY.toString()) + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
        sender.sendMessage(this.getDisplayName(sender));
        for (final Claim claim : this.claims) {
            Location location = claim.getCenter();
            sender.sendMessage(ChatColor.YELLOW + "  Location: " + ChatColor.RED + '(' + (String)KothFaction.ENVIRONMENT_MAPPINGS.get(location.getWorld().getEnvironment()) + ", " + location.getBlockX() + " | " + location.getBlockZ() + ')');
        }
        if (this.captureZone != null) {
            final long remainingCaptureMillis = this.captureZone.getRemainingCaptureMillis();
            final long defaultCaptureMillis = this.captureZone.getDefaultCaptureMillis();
            if (remainingCaptureMillis > 0L && remainingCaptureMillis != defaultCaptureMillis) {
                sender.sendMessage(ChatColor.YELLOW + "  Remaining Time: " + ChatColor.RED + DurationFormatUtils.formatDurationWords(remainingCaptureMillis, true, true));
            }
            sender.sendMessage(ChatColor.YELLOW + "  Capture Delay: " + ChatColor.RED + this.captureZone.getDefaultCaptureWords());
            if (this.captureZone.getCappingPlayer() != null && sender.hasPermission("fhcf.koth.checkcapper")) {
                final Player capping = this.captureZone.getCappingPlayer();
                final PlayerFaction playerFaction;
                final String factionTag = "[" + (((playerFaction = Main.get().getFactionManager().getPlayerFaction(capping)) == null) ? "*" : playerFaction.getName()) + "]";
                sender.sendMessage(ChatColor.YELLOW + "  Current Capper: " + ChatColor.RED + capping.getName() + ChatColor.GOLD + factionTag);
            }
        }
        sender.sendMessage(String.valueOf(ChatColor.GRAY.toString()) + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
    }
    
    public CaptureZone getCaptureZone() {
        return this.captureZone;
    }
    
    public void setCaptureZone(final CaptureZone captureZone) {
        this.captureZone = captureZone;
    }
}
