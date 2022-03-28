package me.danidev.core.commands.apply.argument;

import me.danidev.core.handlers.ApplyHandler;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ApplyStaffArgument extends CommandArgument {

    public ApplyStaffArgument() {
        super("staff", "Toggle Staff Applies");
    }

    @Override
    public String getUsage(String label) {
        return "/" + label + " " + this.getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ApplyHandler.setStaff(!ApplyHandler.isStaff());
        sender.sendMessage(CC.translate("&eStaff Applies has been set &c" + ApplyHandler.isStaff() + "&e."));
        return true;
    }
}
