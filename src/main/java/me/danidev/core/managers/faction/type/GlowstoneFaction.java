package me.danidev.core.managers.faction.type;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import java.util.Map;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class GlowstoneFaction extends ClaimableFaction implements ConfigurationSerializable {

    public GlowstoneFaction() {
        super("Glowstone");
        this.safezone = false;
    }
    
    public GlowstoneFaction(final Map<String, Object> map) {
        super(map);
    }
    
    public String getDisplayName(final CommandSender sender) {
        return ChatColor.GOLD + this.getName().replace("Glowstone Mountain", "Glowstone Mountain");
    }
    
    public boolean isDeathban() {
        return true;
    }
}
