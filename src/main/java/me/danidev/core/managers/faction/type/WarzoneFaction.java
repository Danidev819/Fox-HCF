package me.danidev.core.managers.faction.type;

import me.danidev.core.utils.service.ConfigurationService;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class WarzoneFaction extends Faction {

    public WarzoneFaction() {
        super("Warzone");
    }
    
    public WarzoneFaction(final Map<String, Object> map) {
        super(map);
    }
    
    @Override
    public String getDisplayName(final CommandSender sender) {
        return ConfigurationService.WARZONE_COLOR + this.getName();
    }
}
