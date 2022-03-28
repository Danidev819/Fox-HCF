package me.danidev.core.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.danidev.core.Main;
import me.danidev.core.utils.CC;

public class SetDeathbanSpawnCommand implements CommandExecutor {

    public SetDeathbanSpawnCommand() {
        Main.get().getCommand("setdeathbanspawn").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        Main.get().getDeathbanManager().setDeathbanArena(player.getLocation().clone());

        player.sendMessage(CC.translate("&aDeathban's arena spawn set!"));
        return true;
    }
}