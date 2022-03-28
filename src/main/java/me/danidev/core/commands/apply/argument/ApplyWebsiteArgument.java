package me.danidev.core.commands.apply.argument;

import me.danidev.core.handlers.ApplyHandler;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ApplyWebsiteArgument extends CommandArgument {

    public ApplyWebsiteArgument() {
        super("website", "Toggle Website Applies");
    }

    @Override
    public String getUsage(String label) {
        return "/" + label + " " + this.getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ApplyHandler.setWebsite(!ApplyHandler.isWebsite());
        sender.sendMessage(CC.translate("&eWebsite Applies has been set &c" + ApplyHandler.isWebsite() + "&e."));
        return true;
    }
}
