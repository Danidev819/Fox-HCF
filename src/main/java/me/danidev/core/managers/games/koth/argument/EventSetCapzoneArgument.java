package me.danidev.core.managers.games.koth.argument;

import java.util.Collections;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.List;

import me.danidev.core.Main;
import me.danidev.core.managers.games.citadel.CitadelFaction;
import me.danidev.core.managers.games.citadel.CitadelTracker;
import me.danidev.core.managers.games.koth.faction.CapturableFaction;
import me.danidev.core.managers.games.koth.faction.ConquestFaction;
import me.danidev.core.managers.games.koth.faction.EventFaction;
import me.danidev.core.managers.games.koth.faction.KothFaction;
import me.danidev.core.managers.games.koth.tracker.ConquestTracker;
import me.danidev.core.managers.games.koth.tracker.KothTracker;
import me.danidev.core.utils.command.CommandArgument;
import me.danidev.core.utils.cuboid.Cuboid;
import me.danidev.core.managers.games.koth.CaptureZone;
import org.bukkit.World;
import java.util.Set;

import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import me.danidev.core.managers.faction.FactionManager;
import me.danidev.core.managers.faction.claim.Claim;
import me.danidev.core.managers.faction.type.Faction;
import org.apache.commons.lang3.StringUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class EventSetCapzoneArgument extends CommandArgument {
	
    private final Main plugin;
    
    public EventSetCapzoneArgument(final Main plugin) {
        super("setcapzone", "Sets the capture zone of an event");
        this.plugin = plugin;
        this.permission = "fhcf.commands.event.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return "/" + label + ' ' + this.getName() + " <eventName>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can set KOTH arena capture points");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
            return true;
        }
        final WorldEditPlugin worldEdit = this.plugin.getWorldEdit();
        if (worldEdit == null) {
            sender.sendMessage(ChatColor.RED + "WorldEdit must be installed to set KOTH capture points.");
            return true;
        }
        final Selection selection = worldEdit.getSelection((Player)sender);
        if (selection == null) {
            sender.sendMessage(ChatColor.RED + "You must make a WorldEdit selection to do this.");
            return true;
        }
        if (selection.getWidth() < 2 || selection.getLength() < 2) {
            sender.sendMessage(ChatColor.RED + "Capture zones must be at least " + 2 + 'x' + 2 + '.');
            return true;
        }
        final Faction faction = this.plugin.getFactionManager().getFaction(args[1]);
        if (!(faction instanceof CapturableFaction)) {
            sender.sendMessage(ChatColor.RED + "There is not a capturable faction named '" + args[1] + "'.");
            return true;
        }
        final CapturableFaction capturableFaction = (CapturableFaction)faction;
        final Set<Claim> claims = capturableFaction.getClaims();
        if (claims.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Capture zones can only be inside the event claim.");
            return true;
        }
        final Claim claim = new Claim(faction, selection.getMinimumPoint(), selection.getMaximumPoint());
        final World world = claim.getWorld();
        final int minimumX = claim.getMinimumX();
        final int maximumX = claim.getMaximumX();
        final int minimumZ = claim.getMinimumZ();
        final int maximumZ = claim.getMaximumZ();
        final FactionManager factionManager = this.plugin.getFactionManager();
        for (int x = minimumX; x <= maximumX; ++x) {
            for (int z = minimumZ; z <= maximumZ; ++z) {
                final Faction factionAt = factionManager.getFactionAt(world, x, z);
                if (!factionAt.equals(capturableFaction)) {
                    sender.sendMessage(ChatColor.RED + "Capture zones can only be inside the event claim.");
                    return true;
                }
            }
        }
        CaptureZone captureZone;
        if (capturableFaction instanceof ConquestFaction) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + this.getName() + ' ' + faction.getName() + " <red|blue|green|yellow>");
                return true;
            }
            final ConquestFaction conquestFaction = (ConquestFaction)capturableFaction;
            final ConquestFaction.ConquestZone conquestZone = ConquestFaction.ConquestZone.getByName(args[2]);
            if (conquestZone == null) {
                sender.sendMessage(ChatColor.RED + "There is no conquest zone named '" + args[2] + "'.");
                sender.sendMessage(ChatColor.RED + "Did you mean?: " + StringUtils.join(ConquestFaction.ConquestZone.getNames(), ", "));
                return true;
            }
            captureZone = new CaptureZone(conquestZone.getName(), conquestZone.getColor().toString(), (Cuboid)claim, ConquestTracker.DEFAULT_CAP_MILLIS);
            conquestFaction.setZone(conquestZone, captureZone);
        }
        else if (capturableFaction instanceof KothFaction) {
            captureZone = new CaptureZone(capturableFaction.getName(), (Cuboid)claim, KothTracker.DEFAULT_CAP_MILLIS);
            ((KothFaction)capturableFaction).setCaptureZone(captureZone);
        }
        else {
            if (!(capturableFaction instanceof CitadelFaction)) {
                sender.sendMessage(ChatColor.RED + "Unexpected error");
                return false;
            }
            captureZone = new CaptureZone(capturableFaction.getName(), (Cuboid)claim, CitadelTracker.DEFAULT_CAP_MILLIS);
            ((CitadelFaction)capturableFaction).setCaptureZone(captureZone);
        }
        sender.sendMessage(ChatColor.YELLOW + "Set capture zone " + captureZone.getDisplayName() + ChatColor.YELLOW + " for faction " + faction.getName() + ChatColor.YELLOW + '.');
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        switch (args.length) {
            case 2: {
                return this.plugin.getFactionManager().getFactions().stream().filter(faction -> faction instanceof EventFaction).map(Faction::getName).collect(Collectors.toList());
            }
            case 3: {
                final Faction faction2 = this.plugin.getFactionManager().getFaction(args[1]);
                if (faction2 instanceof ConquestFaction) {
                    final ConquestFaction.ConquestZone[] zones = ConquestFaction.ConquestZone.values();
                    final ArrayList<String> results = new ArrayList<String>(zones.length);
                    ConquestFaction.ConquestZone[] array;
                    for (int length = (array = zones).length, i = 0; i < length; ++i) {
                        final ConquestFaction.ConquestZone zone = array[i];
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
