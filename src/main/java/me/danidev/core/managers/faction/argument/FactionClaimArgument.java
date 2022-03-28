package me.danidev.core.managers.faction.argument;

import me.danidev.core.Main;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.danidev.core.managers.faction.claim.ClaimHandler;
import me.danidev.core.managers.faction.type.PlayerFaction;

public class FactionClaimArgument extends CommandArgument
{
    private final Main plugin;
    
    public FactionClaimArgument(final Main plugin) {
        super("claim", "Claim lands on the map.");
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return "/" + label + ' ' + this.getName();
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This commands is only executable by players.");
            return true;
        }
        final Player player = (Player)sender;
        final UUID uuid = player.getUniqueId();
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(uuid);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        if (playerFaction.isRaidable()) {
            sender.sendMessage(ChatColor.RED + "You cannot claim land for your faction while raidable.");
            return true;
        }
        final PlayerInventory inventory = player.getInventory();
        if (inventory.contains(ClaimHandler.CLAIM_WAND)) {
            sender.sendMessage(ChatColor.RED + "You already have a claiming wand in your inventory.");
            return true;
        }
        if (!inventory.addItem(new ItemStack[] { ClaimHandler.CLAIM_WAND }).isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Your inventory is full.");
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "You have recieved a" + ChatColor.LIGHT_PURPLE + " claiming wand" + ChatColor.YELLOW + ". Read the item to understand how to claim. You can also" + ChatColor.YELLOW + " use " + ChatColor.RED + '/' + label + " claimchunk" + ChatColor.YELLOW + '.');
        return true;
    }
}
