package me.danidev.core.commands.essentials;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.file.FileConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KillCommand implements CommandExecutor {

    private final FileConfig langConfig = Main.get().getLangConfig();

    public KillCommand(Main plugin) {
        plugin.getCommand("kill").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (args.length == 0) {
            player.setHealth(0.0);
            player.sendMessage(CC.translate(langConfig.getString("KILL.YOUR_SELF")));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(CC.translate("&cPlayer not found."));
            return true;
        }

        target.setHealth(0.0);
        player.sendMessage(CC.translate(langConfig.getString("KILL.OTHER")
                .replace("%PLAYER%", target.getName())));
        return true;
    }
}
