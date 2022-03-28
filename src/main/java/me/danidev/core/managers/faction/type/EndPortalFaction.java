package me.danidev.core.managers.faction.type;

import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import java.util.Map;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class EndPortalFaction extends ClaimableFaction implements ConfigurationSerializable
{
    public EndPortalFaction(final String name) {
        super(name);
    }
    
    public EndPortalFaction(final Map<String, Object> map) {
        super(map);
    }
    
    public String getDisplayName(final CommandSender sender) {
        return ChatColor.DARK_AQUA + "EndPortal";
    }
    
    public boolean isDeathban() {
        return true;
    }
    
    public static class EndPortalFaction1 extends EndPortalFaction implements ConfigurationSerializable
    {
        public EndPortalFaction1() {
            super("EndPortal1");
            for (final World world : Bukkit.getWorlds()) {
                world.getEnvironment();
            }
        }
        
        public EndPortalFaction1(final Map<String, Object> map) {
            super(map);
        }
    }
    
    public static class EndPortalFaction2 extends EndPortalFaction implements ConfigurationSerializable
    {
        public EndPortalFaction2() {
            super("EndPortal2");
            for (final World world : Bukkit.getWorlds()) {
                world.getEnvironment();
            }
        }
        
        public EndPortalFaction2(final Map<String, Object> map) {
            super(map);
        }
    }
    
    public static class EndPortalFaction3 extends EndPortalFaction implements ConfigurationSerializable
    {
        public EndPortalFaction3() {
            super("EndPortal3");
            for (final World world : Bukkit.getWorlds()) {
                world.getEnvironment();
            }
        }
        
        public EndPortalFaction3(final Map<String, Object> map) {
            super(map);
        }
    }
    
    public static class EndPortalFaction4 extends EndPortalFaction implements ConfigurationSerializable
    {
        public EndPortalFaction4() {
            super("EndPortal4");
            for (final World world : Bukkit.getWorlds()) {
                world.getEnvironment();
            }
        }
        
        public EndPortalFaction4(final Map<String, Object> map) {
            super(map);
        }
    }
}
