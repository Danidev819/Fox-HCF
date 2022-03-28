package me.danidev.core.managers.faction.type;

import me.danidev.core.utils.service.ConfigurationService;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class WildernessFaction extends Faction {

    public WildernessFaction() {
        super("Wilderness");
    }
    
    public WildernessFaction(final Map<String, Object> map) {
        super(map);
    }
    
    @Override
    public String getDisplayName(final CommandSender sender) {
        return ConfigurationService.WILDERNESS_COLOR + this.getName();
    }
}
