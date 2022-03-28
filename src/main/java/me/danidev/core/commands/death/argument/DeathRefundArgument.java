package me.danidev.core.commands.death.argument;

import me.danidev.core.listeners.death.DeathListener;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DeathRefundArgument extends CommandArgument {

    public DeathRefundArgument() {
        super("refund", "Rollback an inventory");
    }

    public String getUsage(final String label) {
        return String.valueOf('/') + label + ' ' + this.getName() + " <playerName>";
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;


            if (p.hasPermission("fhcf.command.death.argument.restore")) {
                if (args.length != 2) {
                    p.sendMessage(ChatColor.RED + "Usage: /death restore <player>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    p.sendMessage(ChatColor.RED + "Usage: /death restore <player>");
                    return true;
                }
                if (DeathListener.getPlayerContents().containsKey(target.getUniqueId())) {
                    ItemStack[] inventory = DeathListener.getPlayerContents().get(target.getUniqueId());
                    ItemStack[] armor = DeathListener.getArmor().get(target.getUniqueId());
                    target.getInventory().setContents(inventory);
                    target.getInventory().setArmorContents(armor);
                }
                else {
                    p.sendMessage(ChatColor.RED + "No inventories saved.");
                }
            } else {
                p.sendMessage(CC.translate("&cNo permissions"));
                return true;
            }
        } else {
            sender.sendMessage(CC.translate("&cPlayers only"));
        }
        return true;
    }
}
