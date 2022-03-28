package me.danidev.core.commands.essentials;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;

public class NearCommand implements CommandExecutor {

    public NearCommand(Main plugin) {
        plugin.getCommand("near").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cNo console."));
            return false;
        }

        Player player = (Player) sender;
        Collection<Entity> nearby = player.getNearbyEntities(150.0, 150.0, 150.0);

        player.sendMessage(CC.translate(CC.MENU_BAR));
        player.sendMessage(CC.translate("&6&lNearby Players &7(150 x 150)"));
        player.sendMessage(CC.translate(""));

        for (Entity entity : nearby) {
            if (entity instanceof Player) {
                Player online = (Player) entity;
                String onlineName = Main.get().getRankManager().getRank().getPrefix(online) + online.getName();

                player.sendMessage(CC.translate("&6" + onlineName + " &7(" + Math.round(player.getLocation().distance(online.getLocation())) + ")"));
            }
        }

        player.sendMessage(CC.translate(CC.MENU_BAR));
        return true;
    }
}
