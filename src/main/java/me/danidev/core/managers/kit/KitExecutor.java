package me.danidev.core.managers.kit;

import me.danidev.core.Main;
import me.danidev.core.utils.BukkitUtils;
import me.danidev.core.utils.command.ArgumentExecutor;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.danidev.core.managers.kit.argument.KitApplyArgument;
import me.danidev.core.managers.kit.argument.KitCreateArgument;
import me.danidev.core.managers.kit.argument.KitDeleteArgument;
import me.danidev.core.managers.kit.argument.KitDisableArgument;
import me.danidev.core.managers.kit.argument.KitGuiArgument;
import me.danidev.core.managers.kit.argument.KitHelpArgument;
import me.danidev.core.managers.kit.argument.KitListArgument;
import me.danidev.core.managers.kit.argument.KitPreviewArgument;
import me.danidev.core.managers.kit.argument.KitRenameArgument;
import me.danidev.core.managers.kit.argument.KitSetDelayArgument;
import me.danidev.core.managers.kit.argument.KitSetDescriptionArgument;
import me.danidev.core.managers.kit.argument.KitSetImageArgument;
import me.danidev.core.managers.kit.argument.KitSetIndexArgument;
import me.danidev.core.managers.kit.argument.KitSetItemsArgument;
import me.danidev.core.managers.kit.argument.KitSetMaxUsesArgument;
import me.danidev.core.managers.kit.argument.KitSetSlotArgument;
import me.danidev.core.managers.kit.argument.KitSetMinPlayTimeArgument;

import java.util.ArrayList;
import java.util.List;

public class KitExecutor extends ArgumentExecutor {

    private final Main plugin;

    public KitExecutor(Main plugin) {
        super("kit");
        this.plugin = plugin;
        this.addArgument(new KitApplyArgument(plugin));
        this.addArgument(new KitCreateArgument(plugin));
        this.addArgument(new KitDeleteArgument(plugin));
        this.addArgument(new KitSetDescriptionArgument(plugin));
        this.addArgument(new KitDisableArgument(plugin));
        this.addArgument(new KitGuiArgument(plugin));
        this.addArgument(new KitListArgument(plugin));
        this.addArgument(new KitPreviewArgument(plugin));
        this.addArgument(new KitRenameArgument(plugin));
        this.addArgument(new KitSetDelayArgument(plugin));
        this.addArgument(new KitSetImageArgument(plugin));
        this.addArgument(new KitSetIndexArgument(plugin));
        this.addArgument(new KitSetItemsArgument(plugin));
        this.addArgument(new KitSetMaxUsesArgument(plugin));
        this.addArgument(new KitSetMinPlayTimeArgument(plugin));
        this.addArgument(new KitHelpArgument(plugin));
        this.addArgument(new KitSetSlotArgument(plugin));

        plugin.getCommand("kit").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players may open kit GUI's.");
                return true;
            }
            List<Kit> kits = this.plugin.getKitManager().getKits();
            if (kits.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "No kits have been defined.");
                return true;
            }
            Player player = (Player) sender;
            player.openInventory(this.plugin.getKitManager().getGui(player));
            return true;
        }
        CommandArgument argument = this.getArgument(args[0]);
        String permission = (argument == null) ? null : argument.getPermission();
        if (argument != null && (permission == null || sender.hasPermission(permission))) {
            argument.onCommand(sender, command, label, args);
            return true;
        }
        Kit kit = this.plugin.getKitManager().getKit(args[0]);
        String kitPermission;
        if (sender instanceof Player && kit != null
                && ((kitPermission = kit.getPermissionNode()) == null || sender.hasPermission(kitPermission))) {
            Player player = (Player) sender;
            kit.applyTo(player, false, true);
            return true;
        }
        sender.sendMessage(ChatColor.RED + "Kit or commands " + args[0] + " not found.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            return super.onTabComplete(sender, command, label, args);
        }
        List<String> previous = super.onTabComplete(sender, command, label, args);
        ArrayList<String> kitNames = new ArrayList<>();
        for (Kit kit : this.plugin.getKitManager().getKits()) {
            String permission = kit.getPermissionNode();
            if (permission != null && !sender.hasPermission(permission)) {
                continue;
            }
            kitNames.add(kit.getName());
        }
        if (previous == null || previous.isEmpty()) {
            previous = kitNames;
        } else {
            previous = new ArrayList<>(previous);
            previous.addAll(0, kitNames);
        }
        return BukkitUtils.getCompletions(args, previous);
    }
}
