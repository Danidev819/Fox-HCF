package me.danidev.core.commands.apply.argument;

import me.danidev.core.handlers.ApplyHandler;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ApplyGlobalArgument extends CommandArgument {

    public ApplyGlobalArgument() {
        super("global", "Toggle Global Applies");
    }

    @Override
    public String getUsage(String label) {
        return "/" + label + " " + this.getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ApplyHandler.setGlobal(!ApplyHandler.isGlobal());
        sender.sendMessage(CC.translate("&eGlobal Applies has been set &c" + ApplyHandler.isGlobal() + "&e."));
        return true;
    }
}
