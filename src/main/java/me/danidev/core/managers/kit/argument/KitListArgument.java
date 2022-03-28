package me.danidev.core.managers.kit.argument;

import me.danidev.core.Main;
import me.danidev.core.managers.user.BaseUser;
import me.danidev.core.utils.BukkitUtils;
import me.danidev.core.utils.command.CommandArgument;
import me.danidev.core.managers.kit.Kit;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KitListArgument extends CommandArgument {

    private final Main plugin;

    public KitListArgument(final Main plugin) {
        super("list", "Lists all current kits");
        this.plugin = plugin;
        this.permission = "fhcf.command.kit.argument." + this.getName();
    }

    @Override
    public String getUsage(final String label) {
        return "/" + label + ' ' + this.getName();
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        final List<Kit> kits = this.plugin.getKitManager().getKits();
        if (kits.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No kits have been defined.");
            return true;
        }
        final ArrayList<String> kitNames = new ArrayList<String>();
        for (final Kit kit : kits) {
            final String permission = kit.getPermissionNode();
            if (permission != null && !sender.hasPermission(permission)) {
                continue;
            }
            final BaseUser user = this.plugin.getUserManager().getBaseUser(((Player) sender).getUniqueId());
            final ChatColor color = (user.getKitUses(kit) >= kit.getMaximumUses() || user.getRemainingKitCooldown(kit) >= kit.getMaximumUses() || this.plugin.getPlayTimeManager().getTotalPlayTime(((Player) sender).getUniqueId()) <= kit.getMinPlaytimeMillis()) ? ChatColor.RED : ChatColor.GREEN;
            kitNames.add(color + kit.getDisplayName());
        }
        final String kitList = StringUtils.join(kitNames, ChatColor.GRAY + ", ");
        sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + "Kit List" + ChatColor.GREEN + "[" + kitNames.size() + '/' + kits.size() + "]");
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.RED + kitList + ChatColor.GRAY + ']');
        sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.emptyList();
    }
}
