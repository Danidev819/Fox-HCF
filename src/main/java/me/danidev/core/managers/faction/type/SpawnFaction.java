package me.danidev.core.managers.faction.type;

import java.util.Map;

import me.danidev.core.utils.service.ConfigurationService;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class SpawnFaction extends ClaimableFaction implements ConfigurationSerializable {

    public SpawnFaction() {
        super("Spawn");
        this.safezone = true;
        for (final World world : Bukkit.getWorlds()) {
            final World.Environment environment = world.getEnvironment();
            if (environment == World.Environment.THE_END) {
                continue;
            }
            ConfigurationService.SPAWN_RADIUS.get(world.getEnvironment());
        }
    }
    
    public SpawnFaction(final Map<String, Object> map) {
        super(map);
    }
    
    public boolean isDeathban() {
        return false;
    }
}
