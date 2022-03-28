package me.danidev.core.managers.kit.argument;

import me.danidev.core.utils.CC;
import me.danidev.core.utils.command.CommandArgument;
import me.danidev.core.managers.kit.Kit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.danidev.core.Main;

import java.util.Collections;
import java.util.List;

public class KitSetSlotArgument extends CommandArgument {

    private final Main plugin;

    public KitSetSlotArgument(Main plugin) {
        super("setslot", "set a slot on HCF Inventory");
        this.plugin = plugin;
        this.permission = "fhcf.command.kit.argument." + this.getName();
    }

    @Override
    public String getUsage(String label) {
        return "/" + label + ' ' + this.getName() + " <kitName> <slot>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        Kit kit = this.plugin.getKitManager().getKit(args[1]);
        if (kit == null) {
            sender.sendMessage(ChatColor.RED + "Kit '" + args[1] + "' not found.");
            return true;
        }
        int slot = Integer.valueOf(args[2]);
        if (slot > 45 || slot < 1) {
            sender.sendMessage(CC.translate("&cThe number that you gave it's not registered on the available numbers."));
            return true;
        }
        kit.setSlot(slot);
        sender.sendMessage(CC.translate("&aSuccessfully set the slot '" + slot + "' to the kit &l" + args[1] + "&a."));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
