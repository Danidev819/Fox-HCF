package me.danidev.core.commands.reclaim.argument;

import me.danidev.core.Main;
import me.danidev.core.managers.user.FactionUser;
import me.danidev.core.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class SetReclaimCommand implements CommandExecutor {
    
    public SetReclaimCommand(Main plugin) {
        plugin.getCommand("setreclaim").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(CC.translate("&cUsage: /" + label + " <player> <boolean>"));
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);

        if (player == null) {
            sender.sendMessage(CC.translate("Player not found."));
            return true;
        }

        FactionUser factionUser = Main.get().getUserManager().getUser(player.getUniqueId());
        boolean parseBoolean = Boolean.parseBoolean(args[1]);

        factionUser.setReclaimed(parseBoolean);
        sender.sendMessage(CC.translate("&6" + player.getName() + " &ereclaim has been set to &7" + parseBoolean + "&e."));
        return false;
    }
}
