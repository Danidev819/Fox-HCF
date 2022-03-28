package me.danidev.core.commands.message;

import me.danidev.core.Main;
import me.danidev.core.listeners.event.PlayerMessageEvent;
import me.danidev.core.managers.user.BaseUser;
import com.google.common.collect.Sets;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.HashSet;
import java.util.UUID;

public class ReplyCommand implements CommandExecutor {

    public ReplyCommand(Main plugin) {
        plugin.getCommand("reply").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        
        Player player = (Player) sender;

        BaseUser baseUser = Main.get().getUserManager().getBaseUser(player.getUniqueId());
        UUID lastReplied = baseUser.getLastRepliedTo();

        Player target = (lastReplied == null) ? null : Bukkit.getPlayer(lastReplied);

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");

            if (lastReplied != null && player.canSee(target)) {
                sender.sendMessage(ChatColor.RED + "You are in a conversation with " + target.getName() + '.');
            }
            return true;
        }

        if (target == null || (!player.canSee(target))) {
            sender.sendMessage(ChatColor.RED + "There is no player to reply to.");
            return true;
        }

        String message = StringUtils.join(args, ' ');

        HashSet<Player> recipients = Sets.newHashSet();

        PlayerMessageEvent playerMessageEvent = new PlayerMessageEvent(player, recipients, message, false, lastReplied);
        Bukkit.getPluginManager().callEvent(playerMessageEvent);

        if (!playerMessageEvent.isCancelled()) playerMessageEvent.send();
        return true;
    }
}
