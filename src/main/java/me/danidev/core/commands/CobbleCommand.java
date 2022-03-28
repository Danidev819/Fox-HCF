package me.danidev.core.commands;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.file.FileConfig;
import com.google.common.collect.Sets;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.util.Set;
import java.util.UUID;

import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

public class CobbleCommand implements Listener, CommandExecutor {

    public static final Set<UUID> COBBLE = Sets.newHashSet();
    private final FileConfig langConfig = Main.get().getLangConfig();

    public CobbleCommand(Main plugin) {
        plugin.getCommand("cobble").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This commands is only executable by players.");
            return true;
        }

        Player player = (Player) sender;

        if (!COBBLE.contains(player.getUniqueId())) {
            COBBLE.add(player.getUniqueId());
            player.sendMessage(CC.translate(langConfig.getString("COBBLE.DISABLED")));
        }
        else {
            COBBLE.remove(player.getUniqueId());
            player.sendMessage(CC.translate(langConfig.getString("COBBLE.ENABLED")));
        }
        return true;
    }
}
