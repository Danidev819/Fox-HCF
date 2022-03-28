package me.danidev.core.managers.faction.argument;

import me.danidev.core.Main;
import me.danidev.core.utils.service.ConfigurationService;
import me.danidev.core.utils.command.CommandArgument;
import com.lunarclient.bukkitapi.LunarClientAPI;
import me.danidev.core.managers.faction.FactionMember;
import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.danidev.core.managers.faction.claim.Claim;
import me.danidev.core.managers.faction.struct.Role;
import me.danidev.core.managers.faction.type.PlayerFaction;

public class FactionSetHomeArgument extends CommandArgument {
	
    private final Main plugin;

    public FactionSetHomeArgument(final Main plugin) {
        super("sethome", "Set the home location of the faction.", new String[]{"sethq"});
        this.plugin = plugin;
    }

    public String getUsage(final String label) {
        return "/" + label + ' ' + this.getName();
    }

    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This commands is only executable by players.");
            return true;
        }
        final Player player = (Player)sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        final FactionMember factionMember = playerFaction.getMember(player);
        if (factionMember.getRole() == Role.MEMBER) {
            sender.sendMessage(ChatColor.RED + "You must be a faction officer to set the home.");
            return true;
        }
        final Location location = player.getLocation();
        boolean insideTerritory = false;
        for (final Claim claim : playerFaction.getClaims()) {
            if (!claim.contains(location)) {
                continue;
            }
            insideTerritory = true;
            break;
        }
        if (!insideTerritory) {
            player.sendMessage(ChatColor.RED + "You may only set your home in your territory.");
            return true;
        }
        playerFaction.setHome(location);
        Main.get().getWaypointManager().deleteFactionWaypoint(playerFaction, "Home", playerFaction.getHome(), -16776961);
        Main.get().getWaypointManager().createFactionWaypoint(playerFaction, "Home", playerFaction.getHome(), -16776961);
        playerFaction.broadcast(ConfigurationService.TEAMMATE_COLOR + factionMember.getRole().getAstrix() + sender.getName() + ChatColor.YELLOW + " has set the faction home.");
        return true;
    }
}
