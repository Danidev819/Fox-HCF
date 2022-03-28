package me.danidev.core.managers.timer.type;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import me.danidev.core.Main;
import me.danidev.core.managers.classes.PvPClass;
import me.danidev.core.managers.timer.PlayerTimer;
import me.danidev.core.managers.timer.TimerCooldown;
import me.danidev.core.managers.user.Config;
import me.danidev.core.utils.service.ConfigurationService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.EquipmentSetEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import com.google.common.base.Preconditions;

public class PvPClassWarmupTimer extends PlayerTimer implements Listener {

	protected Map<UUID, PvPClass> classWarmups;

	private final Main plugin;

	public PvPClassWarmupTimer(Main plugin) {
		super("Warmup", TimeUnit.SECONDS.toMillis(ConfigurationService.CLASS_WARMUP_TIMER), false);
		this.plugin = plugin;
        this.classWarmups = new HashMap<>();
        this.classUpdate();
	}

    @Deprecated
    public void classUpdate() {
        new BukkitRunnable() {
            public void run() {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    PvPClassWarmupTimer.this.attemptEquip(online);
                }
            }
        }.runTaskLater(this.plugin, 10L);
    }

	@Override
	public String getScoreboardPrefix() {
		return ChatColor.translateAlternateColorCodes('&', "&b&l");
	}

    @Override
    public void onDisable(Config config) {
        super.onDisable(config);
        this.classWarmups.clear();
    }

    @Override
    public TimerCooldown clearCooldown(UUID playerUUID) {
        TimerCooldown runnable = super.clearCooldown(playerUUID);
        if (runnable != null) {
            this.classWarmups.remove(playerUUID);
            return runnable;
        }
        return null;
    }

    @Override
    public void onExpire(UUID userUUID) {
        Player player = Bukkit.getPlayer(userUUID);
        if (player == null) {
            return;
        }
        PvPClass hCFClass = this.classWarmups.remove(userUUID);
        Preconditions.checkNotNull((Object) hCFClass, "Attempted to equip a class for %s, but nothing was added",
                player.getName());
        this.plugin.getPvpClassManager().setEquippedClass(player, hCFClass);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerQuitEvent event) {
        this.plugin.getPvpClassManager().setEquippedClass(event.getPlayer(), null);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.attemptEquip(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEquipmentSet(EquipmentSetEvent event) {
        HumanEntity humanEntity = event.getHumanEntity();
        if (humanEntity instanceof Player) {
            this.attemptEquip((Player) humanEntity);
        }
    }

    private void attemptEquip(Player player) {
        PvPClass current = this.plugin.getPvpClassManager().getEquippedClass(player);
        if (current != null) {
            if (current.isApplicableFor(player)) {
                return;
            }
            this.plugin.getPvpClassManager().setEquippedClass(player, null);
        } else if ((current = this.classWarmups.get(player.getUniqueId())) != null) {
            if (current.isApplicableFor(player)) {
                return;
            }
            this.clearCooldown(player.getUniqueId());
        }
        Collection<PvPClass> pvpClasses = this.plugin.getPvpClassManager().getPvpClasses();
        for (PvPClass hCFClass : pvpClasses) {
            if (hCFClass.isApplicableFor(player)) {
                this.classWarmups.put(player.getUniqueId(), hCFClass);
                this.setCooldown(player, player.getUniqueId(), hCFClass.getWarmupDelay(), false);
                break;
            }
        }
    }
}