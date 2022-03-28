package me.danidev.core.managers.deathban;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import me.danidev.core.Main;
import me.danidev.core.utils.TaskUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import lombok.Getter;
import net.minecraft.util.com.google.common.collect.Maps;

public class DeathbanManager {

    @Getter
    private final Map<UUID, Deathban> deathbansMap;

    @Getter
    private Location deathbanArena = null;

    private final int AUTO_SAVE_INTERVAL = 300 * 20;
    public static final long DEFAULT_DEATHBAN_TIME = TimeUnit.HOURS.toMillis(1);

    public DeathbanManager() {
        this.deathbansMap = Maps.newConcurrentMap();

        this.load();

        TaskUtils.runTaskTimerAsynchronously(() -> this.save(), this.AUTO_SAVE_INTERVAL);
        TaskUtils.runTaskTimerAsynchronously(() -> {
            for (Map.Entry<UUID, Deathban> deathbansEntry : this.deathbansMap.entrySet()) {
                UUID uuid = deathbansEntry.getKey();
                Deathban deathban = deathbansEntry.getValue();

                if (deathban.isActive()) {
                    continue;
                }

                if (deathban.revive()) {
                    this.deathbansMap.remove(uuid);
                }
            }
        }, 20);
    }

    public void load() {

        if (Main.get().getDeathbanConfig().getConfiguration().contains("DEATHBAN_ARENA")) {
            World world = Bukkit.getWorld(Main.get().getDeathbanConfig().getString("DEATHBAN_ARENA.WORLD"));
            double x = Main.get().getDeathbanConfig().getDouble("DEATHBAN_ARENA.X");
            double y = Main.get().getDeathbanConfig().getDouble("DEATHBAN_ARENA.Y");
            double z = Main.get().getDeathbanConfig().getDouble("DEATHBAN_ARENA.Z");
            float yaw = Float.parseFloat(Main.get().getDeathbanConfig().getString("DEATHBAN_ARENA.YAW"));
            float pitch = Float.parseFloat(Main.get().getDeathbanConfig().getString("DEATHBAN_ARENA.PITCH"));

            this.deathbanArena = new Location(world, x, y, z, yaw, pitch);
        }

        this.deathbansMap.clear();

        if (!Main.get().getDeathbanConfig().getConfiguration().contains("DEATHBANS")) {
            return;
        }

        Main.get().getDeathbanConfig().getConfiguration().getConfigurationSection("DEATHBANS").getKeys(false).forEach(key -> {
            UUID deathbanned = UUID.fromString(key);
            long createdAt = Main.get().getDeathbanConfig().getLong("DEATHBANS." + key + ".CREATED_AT");
            long expiryMillis = Main.get().getDeathbanConfig().getLong("DEATHBANS." + key + ".EXPIRY_MILLIS");
            String reason = Main.get().getDeathbanConfig().getString("DEATHBANS." + key + ".REASON");

            World deathWorld = Bukkit.getWorld(Main.get().getDeathbanConfig().getString("DEATHBANS." + key + ".DEATH_LOCATION.WORLD"));
            int deathX = Main.get().getDeathbanConfig().getInt("DEATHBANS." + key + ".DEATH_LOCATION.X");
            int deathY = Main.get().getDeathbanConfig().getInt("DEATHBANS." + key + ".DEATH_LOCATION.Y");
            int deathZ = Main.get().getDeathbanConfig().getInt("DEATHBANS." + key + ".DEATH_LOCATION.Z");

            Location deathLocation = new Location(deathWorld, deathX, deathY, deathZ);

            Deathban deathban = new Deathban(deathbanned, createdAt, createdAt + expiryMillis, reason, deathLocation);

            this.deathbansMap.put(deathbanned, deathban);
        });
    }

    public void save() {
        Main.get().getDeathbanConfig().getConfiguration().set("DEATHBANS", null);

        this.deathbansMap.values().forEach(deathban -> {
            String uuid = deathban.getDeathbanned().toString();

            Main.get().getDeathbanConfig().getConfiguration().set("DEATHBANS." + uuid + ".CREATED_AT", deathban.getCreatedAt());
            Main.get().getDeathbanConfig().getConfiguration().set("DEATHBANS." + uuid + ".EXPIRY_MILLIS", deathban.getTimeleft());
            Main.get().getDeathbanConfig().getConfiguration().set("DEATHBANS." + uuid + ".REASON", deathban.getReason());

            Location loc = deathban.getDeathLocation();

            Main.get().getDeathbanConfig().getConfiguration().set("DEATHBANS." + uuid + ".DEATH_LOCATION.WORLD", loc.getWorld().getName());
            Main.get().getDeathbanConfig().getConfiguration().set("DEATHBANS." + uuid + ".DEATH_LOCATION.X", loc.getBlockX());
            Main.get().getDeathbanConfig().getConfiguration().set("DEATHBANS." + uuid + ".DEATH_LOCATION.Y", loc.getBlockY());
            Main.get().getDeathbanConfig().getConfiguration().set("DEATHBANS." + uuid + ".DEATH_LOCATION.Z", loc.getBlockZ());
        });

        Main.get().getDeathbanConfig().save();
    }

    public void setDeathbanArena(Location location) {
        this.deathbanArena = location;

        FileConfiguration locationsFile = Main.get().getLocationsConfig().getConfiguration();

        locationsFile.set("DEATHBAN_ARENA.WORLD", location.getWorld().getName());
        locationsFile.set("DEATHBAN_ARENA.X", location.getX());
        locationsFile.set("DEATHBAN_ARENA.Y", location.getY());
        locationsFile.set("DEATHBAN_ARENA.Z", location.getZ());
        locationsFile.set("DEATHBAN_ARENA.YAW", location.getYaw());
        locationsFile.set("DEATHBAN_ARENA.PITCH", location.getPitch());

        Main.get().getLocationsConfig().save();
    }

    public Deathban get(UUID uuid) {
        return this.deathbansMap.get(uuid);
    }

    public boolean isDeathbanned(UUID uuid) {
        Deathban deathban = this.get(uuid);

        return deathban != null && deathban.isActive();
    }

    public void sendToDeathbanArena(Player player) {
        if (this.deathbanArena == null) {
            return;
        }

        player.teleport(this.deathbanArena);
    }
}
