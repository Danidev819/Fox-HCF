package me.danidev.core.managers.faction.struct;

import me.danidev.core.utils.service.ConfigurationService;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Locale;

public enum ChatChannel {

    FACTION("FACTION", 0, "Faction"), 
    ALLIANCE("ALLIANCE", 1, "Alliance"), 
    PUBLIC("PUBLIC", 2, "Public");
    
    private final String name;
    
    private ChatChannel(final String s, final int n, final String name) {
        this.name = name;
    }
    
    public static ChatChannel parse(final String id) {
        return parse(id, ChatChannel.PUBLIC);
    }
    
    public static ChatChannel parse(String id, final ChatChannel def) {
        final String lowerCase;
        final String s = lowerCase = (id = id.toLowerCase(Locale.ENGLISH));
        final String s2;
        switch ((s2 = s).hashCode()) {
            case -1243020381: {
                if (!s2.equals("global")) {
                    return (def == null) ? null : def.getRotation();
                }
                return ChatChannel.PUBLIC;
            }
            case -1091888612: {
                if (!s2.equals("faction")) {
                    return (def == null) ? null : def.getRotation();
                }
                break;
            }
            case -977423767: {
                if (!s2.equals("public")) {
                    return (def == null) ? null : def.getRotation();
                }
                return ChatChannel.PUBLIC;
            }
            case 97: {
                if (!s2.equals("a")) {
                    return (def == null) ? null : def.getRotation();
                }
                return ChatChannel.ALLIANCE;
            }
            case 102: {
                if (!s2.equals("f")) {
                    return (def == null) ? null : def.getRotation();
                }
                break;
            }
            case 103: {
                if (!s2.equals("g")) {
                    return (def == null) ? null : def.getRotation();
                }
                return ChatChannel.PUBLIC;
            }
            case 112: {
                if (!s2.equals("p")) {
                    return (def == null) ? null : def.getRotation();
                }
                return ChatChannel.PUBLIC;
            }
            case 3106: {
                if (!s2.equals("ac")) {
                    return (def == null) ? null : def.getRotation();
                }
                return ChatChannel.ALLIANCE;
            }
            case 3261: {
                if (!s2.equals("fc")) {
                    return (def == null) ? null : def.getRotation();
                }
                break;
            }
            case 3292: {
                if (!s2.equals("gc")) {
                    return (def == null) ? null : def.getRotation();
                }
                return ChatChannel.PUBLIC;
            }
            case 3571: {
                if (!s2.equals("pc")) {
                    return (def == null) ? null : def.getRotation();
                }
                return ChatChannel.PUBLIC;
            }
            case 101128: {
                if (!s2.equals("fac")) {
                    return (def == null) ? null : def.getRotation();
                }
                break;
            }
            case 111357: {
                if (!s2.equals("pub")) {
                    return (def == null) ? null : def.getRotation();
                }
                return ChatChannel.PUBLIC;
            }
            case 2996984: {
                if (!s2.equals("ally")) {
                    return (def == null) ? null : def.getRotation();
                }
                return ChatChannel.ALLIANCE;
            }
            case 3135084: {
                if (!s2.equals("fact")) {
                    return (def == null) ? null : def.getRotation();
                }
                break;
            }
            case 107017530: {
                if (!s2.equals("publi")) {
                    return (def == null) ? null : def.getRotation();
                }
                return ChatChannel.PUBLIC;
            }
            case 1806944311: {
                if (!s2.equals("alliance")) {
                    return (def == null) ? null : def.getRotation();
                }
                return ChatChannel.ALLIANCE;
            }
        }
        return ChatChannel.FACTION;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getDisplayName() {
        String prefix = null;
        switch (this) {
            case FACTION: {
                prefix = ConfigurationService.TEAMMATE_COLOR.toString();
                break;
            }
            case ALLIANCE: {
                prefix = ConfigurationService.ALLY_COLOR.toString();
                break;
            }
            default: {
                prefix = ConfigurationService.ENEMY_COLOR.toString();
                break;
            }
        }
        return String.valueOf(prefix) + this.name;
    }
    
    public String getShortName() {
        switch (this) {
            case FACTION: {
                return "FC";
            }
            case ALLIANCE: {
                return "AC";
            }
            default: {
                return "PC";
            }
        }
    }
    
    public ChatChannel getRotation() {
        switch (this) {
            case FACTION: {
                return ChatChannel.PUBLIC;
            }
            case PUBLIC: {
                return ChatChannel.ALLIANCE;
            }
            case ALLIANCE: {
                return ChatChannel.FACTION;
            }
            default: {
                return ChatChannel.PUBLIC;
            }
        }
    }
    
    public String getRawFormat(final Player player) {
        switch (this) {
            case FACTION: {
                return ConfigurationService.TEAMMATE_COLOR + "(" + this.getDisplayName() + ConfigurationService.TEAMMATE_COLOR + ") " + player.getName() + ChatColor.GRAY + ": " + ChatColor.YELLOW + "%2$s";
            }
            case ALLIANCE: {
                return ConfigurationService.ALLY_COLOR + "(" + this.getDisplayName() + ConfigurationService.ALLY_COLOR + ") " + player.getName() + ChatColor.GRAY + ": " + ChatColor.YELLOW + "%2$s";
            }
            default: {
                throw new IllegalArgumentException("Cannot get the raw format for public chat channel");
            }
        }
    }
}
