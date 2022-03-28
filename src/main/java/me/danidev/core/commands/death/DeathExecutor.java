package me.danidev.core.commands.death;

import me.danidev.core.Main;
import me.danidev.core.commands.death.argument.DeathInfoArgument;
import me.danidev.core.commands.death.argument.DeathRefundArgument;
import me.danidev.core.utils.command.ArgumentExecutor;
import me.danidev.core.commands.death.argument.DeathReviveArgument;

public class DeathExecutor extends ArgumentExecutor {

    public DeathExecutor(Main plugin) {
        super("death");

        this.addArgument(new DeathInfoArgument());
        this.addArgument(new DeathRefundArgument());
        this.addArgument(new DeathReviveArgument());

        plugin.getCommand("death").setExecutor(this);
    }
}
