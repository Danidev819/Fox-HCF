package me.danidev.core.commands.apply.argument;

import me.danidev.core.handlers.ApplyHandler;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ApplyHosterArgument extends CommandArgument {

    public ApplyHosterArgument() {
        super("hoster", "Toggle Hoster Applies");
    }

    @Override
    public String getUsage(String label) {
        return "/" + label + " " + this.getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ApplyHandler.setHoster(!ApplyHandler.isHoster());
        sender.sendMessage(CC.translate("&eHoster Apply has been set &c" + ApplyHandler.isHoster() + "&e."));
        return true;
    }
}
