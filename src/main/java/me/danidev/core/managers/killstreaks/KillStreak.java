package me.danidev.core.managers.killstreaks;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map;

public class KillStreak {

    private static Map<UUID, Integer> killStreak;
    
    static {
        KillStreak.killStreak = new HashMap<>();
    }
    
    public static int getKillStreak(final Player player) {
        if (hasKillStreak(player)) {
            return KillStreak.killStreak.get(player.getUniqueId());
        }
        return 0;
    }
    
    public static boolean hasKillStreak(final Player player) {
        return KillStreak.killStreak.containsKey(player.getUniqueId()) && KillStreak.killStreak.get(player.getUniqueId()) >= 1;
    }
    
    public static void incrementKillStreak(final Player player) {
        final Integer killsOnStreak = KillStreak.killStreak.get(player.getUniqueId());
        if (hasKillStreak(player)) {
            KillStreak.killStreak.put(player.getUniqueId(), killsOnStreak + 1);
        }
        else {
            KillStreak.killStreak.put(player.getUniqueId(), 1);
        }
    }
    
    public static void removeKillStreak(final Player player) {
        KillStreak.killStreak.put(player.getUniqueId(), 0);
        KillStreak.killStreak.remove(player.getUniqueId());
    }
}
