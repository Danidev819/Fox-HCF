package me.danidev.core.managers.games.citadel;

import me.danidev.core.Main;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.games.koth.CaptureZone;
import me.danidev.core.utils.CC;

import me.danidev.core.utils.JavaUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CitadelCommand implements CommandExecutor {
    
    public CitadelCommand(Main plugin) {
        plugin.getCommand("citadel").setExecutor(this);
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	if (!sender.hasPermission("fhcf.command.citadel.argument.setcapdelay")) {
    		sender.sendMessage(CC.translate("&cNo permissions."));
    		return true;
    	}

    	if (args.length <= 0) {
    		sender.sendMessage(CC.translate(CC.MENU_BAR));
    		sender.sendMessage(CC.translate("&6&lCitadel Commands"));
    		sender.sendMessage(CC.translate(""));
    		sender.sendMessage(CC.translate("&7/" + label + " setcapdelay <citadelName> <capDelay>"));
            sender.sendMessage(CC.translate(CC.MENU_BAR));
    		return true;
    	}
    	if (args[0].equalsIgnoreCase("setcapdelay")) {
    		if (args.length < 3) {
                sender.sendMessage(CC.translate("&cUsage: /" + label + " setcapdelay <citadelName> <capDelay>"));
                return true;
            }
            Faction faction = Main.get().getFactionManager().getFaction(args[1]);

            if (!(faction instanceof CitadelFaction)) {
                sender.sendMessage(ChatColor.RED + "There is not a Citadel arena named '" + args[1] + "'.");
                return true;
            }

            long duration = JavaUtils.parse(StringUtils.join(args, ' ', 2, args.length));

            if (duration == -1L) {
                sender.sendMessage(ChatColor.RED + "Invalid duration, use the correct format: 10m 1s");
                return true;
            }

            CitadelFaction citadelFaction = (CitadelFaction)faction;
            CaptureZone captureZone = citadelFaction.getCaptureZone();

            if (captureZone == null) {
                sender.sendMessage(ChatColor.RED + citadelFaction.getDisplayName(sender) + ChatColor.RED + " does not have a capture zone.");
                return true;
            }

            if (captureZone.isActive() && duration < captureZone.getRemainingCaptureMillis()) {
                captureZone.setRemainingCaptureMillis(duration);
            }

            captureZone.setDefaultCaptureMillis(duration);
            sender.sendMessage(ChatColor.YELLOW + "Set the capture delay of Citadel arena " + ChatColor.WHITE + citadelFaction.getDisplayName(sender) + ChatColor.YELLOW + " to " + ChatColor.WHITE + DurationFormatUtils.formatDurationWords(duration, true, true) + ChatColor.WHITE + '.');
            return true;
    	}
		return true;
    }
}
