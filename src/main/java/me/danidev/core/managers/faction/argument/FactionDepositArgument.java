package me.danidev.core.managers.faction.argument;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import me.danidev.core.Main;
import me.danidev.core.managers.faction.struct.Relation;
import me.danidev.core.utils.JavaUtils;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.google.common.collect.ImmutableList;
import me.danidev.core.managers.faction.type.PlayerFaction;

public class FactionDepositArgument extends CommandArgument {
	
    private static final ImmutableList<String> COMPLETIONS;
    private final Main plugin;
    
    static {
        COMPLETIONS = ImmutableList.of("all");
    }
    
    public FactionDepositArgument(final Main plugin) {
        super("deposit", "Deposit money to faction.", new String[]{"d"});
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return "/" + label + ' ' + this.getName() + " <all|amount>";
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This commands is only executable by players.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final Player player = (Player)sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        final UUID uuid = player.getUniqueId();
        final int playerBalance = this.plugin.getEconomyManager().getBalance(uuid);
        Integer amount;
        if (args[1].equalsIgnoreCase("all")) {
            amount = playerBalance;
        }
        else {
            amount = JavaUtils.tryParseInt(args[1]);
            if (amount == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
                return true;
            }
        }
        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "Amount must be positive.");
            return true;
        }
        if (playerBalance < amount) {
            sender.sendMessage(ChatColor.RED + "You need at least " + '$' + JavaUtils.format((Number)amount) + " to do this, you only have " + '$' + JavaUtils.format((Number)playerBalance) + '.');
            return true;
        }
        this.plugin.getEconomyManager().subtractBalance(uuid, amount);
        playerFaction.setBalance(playerFaction.getBalance() + amount);
        playerFaction.broadcast(Relation.MEMBER.toChatColour() + playerFaction.getMember(player).getRole().getAstrix() + sender.getName() + ChatColor.YELLOW + " has deposited " + ChatColor.GREEN + '$' + JavaUtils.format((Number)amount) + ChatColor.YELLOW + " into the faction balance.");
        return true;
    }
    
    @SuppressWarnings("unchecked")
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (List<String>)((args.length == 2) ? FactionDepositArgument.COMPLETIONS : Collections.emptyList());
    }
}
