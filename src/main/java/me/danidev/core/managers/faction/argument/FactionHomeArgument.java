package me.danidev.core.managers.faction.argument;

import me.danidev.core.Main;
import me.danidev.core.managers.games.koth.faction.EventFaction;
import me.danidev.core.managers.timer.PlayerTimer;
import me.danidev.core.utils.DurationFormatter;
import me.danidev.core.utils.command.CommandArgument;
import me.danidev.core.managers.faction.FactionExecutor;
import org.bukkit.Location;

import java.util.UUID;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.faction.type.PlayerFaction;

public class FactionHomeArgument extends CommandArgument {
	
    private final FactionExecutor factionExecutor;
    private final Main plugin;
    
    public FactionHomeArgument(final FactionExecutor factionExecutor, final Main plugin) {
        super("home", "Teleport to your home location.");
        this.factionExecutor = factionExecutor;
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This commands is only executable by players.");
            return true;
        }
        final Player player = (Player)sender;
        if (args.length >= 2 && args[1].equalsIgnoreCase("set")) {
            this.factionExecutor.getArgument("sethome").onCommand(sender, command, label, args);
            return true;
        }
        final UUID uuid = player.getUniqueId();
        PlayerTimer timer = this.plugin.getTimerManager().enderPearlTimer;
        long remaining = timer.getRemaining(player);
        if (remaining > 0L) {
            sender.sendMessage(ChatColor.RED + "You cannot warp whilst your " + timer.getName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + DurationFormatter.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]");
            return true;
        }
        if ((remaining = (timer = this.plugin.getTimerManager().getSpawnTagTimer()).getRemaining(player)) > 0L) {
            sender.sendMessage(ChatColor.RED + "You cannot warp whilst your " + timer.getName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + DurationFormatter.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]");
            return true;
        }
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(uuid);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        final Location home = playerFaction.getHome();
        if (home == null) {
            sender.sendMessage(ChatColor.RED + "Your faction does not have a home set.");
            return true;
        }
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(player.getLocation());
        if (factionAt instanceof EventFaction) {
            sender.sendMessage(ChatColor.RED + "You cannot warp whilst in event zones.");
            return true;
        }
        long millis = 0L;
        if (factionAt.isSafezone()) {
            millis = 0L;
        }
        else {
            switch (player.getWorld().getEnvironment()) {
                case THE_END: {
                    sender.sendMessage(ChatColor.RED + "You cannot teleport to your faction home whilst in The End.");
                    return true;
                }
                case NETHER: {
                    millis = 30000L;
                    break;
                }
                default: {
                    millis = 10000L;
                    break;
                }
            }
        }
        if (!factionAt.equals(playerFaction) && factionAt instanceof PlayerFaction) {
            millis *= 2L;
        }
        if (this.plugin.getTimerManager().protectionTimer.getRemaining(player.getUniqueId()) > 0L) {
            player.sendMessage(ChatColor.RED + "You still have PvP Protection, you must enable it to teleport.");
            return true;
        }
        this.plugin.getTimerManager().getTeleportTimer().teleport(player, home, millis, ChatColor.YELLOW + "Sending you to your faction home in " + ChatColor.LIGHT_PURPLE + DurationFormatter.getRemaining(millis, true, false) + ChatColor.YELLOW + ". Do not move or take damage.", PlayerTeleportEvent.TeleportCause.COMMAND);
        return true;
    }
}
