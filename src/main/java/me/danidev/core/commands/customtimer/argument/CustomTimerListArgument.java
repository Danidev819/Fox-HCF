package me.danidev.core.commands.customtimer.argument;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CustomTimerListArgument extends CommandArgument {

    public CustomTimerListArgument() {
        super("list", "Custom Timer List");
    }

    @Override
    public String getUsage(String label) {
        return "/" + label + " " + this.getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(CC.translate("&7&m----------------------"));
        sender.sendMessage(CC.translate("&6&lCustom Timer List"));
        sender.sendMessage(CC.translate(""));

        if (Main.get().getCustomTimerManager().getCustomTimers().isEmpty()) {
            sender.sendMessage(CC.translate("&cNone"));
        }
        else {
            Main.get().getCustomTimerManager().getCustomTimers().forEach(timer ->
                    sender.sendMessage(CC.translate(" &7- &b" + timer.getName())));
        }

        sender.sendMessage(CC.translate("&7&m----------------------"));
        return true;
    }
}
