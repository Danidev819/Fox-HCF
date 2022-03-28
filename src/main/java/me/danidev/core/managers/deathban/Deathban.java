package me.danidev.core.managers.deathban;

import java.util.Objects;
import java.util.UUID;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Deathban {

    private UUID deathbanned;

    private long createdAt;
    private long expiryMillis;
    private String reason;

    private Location deathLocation;

    public Deathban(UUID deathbanned, String reason, Location deathLocation) {
        this.deathbanned = deathbanned;

        this.createdAt = System.currentTimeMillis();
        this.expiryMillis = this.createdAt + DeathbanManager.DEFAULT_DEATHBAN_TIME;
        this.reason = reason;
        this.deathLocation = deathLocation;
    }

    public boolean revive() {
        Player player = Bukkit.getPlayer(this.deathbanned);

        if (player == null) {
            return false;
        }

        player.teleport(player.getWorld().getSpawnLocation());
        player.sendMessage(CC.translate("&aYour deathban has finished!"));
        Main.get().getTimerManager().getProtectionTimer().setCooldown(player, player.getUniqueId(), Main.get().getTimerManager().getProtectionTimer().defaultCooldown, true);
        if (Main.get().getTimerManager().getProtectionTimer().setCooldown(player, player.getUniqueId(), Main.get().getTimerManager().getProtectionTimer().defaultCooldown, true)) {
            Main.get().getTimerManager().getProtectionTimer().setPaused(player, player.getUniqueId(), true);
        }

        return true;
    }

    public long getTimeleft() {
        return this.expiryMillis - System.currentTimeMillis();
    }

    public boolean isActive() {
        return this.getTimeleft() > 0L;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Deathban)) {
            return false;
        }

        return ((Deathban) obj).getDeathbanned().equals(this.deathbanned);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.deathbanned);
    }
}