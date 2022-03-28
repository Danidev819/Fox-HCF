package me.danidev.core.commands;

import me.danidev.core.Main;
import me.danidev.core.managers.menu.blockshop.BlockShopCategoryMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BlockShopCommand implements CommandExecutor {

    public BlockShopCommand(Main plugin) {
        plugin.getCommand("blockshop").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        new BlockShopCategoryMenu().openMenu(player);
        return false;
    }
}
