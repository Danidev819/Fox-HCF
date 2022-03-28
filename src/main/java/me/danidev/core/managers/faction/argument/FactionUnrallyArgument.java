package me.danidev.core.managers.faction.argument;

import me.danidev.core.Main;
import me.danidev.core.managers.faction.type.PlayerFaction;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.command.CommandArgument;
import me.danidev.core.utils.file.FileConfig;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FactionUnrallyArgument extends CommandArgument {

    private final Main plugin;
    private final FileConfig langConfig = Main.get().getLangConfig();

    public FactionUnrallyArgument(final Main plugin) {
        super("unrally", "Remove a rally waypoint.");
        this.plugin = plugin;
    }


    public String getUsage(final String label) {
        return "/" + label + ' ' + this.getName();
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This commands is only executable by players.");
            return true;
        }
        final Player player = (Player)sender;
        final UUID uuid = player.getUniqueId();
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(uuid);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        if(!playerFaction.isRallyEnabled()) {
            sender.sendMessage(ChatColor.RED + "There is no rally.");
            return true;
        }
        Main.get().getWaypointManager().deleteFactionWaypoint(playerFaction, "Faction Rally", playerFaction.getRally(), Color.ORANGE.asBGR());
        playerFaction.setRallyEnabled(false);
        playerFaction.broadcast(CC.translate(langConfig.getString("FACTION.UNRALLY")
                .replace("%PLAYER%", playerFaction.getMember(player).getRole().getAstrix() + player.getName())));
        return true;
    }
}


//Fox HCF Made by Danidev819 :D