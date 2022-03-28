package me.danidev.core.managers.user;

import java.util.Set;
import java.util.LinkedHashMap;
import java.util.Collection;

import me.danidev.core.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;

import com.google.common.base.Preconditions;

import org.apache.commons.lang3.ObjectUtils;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import java.util.HashMap;

import java.util.UUID;
import java.util.Map;

import org.bukkit.event.Listener;

public class UserManager implements Listener {
	
	private final Main plugin;
	private final Map<UUID, FactionUser> users;
	private final ConsoleUser console;
	private Config userConfig;
	private Map<UUID, ServerParticipator> participators;

	public UserManager(Main plugin) {
		this.users = new HashMap<>();
		this.plugin = plugin;
		this.reloadUserData();
		this.reloadParticipatorData();
		ServerParticipator participator = this.participators.get(ConsoleUser.CONSOLE_UUID);
		if (participator != null) {
			this.console = (ConsoleUser) participator;
		} else {
			this.participators.put(ConsoleUser.CONSOLE_UUID, this.console = new ConsoleUser());
		}
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		UUID uuid = event.getPlayer().getUniqueId();
		this.users.putIfAbsent(uuid, new FactionUser(uuid));
	}

	public Map<UUID, FactionUser> getUsers() {
		return this.users;
	}

	public FactionUser getUserAsync(UUID uuid) {
		synchronized (this.users) {
			FactionUser revert = new FactionUser(uuid);
			FactionUser user = this.users.putIfAbsent(uuid, revert);
			return (FactionUser) ObjectUtils.firstNonNull((Object[]) new FactionUser[] { user, revert });
		}
	}

	public FactionUser getUser(UUID uuid) {
		FactionUser revert = new FactionUser(uuid);
		FactionUser user = this.users.putIfAbsent(uuid, revert);
		return (FactionUser) ObjectUtils.firstNonNull((Object[]) new FactionUser[] { user, revert });
	}

	public void reloadUserData() {
		this.userConfig = new Config(this.plugin, "faction-users");
		Object object = this.userConfig.get("users");
		if (object instanceof MemorySection) {
			MemorySection section = (MemorySection) object;
			Collection<String> keys = section.getKeys(false);
			for (String id : keys) {
				this.users.put(UUID.fromString(id),
						(FactionUser) this.userConfig.get(String.valueOf(section.getCurrentPath()) + '.' + id));
			}
		}
	}

	public void saveUserData() {
		Set<Map.Entry<UUID, FactionUser>> entrySet = this.users.entrySet();
		LinkedHashMap<String, FactionUser> saveMap = new LinkedHashMap<>(entrySet.size());
		for (Map.Entry<UUID, FactionUser> entry : entrySet) {
			saveMap.put(entry.getKey().toString(), entry.getValue());
		}
		this.userConfig.set("users", saveMap);
		this.userConfig.save();
	}
	
	public ConsoleUser getConsole() {
		return this.console;
	}

	public Map<UUID, ServerParticipator> getParticipators() {
		return this.participators;
	}

	public ServerParticipator getParticipator(CommandSender sender) {
		Preconditions.checkNotNull((Object) sender, "CommandSender cannot be null");
		if (sender instanceof ConsoleCommandSender) {
			return this.console;
		}
		if (sender instanceof Player) {
			return this.participators.get(((Player) sender).getUniqueId());
		}
		return null;
	}

	public ServerParticipator getParticipator(UUID uuid) {
		Preconditions.checkNotNull((Object) uuid, "Unique ID cannot be null");
		return this.participators.get(uuid);
	}

	public BaseUser getBaseUser(UUID uuid) {
		ServerParticipator participator = this.getParticipator(uuid);
		BaseUser baseUser;
		if (participator instanceof BaseUser) {
			baseUser = (BaseUser) participator;
		} else {
			this.participators.put(uuid, baseUser = new BaseUser(uuid));
		}
		return baseUser;
	}

	public void reloadParticipatorData() {
		this.userConfig = new Config(this.plugin, "participators");
		Object object = this.userConfig.get("participators");
		if (object instanceof MemorySection) {
			MemorySection section = (MemorySection) object;
			Set<String> keys = section.getKeys(false);
			this.participators = new HashMap<>(keys.size());
			for (String id : keys) {
				this.participators.put(UUID.fromString(id),
						(ServerParticipator) this.userConfig.get("participators." + id));
			}
		} else {
			this.participators = new HashMap<>();
		}
	}

	public void saveParticipatorData() {
		Map<String, ServerParticipator> saveMap = new LinkedHashMap<>(this.participators.size());
		for (Map.Entry<UUID, ServerParticipator> entry : this.participators.entrySet()) {
			saveMap.put(entry.getKey().toString(), entry.getValue());
		}
		this.userConfig.set("participators", saveMap);
		this.userConfig.save();
	}
}
