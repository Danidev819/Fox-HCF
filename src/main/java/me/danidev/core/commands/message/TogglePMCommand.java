package me.danidev.core.commands.message;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.file.FileConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TogglePMCommand implements CommandExecutor {

    public static Set<UUID> MSG = new HashSet<>();

    private final FileConfig langConfig = Main.get().getLangConfig();

    public TogglePMCommand(Main plugin) {
        plugin.getCommand("togglepm").setExecutor(this);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        
        Player player = (Player) sender;

        if (!MSG.contains(player.getUniqueId())) {
            MSG.add(player.getUniqueId());
            player.sendMessage(CC.translate(langConfig.getString("TOGGLE_PM.DISABLED")));
        } 
        else {
            MSG.remove(player.getUniqueId());
            player.sendMessage(CC.translate(langConfig.getString("TOGGLE_PM.ENABLED")));
        }
        return true;
    }
}