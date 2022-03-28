package me.danidev.core.utils.scoreboard;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import me.danidev.core.utils.scoreboard.events.AssembleBoardCreateEvent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

@Getter @Setter
public class Assemble {

    private JavaPlugin plugin;
    private AssembleAdapter adapter;
    private Map<UUID, AssembleBoard> boards;
    private AssembleThread thread;
    private AssembleListener listeners;
    private long ticks = 2;
    private boolean hook = false;
    private AssembleStyle assembleStyle = AssembleStyle.MODERNO;

    public Assemble(JavaPlugin plugin, AssembleAdapter adapter) {
        if (plugin == null) {
            throw new RuntimeException("Assemble can not be instantiated without a plugin instance!");
        }

        this.plugin = plugin;
        this.adapter = adapter;
        this.boards = new ConcurrentHashMap<>();

        this.setup();
    }

    public void setup() {
        listeners = new AssembleListener(this);

        //Register Events
        this.plugin.getServer().getPluginManager().registerEvents(listeners, this.plugin);

        //Ensure that the thread has stopped running
        if (this.thread != null) {
            this.thread.stop();
            this.thread = null;
        }

        //Register new boards for existing online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            //Make sure it doesn't double up
            AssembleBoardCreateEvent createEvent = new AssembleBoardCreateEvent(player);

            Bukkit.getPluginManager().callEvent(createEvent);
            if (createEvent.isCancelled()) {
                return;
            }

            getBoards().put(player.getUniqueId(), new AssembleBoard(player, this));
        }

        //Start Thread
        this.thread = new AssembleThread(this);
    }

    public void cleanup() {
        if (this.thread != null) {
            this.thread.stop();
            this.thread = null;
        }

        if (listeners != null) {
            HandlerList.unregisterAll(listeners);
            listeners = null;
        }

        for (UUID uuid : getBoards().keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            getBoards().remove(uuid);
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }

    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    public AssembleAdapter getAdapter() {
        return this.adapter;
    }

    public Map<UUID, AssembleBoard> getBoards() {
        return this.boards;
    }

    public AssembleThread getThread() {
        return this.thread;
    }

    public AssembleListener getListeners() {
        return this.listeners;
    }

    public long getTicks() {
        return this.ticks;
    }

    public boolean isHook() {
        return this.hook;
    }

    public AssembleStyle getAssembleStyle() {
        return this.assembleStyle;
    }

    public void setPlugin(final JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void setAdapter(final AssembleAdapter adapter) {
        this.adapter = adapter;
    }

    public void setBoards(final Map<UUID, AssembleBoard> boards) {
        this.boards = boards;
    }

    public void setThread(final AssembleThread thread) {
        this.thread = thread;
    }

    public void setListeners(final AssembleListener listeners) {
        this.listeners = listeners;
    }

    public void setTicks(final long ticks) {
        this.ticks = ticks;
    }

    public void setHook(final boolean hook) {
        this.hook = hook;
    }

    public void setAssembleStyle(final AssembleStyle assembleStyle) {
        this.assembleStyle = assembleStyle;
    }
}