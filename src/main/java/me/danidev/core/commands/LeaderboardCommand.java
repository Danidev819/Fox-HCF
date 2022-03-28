package me.danidev.core.commands;

import me.danidev.core.Main;
import me.danidev.core.managers.menu.leaderboard.LeaderboardMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaderboardCommand implements CommandExecutor {

    public LeaderboardCommand(Main plugin) {
        plugin.getCommand("leaderboard").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        new LeaderboardMenu().openMenu(player);
        return false;
    }
}
