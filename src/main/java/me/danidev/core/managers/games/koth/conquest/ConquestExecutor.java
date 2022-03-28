package me.danidev.core.managers.games.koth.conquest;

import me.danidev.core.Main;
import me.danidev.core.utils.command.ArgumentExecutor;

public class ConquestExecutor extends ArgumentExecutor {

	public ConquestExecutor(Main plugin) {
		super("conquest");

		this.addArgument(new ConquestSetPointsArgument());

		plugin.getCommand("conquest").setExecutor(this);
	}
}
