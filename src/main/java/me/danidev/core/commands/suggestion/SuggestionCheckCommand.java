package me.danidev.core.commands.suggestion;

import me.danidev.core.Main;
import me.danidev.core.managers.menu.suggestion.SuggestionMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SuggestionCheckCommand implements CommandExecutor {

    public SuggestionCheckCommand(Main plugin) {
        plugin.getCommand("suggestioncheck").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        new SuggestionMenu().openMenu(player);
        return true;
    }
}
