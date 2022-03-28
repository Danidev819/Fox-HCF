package me.danidev.core.listeners;

import me.danidev.core.Main;
import me.danidev.core.managers.faction.event.PlayerLeftFactionEvent;
import me.danidev.core.managers.faction.type.PlayerFaction;
import me.danidev.core.providers.NametagProvider;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.JavaUtils;
import me.danidev.core.utils.Utils;
import me.danidev.core.utils.nametags.BufferedNametag;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketTeammates;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LunarListener implements Listener {

    private final Map<UUID, Integer> TASK = Maps.newHashMap();

    public LunarListener(Main plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        PlayerFaction faction = Main.get().getFactionManager().getPlayerFaction(player.getUniqueId());

        if (faction != null && faction.getHome() != null) {
            Main.get().getWaypointManager().joinWaypoint(player, "Home", faction.getHome(), -16776961);
        }

        if (faction != null && faction.getFocused() != null && faction.getFocused().getHome() != null) {
            Main.get().getWaypointManager().joinWaypoint(player, faction.getFocused().getName(),
                    faction.getFocused().getHome(), -65536);
        }

        Main.get().getWaypointManager().joinWaypoint(player, "Spawn", Bukkit.getWorld("world").getSpawnLocation(),
                -16711936);

        if (Utils.destringifyLocation(Main.get().getLocationsConfig().getString("END_EXIT")) != null) {
            Main.get().getWaypointManager().joinWaypoint(player, "End Exit",
                    Utils.destringifyLocation(Main.get().getLocationsConfig().getString("END_EXIT")), -65281);
        }

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                PlayerFaction playerFaction = Main.get().getFactionManager().getPlayerFaction(player.getUniqueId());

                if (playerFaction != null) {
                    UUID leader = playerFaction.getLeader().getUniqueId();

                    Map<UUID, Map<String, Double>> positions = new HashMap<>();
                    playerFaction.getOnlinePlayers().forEach(member -> {
                        UUID uuid = member.getUniqueId();
                        Map<String, Double> position = positions.computeIfAbsent(uuid, id -> new HashMap<>());

                        position.put("x", member.getLocation().getX());
                        position.put("y", member.getLocation().getY());
                        position.put("z", member.getLocation().getZ());
                    });

                    LCPacketTeammates teammates = new LCPacketTeammates(leader, System.currentTimeMillis(), positions);

                    playerFaction.getOnlinePlayers()
                            .forEach(member -> LunarClientAPI.getInstance().sendTeammates(player, teammates));
                }

                for (Player online : Bukkit.getOnlinePlayers()) {
                    List<String> lunarNametag = Lists.newArrayList();

                    if (playerFaction != null) {
                        String DTR = playerFaction.getDtrColourString()
                                + JavaUtils.format(playerFaction.getDeathsUntilRaidable(false))
                                + playerFaction.getRegenStatus().getSymbol();
                        lunarNametag.add(Main.get().getMainConfig().getString("LUNAR_NAMETAG.FORMAT")
                                .replace("%FACTION%", playerFaction.getName()).replace("%DTR%", DTR));
                    }

                    BufferedNametag playerNametag = NametagProvider.getNametag(player, online);

                    lunarNametag.add(playerNametag.getPrefix() + player.getName());

                    LunarClientAPI.getInstance().overrideNametag(player, CC.translate(lunarNametag), online);
                }
            }
        };

        runnable.runTaskTimerAsynchronously(Main.get(), 20L * 2, 20L * 2);
        TASK.put(player.getUniqueId(), runnable.getTaskId());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        TASK.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        TASK.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onLeaveFaction(PlayerLeftFactionEvent event) {
        TASK.remove(event.getPlayer().get().getUniqueId());
    }
}
