package me.danidev.core.commands.essentials;

import me.danidev.core.Main;
import me.danidev.core.utils.BukkitUtils;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.JavaUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EnchantCommand implements CommandExecutor, TabCompleter {
	
    public EnchantCommand(Main plugin) {
        plugin.getCommand("enchant").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(CC.translate(CC.MENU_BAR));
            player.sendMessage(CC.translate("&6&lEnchant Commands"));
            player.sendMessage(CC.translate(""));
            player.sendMessage(CC.translate("&7/" + command.getLabel() + " add <enchantment> <level>"));
            player.sendMessage(CC.translate("&7/" + command.getLabel() + " remove <enchantment>"));
            player.sendMessage(CC.translate("&7/" + command.getLabel() + " list"));
            player.sendMessage(CC.translate(CC.MENU_BAR));
            return true;
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (args.length < 3) {
                player.sendMessage(CC.translate("&cUsage: /" + command.getLabel() + " add <enchantment> <level>"));
                return true;
            }

            ItemStack itemStack = player.getItemInHand();

            if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
                player.sendMessage(CC.translate("&cYou need hold any item."));
                return true;
            }

            String enchantmentName = args[1].toUpperCase();
            Enchantment enchantment = Enchantment.getByName(enchantmentName);

            if (enchantment == null) {
                player.sendMessage(CC.translate("&cEnchant '" + enchantmentName + "' not found."));
                return true;
            }

            Integer level = JavaUtils.tryParseInt(args[2]);

            if (level == null) {
                player.sendMessage(CC.translate("&cEnchant level must be a number."));
                return true;
            }

            itemStack.addUnsafeEnchantment(enchantment, level);
            player.sendMessage(CC.translate("&eEnchant add &c" + enchantment.getName() + " &ewith level &c"
                    + level + " &eto &c" + itemStack.getType().name()));
        }
        else if (args[0].equalsIgnoreCase("remove")) {
            if (args.length < 2) {
                player.sendMessage(CC.translate("&cUsage: /" + command.getLabel() + " remove <enchantment>"));
                return true;
            }

            ItemStack itemStack = player.getItemInHand();

            if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
                player.sendMessage(CC.translate("&cYou need hold any item."));
                return true;
            }

            String enchantmentName = args[1].toUpperCase();
            Enchantment enchantment = Enchantment.getByName(enchantmentName);

            if (enchantment == null) {
                player.sendMessage(CC.translate("&cEnchant '" + enchantmentName + "' not found."));
                return true;
            }

            itemStack.removeEnchantment(enchantment);
            player.sendMessage(CC.translate("&eEnchant remove &c" + enchantment.getName()
                    + " &eto &c" + itemStack.getType().name()));
        }
        else if (args[0].equalsIgnoreCase("list")) {
            player.sendMessage(CC.translate(CC.MENU_BAR));
            player.sendMessage(CC.translate("&6&lEnchantment List"));
            player.sendMessage(CC.translate(""));
            for (Enchantment enchantment :  Enchantment.values()) {
                player.sendMessage(CC.translate(" &7- " + enchantment.getName()));
            }
            player.sendMessage(CC.translate(CC.MENU_BAR));
        }
        else {
            player.sendMessage(CC.translate("&cEnchant '" + args[0] + "' argument not found."));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 2: {
                Enchantment[] enchantments = Enchantment.values();
                ArrayList<String> results = new ArrayList<>(enchantments.length);
                Enchantment[] array;

                for (int length = (array = enchantments).length, i = 0; i < length; ++i) {
                    Enchantment enchantment = array[i];
                    results.add(enchantment.getName());
                }
                return BukkitUtils.getCompletions(args, results);
            }
            case 3: {
                return null;
            }
            default: {
                return Collections.emptyList();
            }
        }
    }
}






//FOX HCF MADE BY Danidev819