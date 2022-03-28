package me.danidev.core.managers.eotw;

import me.danidev.core.Main;
import me.danidev.core.listeners.BorderListener;
import me.danidev.core.managers.faction.event.FactionClaimChangeEvent;
import me.danidev.core.managers.faction.event.FactionCreateEvent;
import me.danidev.core.managers.faction.event.cause.ClaimChangeCause;
import me.danidev.core.managers.faction.type.ClaimableFaction;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.faction.type.PlayerFaction;
import me.danidev.core.managers.games.koth.faction.KothFaction;
import me.danidev.core.managers.kit.event.KitApplyEvent;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class EOTWHandler implements Listener {
    public static final int BORDER_DECREASE_MINIMUM = 500;
    public static final int BORDER_DECREASE_AMOUNT = 200;

    public static final long BORDER_DECREASE_TIME_MILLIS = TimeUnit.MINUTES.toMillis(20L);
    public static final int BORDER_DECREASE_TIME_SECONDS = (int) TimeUnit.MILLISECONDS.toSeconds(BORDER_DECREASE_TIME_MILLIS);
    public static final int BORDER_DECREASE_TIME_SECONDS_HALVED = BORDER_DECREASE_TIME_SECONDS / 2;
    public static final String BORDER_DECREASE_TIME_WORDS = DurationFormatUtils.formatDurationWords(BORDER_DECREASE_TIME_MILLIS, true, true);
    public static final String BORDER_DECREASE_TIME_ALERT_WORDS = DurationFormatUtils.formatDurationWords(BORDER_DECREASE_TIME_MILLIS / 2, true, true);

    public static final long EOTW_WARMUP_WAIT_MILLIS = TimeUnit.MINUTES.toMillis(2L);
    public static final int EOTW_WARMUP_WAIT_SECONDS = (int) (TimeUnit.MILLISECONDS.toSeconds(EOTW_WARMUP_WAIT_MILLIS));

    private static final long EOTW_CAPPABLE_WAIT_MILLIS = TimeUnit.MINUTES.toMillis(1L);
    private static final int WITHER_INTERVAL_SECONDS = 5;

    private EotwRunnable runnable;
    private Main plugin;

    public EOTWHandler(Main plugin) {
        this.plugin = plugin;
    }

    public EotwRunnable getRunnable() {
        return runnable;
    }

    public boolean isEndOfTheWorld() {
        return isEndOfTheWorld(true);
    }

    public boolean isEndOfTheWorld(boolean ignoreWarmup) {
        return runnable != null && (!ignoreWarmup || runnable.getElapsedMilliseconds() > 0);
    }

    public void setEndOfTheWorld(boolean yes) {
        if (yes == isEndOfTheWorld(false)) {
            return;
        }

        if (yes) {
            runnable = new EotwRunnable();
            runnable.runTaskTimer(plugin, 20L, 20L);
        } else {
            if (runnable != null) {
                runnable.cancel();
                runnable = null;
            }
        }
    }

    public static final class EotwRunnable extends BukkitRunnable {

        private static final PotionEffect WITHER = new PotionEffect(PotionEffectType.WITHER, 200, 0);

        private final Set<Player> outsideBorder = new HashSet<>();

        private long startStamp;
        private int elapsedSeconds;

        public EotwRunnable() {
            this.startStamp = System.currentTimeMillis() + EOTW_WARMUP_WAIT_MILLIS;
            this.elapsedSeconds = -EOTW_WARMUP_WAIT_SECONDS;
        }

        public void handleDisconnect(Player player) {
            outsideBorder.remove(player);
        }

        public long getMillisUntilStarting() {
            long difference = System.currentTimeMillis() - startStamp;
            return difference > 0L ? -1L : Math.abs(difference);
        }

        public long getMillisUntilCappable() {
            return EOTW_CAPPABLE_WAIT_MILLIS - getElapsedMilliseconds();
        }
        private KothFaction kothFaction;
        public long getElapsedMilliseconds() {
            return System.currentTimeMillis() - startStamp;
        }

        @Override
        public void run() {
            elapsedSeconds++;

            if (elapsedSeconds == 0) {
                for (Faction faction : Main.get().getFactionManager().getFactions()) {
                    if (faction instanceof ClaimableFaction) {
                        ClaimableFaction claimableFaction = (ClaimableFaction) faction;
                        claimableFaction.removeClaims(claimableFaction.getClaims(), Bukkit.getConsoleSender());
                    }
                }

                Bukkit.broadcastMessage(ChatColor.RED + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
                Bukkit.broadcastMessage(ChatColor.RED + "\u2588" + ChatColor.DARK_RED + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + " " + ChatColor.DARK_RED.toString() + ChatColor.BOLD + "EOTW");
                Bukkit.broadcastMessage(ChatColor.RED + "\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.RED + "\u2588\u2588\u2588\u2588\u2588" + " " + ChatColor.RED.toString() + ChatColor.BOLD + "EOTW has commenced.");
                Bukkit.broadcastMessage(ChatColor.RED + "\u2588" + ChatColor.DARK_RED + "\u2588\u2588\u2588\u2588" + ChatColor.RED + "\u2588\u2588" + " " + ChatColor.RED + "All SafeZones are now Deathban.");
                Bukkit.broadcastMessage(ChatColor.RED + "\u2588" + ChatColor.DARK_RED + "\u2588" + ChatColor.RED + "\u2588\u2588\u2588\u2588\u2588" + " " + ChatColor.RED + "The world border will now start shrinking to 500.");
                Bukkit.broadcastMessage(ChatColor.RED + "\u2588" + ChatColor.DARK_RED + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + " " + ChatColor.RED + "All factions are now raidable.");
                Bukkit.broadcastMessage(ChatColor.RED + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
                Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "f setdtr all -999");
                return;
            }

            if (elapsedSeconds % WITHER_INTERVAL_SECONDS == 0) {
                Iterator<Player> iterator = outsideBorder.iterator();
                while (iterator.hasNext()) {
                    Player player = iterator.next();

                    if (BorderListener.isWithinBorder(player.getLocation())) {
                        iterator.remove();
                        continue;
                    }

                    player.sendMessage(ChatColor.RED + "You are currently outside of the border during EOTW, so you were withered.");
                    player.addPotionEffect(WITHER, true);
                }
            }

            for (World.Environment current : World.Environment.values()) {
                int borderSize = Configuration.BORDER_SIZES.get(current);
                int newBorderSize = borderSize - BORDER_DECREASE_AMOUNT;
                if (newBorderSize <= BORDER_DECREASE_MINIMUM) {
                    Configuration.BORDER_SIZES.put(current, BORDER_DECREASE_MINIMUM);
                    continue;
                }
                if (elapsedSeconds % BORDER_DECREASE_TIME_SECONDS == 0) {
                    Configuration.BORDER_SIZES.put(current, borderSize = newBorderSize);
                    String msg = (ChatColor.RED + "Border has been decreased to " + ChatColor.DARK_RED + newBorderSize + ChatColor.RED + " blocks.");

                    for (Player player : Bukkit.getOnlinePlayers()) {
                       if (player.getWorld().getEnvironment().equals(current))
                            player.sendMessage(msg);
                    }

                    // Update list of players outside of the border now it has shrunk.
                    for (Player player : Bukkit.getOnlinePlayers()) {

                        if (!BorderListener.isWithinBorder(player.getLocation())) {
                            outsideBorder.add(player);
                        }

                    }
                } else if (elapsedSeconds % BORDER_DECREASE_TIME_SECONDS_HALVED == 0) {
                    String msg2 = (ChatColor.DARK_AQUA + "Border decreasing to " + ChatColor.YELLOW + newBorderSize + ChatColor.DARK_AQUA + " blocks in " + ChatColor.YELLOW
                            + BORDER_DECREASE_TIME_ALERT_WORDS + ChatColor.DARK_AQUA + '.');
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getWorld().getEnvironment().equals(current))
                            player.sendMessage(msg2);
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionCreate(final FactionCreateEvent factionCreateEvent) {
        if (Main.get().getEotwHandler().isEndOfTheWorld() && factionCreateEvent.getFaction() instanceof PlayerFaction) {
            factionCreateEvent.setCancelled(true);
            factionCreateEvent.getSender().sendMessage(CC.translate("&cYou can't create factions while &lEOTW &cis active."));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionClaimChange(final FactionClaimChangeEvent factionClaimChangeEvent) {
        if (Main.get().getEotwHandler().isEndOfTheWorld() && factionClaimChangeEvent.getCause() == ClaimChangeCause.CLAIM && factionClaimChangeEvent.getClaimableFaction() instanceof PlayerFaction) {
            factionClaimChangeEvent.setCancelled(true);
            factionClaimChangeEvent.getSender().sendMessage(CC.translate("&cYou can't claim while &lEOTW &cis active."));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onUseKit(KitApplyEvent kitapplyEvent) {
       if (Main.get().getEotwHandler().isEndOfTheWorld() && Main.get().getMainConfig().getBoolean("KIT-USE-IN-EOTW.ENABLED") == true)  {
            kitapplyEvent.setCancelled(false);
        }
        kitapplyEvent.setCancelled(true);
    }
}