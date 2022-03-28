package me.danidev.core.managers.faction.argument.staff;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.managers.MessageManager;
import me.danidev.core.utils.Utils;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.faction.type.PlayerFaction;

public class FactionRemovePointsArgument extends CommandArgument {
	
    public FactionRemovePointsArgument() {
        super("removepoints", "Remove faction points.");
        this.permission = "fhcf.commands.faction.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return "/" + label + ' ' + this.getName() + " <factionName> <points>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 3) {
            sender.sendMessage(CC.translate("&cCorrect Usage: /f removepoints <factionName> <points>."));
            return true;
        }
        final int newPoints = Integer.valueOf(args[2]);
        if (newPoints < 0) {
            sender.sendMessage(CC.translate("&cInvalid number"));
            return true;
        }
        final Faction faction = Main.get().getFactionManager().getContainingFaction(args[1]);
        if (faction == null) {
            sender.sendMessage(Utils.faction_not_found);
            return true;
        }
        if (!(faction instanceof PlayerFaction)) {
            sender.sendMessage(CC.translate("&cThis others of faction does not use points."));
            return true;
        }
        final PlayerFaction playerFaction = (PlayerFaction)faction;
        final int previousPoints = playerFaction.getPoints();
        playerFaction.setPoints(previousPoints - newPoints);
        MessageManager.sendMessage(CC.translate("&a&l" + sender.getName() + "&asuccsessfully removed &l" + newPoints + " &eto the faction &f" + faction.getName() + "&e."), "fhcf.admin");
        return true;
    }
}
