package me.danidev.core.commands;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class SafeStopCommand implements CommandExecutor {

    public SafeStopCommand(Main plugin) {
        plugin.getCommand("safestop").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "savedata");

        Main.get().getUserManager().saveUserData();
        Main.get().getFactionManager().saveFactionData();
        Main.get().getEconomyManager().saveEconomyData();

        Bukkit.getOnlinePlayers().forEach(online -> online.performCommand("hub"));

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(CC.translate("&6The server has been stopped by " + sender.getName() + "."));
        Bukkit.broadcastMessage("");

        Bukkit.shutdown();
        return true;
    }
}
