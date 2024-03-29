package me.danidev.core.managers.kit.argument;

import me.danidev.core.Main;
import me.danidev.core.utils.command.CommandArgument;
import me.danidev.core.managers.kit.Kit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class KitGuiArgument extends CommandArgument {

    private final Main plugin;

    public KitGuiArgument(final Main plugin) {
        super("gui", "Opens the kit gui");
        this.plugin = plugin;
        this.permission = "fhcf.command.kit.argument." + this.getName();
    }

    @Override
    public String getUsage(final String label) {
        return "/" + label + ' ' + this.getName();
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may open kit GUI's.");
            return true;
        }
        List<Kit> kits = this.plugin.getKitManager().getKits();
        if (kits.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No kits have been defined.");
            return true;
        }
        final Player player = (Player) sender;
        player.openInventory(this.plugin.getKitManager().getGui(player));
        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.emptyList();
    }
}
