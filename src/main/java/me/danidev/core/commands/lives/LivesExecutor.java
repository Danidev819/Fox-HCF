package me.danidev.core.commands.lives;

import me.danidev.core.Main;
import me.danidev.core.utils.command.ArgumentExecutor;
import me.danidev.core.commands.lives.argument.LivesCheckArgument;
import me.danidev.core.commands.lives.argument.LivesClearDeathbanArgument;
import me.danidev.core.commands.lives.argument.LivesGiveArgument;
import me.danidev.core.commands.lives.argument.LivesSetArgument;
import me.danidev.core.commands.lives.argument.LivesSetDeathbanTimeArgument;

public class LivesExecutor extends ArgumentExecutor {

	public LivesExecutor(Main plugin) {
		super("lives");
		this.addArgument(new LivesCheckArgument());
		this.addArgument(new LivesClearDeathbanArgument());
		this.addArgument(new LivesGiveArgument());
		this.addArgument(new LivesSetArgument());
		this.addArgument(new LivesSetDeathbanTimeArgument());

		plugin.getCommand("lives").setExecutor(this);
	}
}
