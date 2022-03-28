package me.danidev.core.commands.customtimer;

import me.danidev.core.Main;
import me.danidev.core.commands.customtimer.argument.CustomTimerListArgument;
import me.danidev.core.commands.customtimer.argument.CustomTimerStartArgument;
import me.danidev.core.commands.customtimer.argument.CustomTimerStopArgument;
import me.danidev.core.utils.command.ArgumentExecutor;

public class CustomTimerExecutor extends ArgumentExecutor {

    public CustomTimerExecutor(Main plugin) {
        super("customtimer");

        this.addArgument(new CustomTimerStartArgument());
        this.addArgument(new CustomTimerStopArgument());
        this.addArgument(new CustomTimerListArgument());

        plugin.getCommand("customtimer").setExecutor(this);
    }
}
