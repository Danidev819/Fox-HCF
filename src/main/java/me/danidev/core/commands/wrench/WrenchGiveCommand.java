package me.danidev.core.commands.wrench;

import me.danidev.core.Main;
import me.danidev.core.managers.wrench.Wrench;
import me.danidev.core.utils.CC;
import org.bukkit.inventory.ItemStack;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class WrenchGiveCommand implements CommandExecutor {

    public WrenchGiveCommand(Main plugin) {
        plugin.getCommand("wrenchgive").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(CC.translate("&cUsage: /" + label + " <player>"));
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);

        if (player == null) {
            sender.sendMessage(CC.translate("&cPlayer not found."));
            return true;
        }

        ItemStack wrench = new Wrench().getItemIfPresent();

        player.getInventory().addItem(wrench);
        player.sendMessage(CC.translate("&eYou were given a &cWrench &efrom " + sender.getName() + "."));
        sender.sendMessage(CC.translate("&eYou have given " + player.getName() + " a &cWrench&e."));
        return true;
    }
}
