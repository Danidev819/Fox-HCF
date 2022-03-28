package me.danidev.core.managers.lunar;

import me.danidev.core.managers.faction.type.PlayerFaction;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WaypointManager {

    public void createFactionWaypoint(PlayerFaction playerFaction, String name, Location location, int color) {
        for (Player members : playerFaction.getOnlinePlayers()) {
            if (LunarClientAPI.getInstance().isRunningLunarClient(members)) {
                LunarClientAPI.getInstance().sendWaypoint(members, getWaypoint(name, location, color));
            }
        }
    }

    public void deleteFactionWaypoint(PlayerFaction playerFaction, String name, Location location, int color) {
        for (Player members : playerFaction.getOnlinePlayers()) {
            if (LunarClientAPI.getInstance().isRunningLunarClient(members)) {
                LunarClientAPI.getInstance().removeWaypoint(members, getWaypoint(name, location, color));
            }
        }
    }

    public void createWaypoint(String name, Location location, int color) {
        Bukkit.getOnlinePlayers().forEach(online -> {
            if (LunarClientAPI.getInstance().isRunningLunarClient(online)) {
                LunarClientAPI.getInstance().sendWaypoint(online, getWaypoint(name, location, color));
            }
        });
    }

    public void deleteWaypoint(String name, Location location, int color) {
        Bukkit.getOnlinePlayers().forEach(online -> {
            if (LunarClientAPI.getInstance().isRunningLunarClient(online)) {
                LunarClientAPI.getInstance().removeWaypoint(online, getWaypoint(name, location, color));
            }
        });
    }

    public void joinWaypoint(Player player, String name, Location location, int color) {
        LunarClientAPI.getInstance().sendWaypoint(player, getWaypoint(name, location, color));
    }

    public LCWaypoint getWaypoint(String name, Location location, int color) {
        return new LCWaypoint(name, location, color, true, true);
    }
}
