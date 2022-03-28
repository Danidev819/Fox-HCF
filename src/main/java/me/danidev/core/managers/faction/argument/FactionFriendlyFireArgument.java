package me.danidev.core.managers.faction.argument;

import me.danidev.core.Main;
import me.danidev.core.utils.command.CommandArgument;
import me.danidev.core.managers.faction.FactionMember;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.danidev.core.managers.faction.struct.Role;
import me.danidev.core.managers.faction.type.PlayerFaction;

public  class FactionFriendlyFireArgument extends CommandArgument {

    private final Main plugin;

    public FactionFriendlyFireArgument(Main plugin) {
        super("friendlyfire", "Toggle faction pvp.", new String[]{"ff"});
        this.plugin = plugin;
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName();
    }
    
	@SuppressWarnings("deprecation")
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This commands is only executable by players.");
            return true;
        }
        Player player = (Player) sender;
        PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);

        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        FactionMember factionMember = playerFaction.getMember(player.getUniqueId());

        if (factionMember.getRole() == Role.MEMBER) {
            sender.sendMessage(ChatColor.RED + "You must be a faction caption to do this.");
            return true;
        }
        boolean newFriendlyFire = !playerFaction.isFriendlyFire();
        playerFaction.setFriendlyFire(newFriendlyFire);
        playerFaction.broadcast(ChatColor.YELLOW  + "Friendly Fire has been " + (newFriendlyFire ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled") + ChatColor.YELLOW + " by " + ChatColor.LIGHT_PURPLE + sender.getName());
		return true;
	}
}
	
