package me.danidev.core.managers.kit.argument;

import me.danidev.core.Main;
import me.danidev.core.utils.JavaUtils;
import me.danidev.core.utils.command.CommandArgument;
import me.danidev.core.managers.kit.Kit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.danidev.core.managers.kit.event.KitCreateEvent;

public class KitCreateArgument extends CommandArgument {

    private final Main plugin;

    public KitCreateArgument(Main plugin) {
        super("create", "Creates a kit");
        this.plugin = plugin;
        this.permission = "fhcf.command.kit.argument." + this.getName();
    }

    @Override
    public String getUsage(String label) {
        return "/" + label + ' ' + this.getName() + " <kitName> [kitDescription]";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may create kits.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        if (!JavaUtils.isAlphanumeric(args[1])) {
            sender.sendMessage(ChatColor.GRAY + "Kit names may only be alphanumeric.");
            return true;
        }
        Kit kit = this.plugin.getKitManager().getKit(args[1]);
        if (kit != null) {
            sender.sendMessage(ChatColor.RED + "There is already a kit named " + args[1] + '.');
            return true;
        }
        Player player = (Player) sender;
        kit = new Kit(args[1], (args.length >= 3) ? args[2] : null, player.getInventory(), player.getActivePotionEffects());
        KitCreateEvent event = new KitCreateEvent(kit);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return true;
        }
        this.plugin.getKitManager().createKit(kit);
        sender.sendMessage(ChatColor.GRAY + "Created kit '" + kit.getDisplayName() + "'.");
        return true;
    }
}
