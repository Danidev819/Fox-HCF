package me.danidev.core.managers.faction.argument.staff;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.managers.MessageManager;
import me.danidev.core.utils.Utils;
import me.danidev.core.utils.command.CommandArgument;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.faction.type.PlayerFaction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class FactionSetPointsArgument extends CommandArgument {

    public FactionSetPointsArgument() {
        super("setpoints", "Set faction points.");
        this.permission = "fhcf.commands.faction.argument." + this.getName();
    }

    public String getUsage(final String label) {
        return "/" + label + ' ' + this.getName() + " <factionName> <points>";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 3) {
            sender.sendMessage(CC.translate("&cCorrect Usage: /f setpoints <factionName> <points>."));
            return true;
        }
        final int newPoints = Integer.parseInt(args[2]);
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
        final PlayerFaction playerFaction = (PlayerFaction) faction;
        playerFaction.setPoints(newPoints);
        MessageManager.sendMessage(CC.translate("&a&l" + sender.getName() + " &esuccsessfully set &c" + newPoints + " &epoints to the faction &9" + faction.getName() + "&e."), "fhcf.admin");
        return true;
    }
}
