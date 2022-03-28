package me.danidev.core.commands;

import me.danidev.core.Main;
import me.danidev.core.managers.support.menu.SupportMenu;
import me.danidev.core.utils.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SupportCommand implements CommandExecutor {

    public SupportCommand(Main plugin) {
        plugin.getCommand("support").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)){
            commandSender.sendMessage(CC.translate("&cNo console."));
        } else {
            new SupportMenu().openMenu(((Player) commandSender).getPlayer());
        }
        return false;
    }
}