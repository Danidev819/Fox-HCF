package me.danidev.core.commands.koth;

import me.danidev.core.Main;
import me.danidev.core.commands.koth.argument.KothSetCapDelayArgument;
import me.danidev.core.utils.command.ArgumentExecutor;

public class KothExecutor extends ArgumentExecutor {

	public KothExecutor(Main plugin) {
		super("koth");

		this.addArgument(new KothSetCapDelayArgument(plugin));

		plugin.getCommand("koth").setExecutor(this);
	}
}
