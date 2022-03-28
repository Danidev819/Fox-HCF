package me.danidev.core.managers.timer;

import me.danidev.core.Main;
import me.danidev.core.managers.timer.argument.TimerCheckArgument;
import me.danidev.core.managers.timer.argument.TimerSetArgument;
import me.danidev.core.utils.command.ArgumentExecutor;

public class TimerExecutor extends ArgumentExecutor {

	public TimerExecutor(Main plugin) {
		super("timer");

		this.addArgument(new TimerCheckArgument());
		this.addArgument(new TimerSetArgument());

		plugin.getCommand("timer").setExecutor(this);
	}
}
