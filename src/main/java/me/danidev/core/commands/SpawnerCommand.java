package me.danidev.core.commands;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;

public class SpawnerCommand implements CommandExecutor {

    public SpawnerCommand(Main plugin) {
        plugin.getCommand("spawner").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player )sender;

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "/spawner <entity>");
            return true;
        }

        String spawner = args[0];

        player.getInventory().addItem(getSpawner(spawner));
        player.sendMessage(CC.translate("&eYou just got a &a" + spawner + " Spawner&e."));
        return false;
    }

    private ItemStack getSpawner(String spawner) {
        return new ItemBuilder(Material.MOB_SPAWNER)
                .name("&a" + spawner + " Spawner")
                .build();
    }
}
