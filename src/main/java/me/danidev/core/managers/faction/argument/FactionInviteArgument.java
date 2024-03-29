package me.danidev.core.managers.faction.argument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import me.danidev.core.Main;
import me.danidev.core.utils.chat.ClickAction;
import me.danidev.core.utils.chat.Text;
import me.danidev.core.utils.command.CommandArgument;
import me.danidev.core.managers.faction.struct.Relation;
import me.danidev.core.managers.faction.struct.Role;
import me.danidev.core.managers.faction.type.PlayerFaction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.regex.Pattern;

public class FactionInviteArgument extends CommandArgument {
	
    private static final Pattern USERNAME_REGEX;
    private final Main plugin;
    
    static {
        USERNAME_REGEX = Pattern.compile("^[a-zA-Z0-9_]{2,16}$");
    }
    
    public FactionInviteArgument(final Main plugin) {
        super("invite", "Invite a player to your faction.", new String[]{"inv"});
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return "/" + label + ' ' + this.getName() + " <playerName>";
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can invite to a faction.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        if (!FactionInviteArgument.USERNAME_REGEX.matcher(args[1]).matches()) {
            sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is an invalid username.");
            return true;
        }
        final Player player = (Player)sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            sender.sendMessage(ChatColor.RED + "You must a faction officer to invite members.");
            return true;
        }
        final Set<String> invitedPlayerNames = playerFaction.getInvitedPlayerNames();
        String name = args[1];
        if (playerFaction.getMember(name) != null) {
            sender.sendMessage(ChatColor.RED + "'" + name + "' is already in your faction.");
            return true;
        }
        if (!this.plugin.getEotwHandler().isEndOfTheWorld() && playerFaction.isRaidable()) {
            sender.sendMessage(ChatColor.RED + "You may not invite players whilst your faction is raidable.");
            return true;
        }
        if (!invitedPlayerNames.add(name)) {
            sender.sendMessage(ChatColor.RED + name + " has already been invited.");
            return true;
        }
        final Player target = Bukkit.getPlayer(name);
        if (target != null) {
            name = target.getName();
            final Text text = new Text(sender.getName()).setColor(Relation.ENEMY.toChatColour()).append(new Text(" has invited you to join ").setColor(ChatColor.YELLOW));
            text.append(new Text(playerFaction.getName()).setColor(Relation.ENEMY.toChatColour())).append(new Text(". ").setColor(ChatColor.YELLOW));
            text.append(new Text("Click here").setColor(ChatColor.GREEN).setClick(ClickAction.RUN_COMMAND, "/" + label + " accept " + playerFaction.getName()).setHoverText(ChatColor.AQUA + "Click to join " + playerFaction.getDisplayName(target) + ChatColor.AQUA + '.')).append(new Text(" to accept this invitation.").setColor(ChatColor.YELLOW));
            text.send(target);
        }
        playerFaction.broadcast(Relation.MEMBER.toChatColour() + sender.getName() + ChatColor.YELLOW + " has invited " + Relation.ENEMY.toChatColour() + name + ChatColor.YELLOW + " to the faction.");
        return true;
    }
    
    @SuppressWarnings("deprecation")
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        final Player player = (Player)sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null || playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            return Collections.emptyList();
        }
        final ArrayList<String> results = new ArrayList<String>();
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (player.canSee(target) && !results.contains(target.getName())) {
                final PlayerFaction targetFaction;
                if ((targetFaction = this.plugin.getFactionManager().getPlayerFaction(target.getUniqueId())) == null || !targetFaction.equals(playerFaction)) {
                    results.add(target.getName());
                }
            }
        }
        return results;
    }
}
