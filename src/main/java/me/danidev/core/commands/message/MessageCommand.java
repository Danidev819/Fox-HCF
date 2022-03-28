package me.danidev.core.commands.message;

import me.danidev.core.Main;
import me.danidev.core.listeners.event.PlayerMessageEvent;
import me.danidev.core.utils.CC;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Set;

public class MessageCommand implements CommandExecutor {

    public MessageCommand(Main plugin) {
        plugin.getCommand("message").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (args.length < 2) {
            player.sendMessage(CC.translate("&cUsage: /" + label + " <player> <message>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(CC.translate("&cPlayer not found."));
            return true;
        }

        String message = StringUtils.join(args, ' ', 1, args.length);

        Set<Player> recipients = Collections.singleton(target);

        PlayerMessageEvent playerMessageEvent = new PlayerMessageEvent(player, recipients, message, false, null);
        Bukkit.getPluginManager().callEvent(playerMessageEvent);

        if (!playerMessageEvent.isCancelled()) playerMessageEvent.send();
        return true;
    }
}
