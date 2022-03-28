package me.danidev.core.commands.essentials;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.file.FileConfig;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BroadcastCommand implements CommandExecutor {

    private final FileConfig langConfig = Main.get().getLangConfig();
	
	public BroadcastCommand(Main plugin) {
		plugin.getCommand("broadcast").setExecutor(this);
	}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " (-raw) <message>");
            return true;
        }

        if (args[0].equalsIgnoreCase("-raw")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " (-raw) <message>");
                return true;
            }

            String message = StringUtils.join(args, ' ', 1, args.length);

            Bukkit.broadcastMessage(CC.translate(message));
            return true;
        }

        String message = StringUtils.join(args, ' ', 0, args.length);

        Bukkit.broadcastMessage(CC.translate(langConfig.getString("BROADCAST")
                .replace("%MESSAGE%", message)));
        return true;
    }
}
