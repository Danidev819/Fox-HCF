package me.danidev.core.commands.essentials;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.file.FileConfig;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SudoCommand implements CommandExecutor {

    private final FileConfig langConfig = Main.get().getLangConfig();

    public SudoCommand(Main plugin) {
        plugin.getCommand("sudo").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (args.length < 1) {
            CC.message(player, "&cUsage: /" + command.getLabel() + " <player> <-c|-m> <command|message>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            CC.message(player, "&cPlayer not found.");
            return true;
        }

        if (args[1].equalsIgnoreCase("-c")) {
            if (args.length < 3) {
                CC.message(player, "&cUsage: /" + command.getLabel() + " <player> <-c> <command>");
                return true;
            }

            String executeCommand = StringUtils.join(args, ' ', 2, args.length);

            target.performCommand(executeCommand);
            CC.message(player, langConfig.getString("SUDO.COMMAND")
                    .replace("%PLAYER%", target.getName())
                    .replace("%COMMAND%", executeCommand));
        }
        else if (args[1].equalsIgnoreCase("-m")) {
            if (args.length < 3) {
                CC.message(player, "&cUsage: /" + command.getLabel() + " <player> <-m> <message>");
                return true;
            }

            String message = StringUtils.join(args, ' ', 2, args.length);
            target.chat(message);
            CC.message(player, langConfig.getString("SUDO.MESSAGE")
                    .replace("%PLAYER%", target.getName())
                    .replace("%MESSAGE%", message));
        }
        else {
            CC.message(player, "&cFormat '" + args[0] + "' not found.");
        }
        return false;
    }
}
