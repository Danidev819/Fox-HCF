package me.danidev.core.commands.essentials;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.file.FileConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ListCommand implements CommandExecutor {

    private final FileConfig langConfig = Main.get().getLangConfig();

    public ListCommand(Main plugin) {
        plugin.getCommand("list").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        for (String message : langConfig.getStringList("LIST")) {
            sender.sendMessage(CC.translate(message
                    .replace("%ONLINE%", String.valueOf(Bukkit.getOnlinePlayers().size()))));
        }
        return true;
    }
}
