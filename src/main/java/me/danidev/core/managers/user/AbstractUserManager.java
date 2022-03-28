package me.danidev.core.managers.user;

import me.danidev.core.Main;
import org.bukkit.Bukkit;
import com.google.common.base.Preconditions;

import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collections;
import java.util.TreeMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;
import org.bukkit.event.Listener;

public abstract class AbstractUserManager implements Listener
{
    private static final Pattern USERNAME_REGEX;
    protected final Main plugin;
    protected final ConcurrentMap<UUID, FactionUser> inMemoryStorage;
    protected final ConcurrentMap<UUID, FactionUser> onlineStorage;
    protected final Map<String, UUID> uuidCache;
    
    static {
        USERNAME_REGEX = Pattern.compile("^[a-zA-Z0-9_]{2,16}$");
    }
    
    public AbstractUserManager(final Main plugin) {
        this.uuidCache = Collections.synchronizedMap(new TreeMap<>(String.CASE_INSENSITIVE_ORDER));
        this.inMemoryStorage = new ConcurrentHashMap<>();
        this.onlineStorage = new ConcurrentHashMap<>();
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.reloadUserData();
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        FactionUser factionUser = this.inMemoryStorage.get(uuid);
        if (factionUser == null) {
            factionUser = new FactionUser(uuid);
            this.inMemoryStorage.put(uuid, factionUser);
            this.saveUser(factionUser);
        }
        this.onlineStorage.put(uuid, factionUser);
        this.uuidCache.put(player.getName(), uuid);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        this.onlineStorage.remove(uuid);
    }
    
    public ConcurrentMap<UUID, FactionUser> getUsers() {
        return this.inMemoryStorage;
    }
    
    public FactionUser getUser(final UUID uuid) {
        Preconditions.checkNotNull((Object)uuid);
        FactionUser factionUser;
        return ((factionUser = this.inMemoryStorage.get(uuid)) != null) ? factionUser : (((factionUser = this.onlineStorage.get(uuid)) != null) ? factionUser : this.insertAndReturn(uuid, new FactionUser(uuid)));
    }
    
    public FactionUser getIfContainedOffline(final UUID uuid) {
        Preconditions.checkNotNull((Object)uuid);
        final FactionUser factionUser;
        return ((factionUser = this.onlineStorage.get(uuid)) != null) ? factionUser : this.inMemoryStorage.get(uuid);
    }
    
    public FactionUser insertAndReturn(final UUID uuid, final FactionUser factionUser) {
        this.inMemoryStorage.put(uuid, factionUser);
        return factionUser;
    }
    
    public FactionUser getIfContains(final UUID uuid) {
        return this.onlineStorage.get(uuid);
    }
    
    public UUID fetchUUID(final String username) {
        final Player player = Bukkit.getPlayer(username);
        if (player != null) {
            return player.getUniqueId();
        }
        if (AbstractUserManager.USERNAME_REGEX.matcher(username).matches()) {
            return this.uuidCache.get(username);
        }
        return null;
    }
    
    public ConcurrentMap<UUID, FactionUser> getOnlineStorage() {
        return this.onlineStorage;
    }
    
    public void saveUser(final FactionUser user) {
    }
    
    public abstract void saveUserData();
    
    public abstract void reloadUserData();
}
