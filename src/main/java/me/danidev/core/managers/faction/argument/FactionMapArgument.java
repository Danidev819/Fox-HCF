package me.danidev.core.managers.faction.argument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import me.danidev.core.Main;
import me.danidev.core.managers.user.FactionUser;
import me.danidev.core.utils.GuavaCompat;
import me.danidev.core.utils.command.CommandArgument;
import me.danidev.core.utils.visualise.VisualType;
import me.danidev.core.managers.faction.LandMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class FactionMapArgument extends CommandArgument {
	
    private final Main plugin;
    
    public FactionMapArgument(final Main plugin) {
        super("map", "See all land claims around your faction.");
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return "/" + label + ' ' + this.getName() + " [factionName]";
    }
    
    @SuppressWarnings("unused")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This commands is only executable by players.");
            return true;
        }
        final Player player = (Player)sender;
        final UUID uuid = player.getUniqueId();
        final FactionUser factionUser = this.plugin.getUserManager().getUser(uuid);
        VisualType visualType;
        if (args.length <= 1) {
            visualType = VisualType.CLAIM_MAP;
        }
        else {
            visualType = (VisualType) GuavaCompat.getIfPresent(VisualType.class, args[1]).orNull();
            if (visualType == null) {
                player.sendMessage(ChatColor.RED + "Visual others " + args[1] + " not found.");
                return true;
            }
        }
        final boolean newShowingMap;
        final boolean bl = newShowingMap = !factionUser.isShowClaimMap();
        if (newShowingMap) {
            if (!LandMap.updateMap(player, this.plugin, visualType, true)) {
                return true;
            }
        }
        else {
            this.plugin.getVisualiseHandler().clearVisualBlocks(player, visualType, null);
            sender.sendMessage(ChatColor.RED + "Claim pillars are no longer shown.");
        }
        factionUser.setShowClaimMap(newShowingMap);
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        final VisualType[] values = VisualType.values();
        final ArrayList<String> results = new ArrayList<String>(values.length);
        VisualType[] array;
        for (int length = (array = values).length, i = 0; i < length; ++i) {
            final VisualType visualType = array[i];
            results.add(visualType.name());
        }
        return results;
    }
}
