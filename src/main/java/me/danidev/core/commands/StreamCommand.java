package me.danidev.core.commands;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.file.FileConfig;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StreamCommand implements CommandExecutor {

    private final FileConfig langConfig = Main.get().getLangConfig();

    public StreamCommand(Main plugin) {
        plugin.getCommand("stream").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(CC.translate("&cUsage: /" + label + " <url>"));
            return true;
        }

        String url = StringUtils.join(args, ' ', 0, args.length);

        if (url.startsWith("https://")) {
            langConfig.getStringList("STREAM").forEach(lines ->
                    Bukkit.broadcastMessage(CC.translate(lines
                            .replace("%PLAYER%", player.getName())
                            .replace("%URL%", url))));
            return true;
        } else {
            player.sendMessage(CC.translate("&bYou need a link with https:// to public this message!"));
        }
        return false;
    }
}
