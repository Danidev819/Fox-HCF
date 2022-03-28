package me.danidev.core.commands.apply.argument;

import me.danidev.core.handlers.ApplyHandler;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ApplyDeveloperArgument extends CommandArgument {

    public ApplyDeveloperArgument() {
        super("developer", "Toggle Developer Applies");
    }

    @Override
    public String getUsage(String label) {
        return "/" + label + " " + this.getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ApplyHandler.setDeveloper(!ApplyHandler.isDeveloper());
        sender.sendMessage(CC.translate("&eDeveloper Applies has been set &c" + ApplyHandler.isDeveloper() + "&e."));
        return true;
    }
}
