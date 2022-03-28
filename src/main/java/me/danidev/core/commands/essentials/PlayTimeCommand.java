package me.danidev.core.commands.essentials;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.file.FileConfig;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayTimeCommand implements CommandExecutor {

    private final FileConfig langConfig = Main.get().getLangConfig();

    public PlayTimeCommand(Main plugin) {
        plugin.getCommand("playtime").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(CC.translate(langConfig.getString("PLAY_TIME.YOUR_SELF")
                    .replace("%TIME%", DurationFormatUtils.formatDurationWords(
                            Main.get().getPlayTimeManager().getTotalPlayTime(player.getUniqueId()),
                            true, true))));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(CC.translate("&cPlayer not found."));
            return true;
        }

        player.sendMessage(CC.translate(langConfig.getString("PLAY_TIME.OTHER")
                .replace("%PLAYER%", target.getName())
                .replace("%TIME%", DurationFormatUtils.formatDurationWords(
                        Main.get().getPlayTimeManager().getTotalPlayTime(target.getUniqueId()),
                        true, true))));
        return true;
    }
}
