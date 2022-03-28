package me.danidev.core.commands.essentials;

import me.danidev.core.Main;
import me.danidev.core.utils.BukkitUtils;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.file.FileConfig;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GamemodeCommand implements CommandExecutor, TabCompleter {

    private final FileConfig langConfig = Main.get().getLangConfig();
	
    public GamemodeCommand(Main plugin) {
        plugin.getCommand("gamemode").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(CC.translate("&cUsage: /" + label + " <modeName> [playerName]"));
            return true;
        }

        GameMode mode = this.getGameModeByName(args[0]);

        if (mode == null) {
            sender.sendMessage(ChatColor.RED + "Gamemode '" + args[0] + "' not found.");
            return true;
        }
        
        Player target;
        
        if (args.length > 1) {
            target = (sender.hasPermission(command.getPermission() + ".others") ? BukkitUtils.playerWithNameOrUUID(args[1]) : null);
        } 
        else {
            if (!(sender instanceof Player)) {
            	sender.sendMessage(CC.translate("&cUsage: /" + label + " <modeName> [playerName]"));
                return true;
            }
            target = (Player) sender;
        }
        if (!BukkitUtils.canSee(sender, target)) {
            sender.sendMessage(CC.translate("&cPlayer not found."));
            return true;
        }
        
        if (target.getGameMode() == mode) {
            sender.sendMessage(ChatColor.RED + "Gamemode of " + target.getName() + " is already " + mode.name() + '.');
            return true;
        }
        
        target.setGameMode(mode);
        sender.sendMessage(CC.translate(langConfig.getString("GAMEMODE")
                .replace("%PLAYER%", target.getName())
                .replace("%GAMEMODE%", mode.name())));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            return Collections.emptyList();
        }

        GameMode[] gameModes = GameMode.values();
        ArrayList<String> results = new ArrayList<>(gameModes.length);
        GameMode[] array;

        for (int length = (array = gameModes).length, i = 0; i < length; ++i) {
            GameMode mode = array[i];
            results.add(mode.name());
        }
        return BukkitUtils.getCompletions(args, results);
    }

    private GameMode getGameModeByName(String gamemode) {
        if (gamemode.equalsIgnoreCase("gms")
                || gamemode.contains("survival")
                || gamemode.equalsIgnoreCase("0")
                || gamemode.equalsIgnoreCase("s")) {
            return GameMode.SURVIVAL;
        }
        if (gamemode.equalsIgnoreCase("gmc")
                || gamemode.contains("creative")
                || gamemode.equalsIgnoreCase("1")
                || gamemode.equalsIgnoreCase("c")) {
            return GameMode.CREATIVE;
        }
        if (gamemode.equalsIgnoreCase("gma")
                || gamemode.contains("adventure")
                || gamemode.equalsIgnoreCase("2")
                || gamemode.equalsIgnoreCase("a")) {
            return GameMode.ADVENTURE;
        }
        return null;
    }
}
