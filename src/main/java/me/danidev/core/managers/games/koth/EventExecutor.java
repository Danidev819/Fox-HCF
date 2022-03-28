package me.danidev.core.managers.games.koth;

import me.danidev.core.Main;
import me.danidev.core.managers.games.citadel.EventSetCapzone;
import me.danidev.core.utils.command.ArgumentExecutor;
import me.danidev.core.managers.games.koth.argument.EventCancelArgument;
import me.danidev.core.managers.games.koth.argument.EventCreateArgument;
import me.danidev.core.managers.games.koth.argument.EventDeleteArgument;
import me.danidev.core.managers.games.koth.argument.EventListArgument;
import me.danidev.core.managers.games.koth.argument.EventRenameArgument;
import me.danidev.core.managers.games.koth.argument.EventSetAreaArgument;
import me.danidev.core.managers.games.koth.argument.EventSetCapzoneArgument;
import me.danidev.core.managers.games.koth.argument.EventStartArgument;
import me.danidev.core.managers.games.koth.argument.EventUptimeArgument;

public class EventExecutor extends ArgumentExecutor {
	
    public EventExecutor(Main plugin) {
        super("event");

        this.addArgument(new EventListArgument(plugin));
        this.addArgument(new EventCancelArgument(plugin));
        this.addArgument(new EventCreateArgument(plugin));
        this.addArgument(new EventDeleteArgument(plugin));
        this.addArgument(new EventRenameArgument(plugin));
        this.addArgument(new EventSetAreaArgument(plugin));
        this.addArgument(new EventSetCapzoneArgument(plugin));
        this.addArgument(new EventStartArgument(plugin));
        this.addArgument(new EventUptimeArgument(plugin));
        this.addArgument(new EventSetCapzone(plugin));

        plugin.getCommand("event").setExecutor(this);
    }
}
