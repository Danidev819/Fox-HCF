package me.danidev.core.managers.faction.argument.staff;

import me.danidev.core.Main;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.Bukkit;
import java.util.ArrayList;
import java.util.Collections;
import org.bukkit.entity.Player;
import java.util.List;

import org.bukkit.conversations.Conversable;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Prompt;
import org.bukkit.plugin.Plugin;
import org.bukkit.conversations.ConversationFactory;
import me.danidev.core.managers.faction.type.ClaimableFaction;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.faction.type.PlayerFaction;

public class FactionClearClaimsArgument extends CommandArgument {
	
    private final ConversationFactory factory;
    private final Main plugin;
    
    public FactionClearClaimsArgument(final Main plugin) {
        super("clearclaims", "Clear the terrains of a faction.");
        this.plugin = plugin;
        this.permission = "fhcf.commands.faction.argument." + this.getName();
        this.factory = new ConversationFactory((Plugin)plugin).withFirstPrompt((Prompt)new ClaimClearAllPrompt(plugin)).withEscapeSequence("/no").withTimeout(10).withModality(false).withLocalEcho(true);
    }
    
    public String getUsage(final String label) {
        return "/" + label + ' ' + this.getName() + " <playerName|factionName|all>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
            return true;
        }
        if (args[1].equalsIgnoreCase("all")) {
            if (!(sender instanceof ConsoleCommandSender)) {
                sender.sendMessage(ChatColor.RED + "This commands can be only executed from console.");
                return true;
            }
            final Conversable conversable = (Conversable)sender;
            conversable.beginConversation(this.factory.buildConversation(conversable));
            return true;
        }
        else {
            final Faction faction = this.plugin.getFactionManager().getContainingFaction(args[1]);
            if (faction == null) {
                sender.sendMessage(ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
                return true;
            }
            if (faction instanceof ClaimableFaction) {
                final ClaimableFaction claimableFaction = (ClaimableFaction)faction;
                if (!claimableFaction.removeClaims(claimableFaction.getClaims(), sender)) {
                    sender.sendMessage(ChatColor.RED + "Potentially failed to remove claims.");
                }
                if (claimableFaction instanceof PlayerFaction) {
                    ((PlayerFaction)claimableFaction).broadcast(String.valueOf(ChatColor.GOLD.toString()) + ChatColor.BOLD + "Your claims have been forcefully wiped by " + sender.getName() + '.');
                }
            }
            sender.sendMessage(String.valueOf(ChatColor.GOLD.toString()) + ChatColor.BOLD + "Claims belonging to " + faction.getName() + " have been forcefully wiped.");
            return true;
        }
    }
    
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        if (args[1].isEmpty()) {
            return null;
        }
        final Player player = (Player)sender;
        final ArrayList<String> results = new ArrayList<String>(this.plugin.getFactionManager().getFactionNameMap().keySet());
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (player.canSee(target)) {
                if (!results.contains(target.getName())) {
                    results.add(target.getName());
                }
            }
        }
        return results;
    }
    
    private static class ClaimClearAllPrompt extends StringPrompt
    {
        private final Main plugin;
        
        public ClaimClearAllPrompt(final Main plugin) {
            this.plugin = plugin;
        }
        
        public String getPromptText(final ConversationContext context) {
            return ChatColor.YELLOW + "Are you sure you want to do this? " + ChatColor.RED + ChatColor.BOLD + "All claims" + ChatColor.YELLOW + " will be cleared. Type " + ChatColor.GREEN + "yes" + ChatColor.YELLOW + " to confirm or " + ChatColor.RED + "no" + ChatColor.YELLOW + " to deny.";
        }
        
        public Prompt acceptInput(final ConversationContext context, final String string) {
            String lowerCase3;
            String lowerCase2 = lowerCase3 = string.toLowerCase();
            String s;
            switch (s = lowerCase2) {
                case "no": {
                    context.getForWhom().sendRawMessage(ChatColor.BLUE + "Cancelled the process of clearing all faction claims.");
                    return Prompt.END_OF_CONVERSATION;
                }
                case "yes": {
                    for (final Faction faction : this.plugin.getFactionManager().getFactions()) {
                        if (!(faction instanceof ClaimableFaction)) {
                            continue;
                        }
                        final ClaimableFaction claimableFaction = (ClaimableFaction)faction;
                        claimableFaction.removeClaims(claimableFaction.getClaims(), (CommandSender)Bukkit.getConsoleSender());
                    }
                    final Conversable conversable;
                    Bukkit.broadcastMessage(String.valueOf(ChatColor.GOLD.toString()) + ChatColor.BOLD + "All claims have been cleared" + (((conversable = context.getForWhom()) instanceof CommandSender) ? (" by " + ((CommandSender)conversable).getName()) : "") + '.');
                    return Prompt.END_OF_CONVERSATION;
                }
                default:
                    break;
            }
            context.getForWhom().sendRawMessage(ChatColor.RED + "Unrecognized response. Process of clearing all faction claims cancelled.");
            return Prompt.END_OF_CONVERSATION;
        }
    }
}
