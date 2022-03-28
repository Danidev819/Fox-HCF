package me.danidev.core.managers.timer;

import java.util.Collection;

import lombok.Getter;
import me.danidev.core.Main;
import me.danidev.core.managers.games.koth.EventTimer;
import me.danidev.core.managers.user.Config;
import org.bukkit.plugin.java.JavaPlugin;

import me.danidev.core.managers.timer.type.ArcherTimer;
import me.danidev.core.managers.timer.type.EnderPearlTimer;
import me.danidev.core.managers.timer.type.GoldenAppleTimer;
import me.danidev.core.managers.timer.type.LogoutTimer;
import me.danidev.core.managers.timer.type.NotchAppleTimer;
import me.danidev.core.managers.timer.type.PvPClassWarmupTimer;
import me.danidev.core.managers.timer.type.ProtectionTimer;
import me.danidev.core.managers.timer.type.SpawnTagTimer;
import me.danidev.core.managers.timer.type.StuckTimer;
import me.danidev.core.managers.timer.type.TeleportTimer;

import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.event.Listener;

public class TimerManager implements Listener {
	
	@Getter
	public EnderPearlTimer enderPearlTimer;
	@Getter
	public ArcherTimer archerTimer;
	@Getter
	private GoldenAppleTimer goldenaApleTimer;
	@Getter
	private LogoutTimer logoutTimer;
	@Getter
	public NotchAppleTimer notchAppleTimer;
	@Getter
	public ProtectionTimer protectionTimer;
	@Getter
	private PvPClassWarmupTimer pvPClassWarmupTimer;
	@Getter
	private SpawnTagTimer spawnTagTimer;
	@Getter
	private StuckTimer stuckTimer;
	@Getter
	private TeleportTimer teleportTimer;
	@Getter
	public EventTimer eventTimer;
	@Getter
	private final Set<Timer> timers = new LinkedHashSet<>();
	private final JavaPlugin plugin;
	private Config config;

	public TimerManager(Main plugin) {
		(this.plugin = plugin).getServer().getPluginManager().registerEvents(this, plugin);
		this.registerTimer(this.enderPearlTimer = new EnderPearlTimer(plugin));
		this.registerTimer(this.goldenaApleTimer = new GoldenAppleTimer(plugin));
		this.registerTimer(this.logoutTimer = new LogoutTimer());
		this.registerTimer(this.notchAppleTimer = new NotchAppleTimer(plugin));
		this.registerTimer(this.protectionTimer = new ProtectionTimer(plugin));
		this.registerTimer(this.pvPClassWarmupTimer = new PvPClassWarmupTimer(plugin));
		this.registerTimer(this.spawnTagTimer = new SpawnTagTimer(plugin));
		this.registerTimer(this.stuckTimer = new StuckTimer());
		this.registerTimer(this.teleportTimer = new TeleportTimer(plugin));
		this.registerTimer(this.eventTimer = new EventTimer(plugin));
		this.registerTimer(this.archerTimer = new ArcherTimer(plugin));
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public Collection<Timer> getTimers() {
		return this.timers;

	}

	public JavaPlugin getPlugin() {
		return this.plugin;
	}

	public Config getConfig() {
		return this.config;
	}

	public void setConfig(final Config config) {
		this.config = config;
	}


	protected boolean canEqual(final Object other) {
		return other instanceof TimerManager;
	}

	public void registerTimer(Timer timer) {
		this.timers.add(timer);

		if (timer instanceof Listener) {
			this.plugin.getServer().getPluginManager().registerEvents((Listener) timer, this.plugin);
		}
	}

	public void unregisterTimer(Timer timer) {
		this.timers.remove(timer);
	}

	/**
	 * Reloads the {@link Timer} data from storage.
	 */
	public void reloadTimerData() {
		this.config = new Config(plugin, "timers");
		for (Timer timer : this.timers) {
			timer.load(this.config);
		}
	}

	/**
	 * Saves the {@link Timer} data to storage.
	 */
	public void saveTimerData() {
		for (Timer timer : this.timers) {
			timer.onDisable(this.config);
		}

		this.config.save();
	}
}
