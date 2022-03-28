package me.danidev.core.commands.apply;

import me.danidev.core.Main;
import me.danidev.core.commands.apply.argument.*;
import me.danidev.core.utils.command.ArgumentExecutor;

public class ApplyExecutor extends ArgumentExecutor {

    public ApplyExecutor(Main plugin) {
        super("apply");

        this.addArgument(new ApplyGlobalArgument());
        this.addArgument(new ApplyHosterArgument());
        this.addArgument(new ApplyStaffArgument());
        this.addArgument(new ApplyWebsiteArgument());
        this.addArgument(new ApplyDeveloperArgument());

        plugin.getCommand("apply").setExecutor(this);
    }
}
