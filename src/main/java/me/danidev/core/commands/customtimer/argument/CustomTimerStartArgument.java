package me.danidev.core.commands.customtimer.argument;

import me.danidev.core.Main;
import me.danidev.core.managers.customtimer.CustomTimer;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.JavaUtils;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CustomTimerStartArgument extends CommandArgument {

    public CustomTimerStartArgument() {
        super("start", "Custom Timer Start");
    }

    @Override
    public String getUsage(String label) {
        return "/" + label + " " + this.getName() + " <name> <prefix> <time>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(ChatColor.RED + this.getUsage(command.getLabel()));
            return true;
        }

        if (Main.get().getCustomTimerManager().getCustomTimer(args[1]) != null) {
            sender.sendMessage(CC.translate("&cA timer with this name already exists."));
            return true;
        }

        long duration = JavaUtils.parse(args[3]);

        if (duration == -1L) {
            sender.sendMessage(CC.translate("&c" + args[3] + " is an invalid duration."));
            return true;
        }

        if (duration < 1000L) {
            sender.sendMessage(CC.translate("&cThe timer must last for at least 20 ticks."));
            return true;
        }

        Main.get().getCustomTimerManager().createTimer(new CustomTimer(args[1], args[2].replace("-", " "), System.currentTimeMillis(), System.currentTimeMillis() + duration));
        sender.sendMessage(CC.translate("&aThe custom timer has been created."));
        return false;
    }
}
