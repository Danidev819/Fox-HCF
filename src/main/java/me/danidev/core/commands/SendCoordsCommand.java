package me.danidev.core.commands;

import java.text.DecimalFormat;

import me.danidev.core.Main;
import me.danidev.core.managers.faction.type.PlayerFaction;
import me.danidev.core.utils.CC;
import org.bukkit.entity.Player;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import org.bukkit.command.CommandExecutor;

public class SendCoordsCommand implements CommandExecutor {
    
    public SendCoordsCommand(Main plugin) {
        plugin.getCommand("sendcoords").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        PlayerFaction playerFaction = Main.get().getFactionManager().getPlayerFaction(player.getUniqueId());

        if (playerFaction == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to use this commands.");
            return true;
        }

        DecimalFormat decimalFormat = new DecimalFormat("#");
        playerFaction.broadcast(CC.translate("&2(Faction) " + player.getName() + "&7: &e[" +
                decimalFormat.format(player.getLocation().getX()) + ", " +
                decimalFormat.format(player.getLocation().getY()) + ", " +
                decimalFormat.format(player.getLocation().getZ()) + "]"));
        return true;
    }
}
