package me.danidev.core.commands.reclaim.argument;

import me.danidev.core.Main;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class ReclaimCommand implements CommandExecutor {

    public ReclaimCommand(Main plugin) {
        plugin.getCommand("reclaim").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        Main.get().getReclaimManager().getReclaim(player);
        return false;
    }
}
