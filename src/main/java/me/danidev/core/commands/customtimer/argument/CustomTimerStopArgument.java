package me.danidev.core.commands.customtimer.argument;

import me.danidev.core.Main;
import me.danidev.core.managers.customtimer.CustomTimer;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CustomTimerStopArgument extends CommandArgument {

    public CustomTimerStopArgument() {
        super("stop", "Custom Timer Stop");
    }

    @Override
    public String getUsage(String label) {
        return "/" + label + " " + this.getName() + " <name>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + this.getUsage(command.getLabel()));
            return true;
        }

        CustomTimer timer = Main.get().getCustomTimerManager().getCustomTimer(args[1]);

        if (timer == null) {
            sender.sendMessage(CC.translate("&cThis timer is not currently active."));
            return true;
        }

        timer.cancel();
        return false;
    }
}
