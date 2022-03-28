package me.danidev.core.managers.games.koth.tracker;

import me.danidev.core.Main;
import me.danidev.core.managers.games.koth.faction.EventFaction;
import me.danidev.core.managers.games.koth.faction.KothFaction;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.DateTimeFormats;
import me.danidev.core.managers.games.koth.CaptureZone;
import me.danidev.core.managers.games.koth.EventTimer;
import me.danidev.core.managers.games.koth.EventType;
import me.danidev.core.utils.DiscordWebhook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("deprecation")
public class KothTracker implements EventTracker {

    public static final long DEFAULT_CAP_MILLIS;
    private static final long MINIMUM_CONTROL_TIME_ANNOUNCE;
    private final Main plugin;

    static {
        MINIMUM_CONTROL_TIME_ANNOUNCE = TimeUnit.SECONDS.toMillis(25L);
        DEFAULT_CAP_MILLIS = TimeUnit.MINUTES.toMillis(15L);
    }

    public KothTracker(final Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public EventType getEventType() {
        return EventType.KOTH;
    }

    @Override
    public void tick(final EventTimer eventTimer, final EventFaction eventFaction) {
        final CaptureZone captureZone = ((KothFaction) eventFaction).getCaptureZone();
        final long remainingMillis = captureZone.getRemainingCaptureMillis();

        if (remainingMillis <= 0L) {
            this.plugin.getTimerManager().eventTimer.handleWinnerKoth(captureZone.getCappingPlayer());
            eventTimer.clearCooldown();
            return;
        }

        if (remainingMillis == captureZone.getDefaultCaptureMillis()) {
            return;
        }

        final int remainingSeconds = (int) (remainingMillis / 1000L);

        if (remainingSeconds > 0 && remainingSeconds % 30 == 0) {
            Bukkit.broadcastMessage(CC.translate("&8[&6&l" + eventFaction.getEventType().getDisplayName() + "&8] &eSomeone is controlling " + captureZone.getDisplayName() + ". &c" + '(' + DateTimeFormats.KOTH_FORMAT.format(remainingMillis) + ')'));
        }
    }

    EventTimer eventTimer = Main.get().getTimerManager().eventTimer;
    EventFaction eventFaction = eventTimer.getEventFaction();
    KothFaction kothFaction = (KothFaction) eventFaction;

    @Override
    public void onContest(final EventFaction eventFaction, final EventTimer eventTimer) {
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7\u2588\u2588\u2588\u2588\u2588\u2588\u2588"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7\u2588&b\u2588&7&7\u2588&7\u2588&7\u2588&b\u2588&7\u2588"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7\u2588&b\u2588&7&7\u2588&7\u2588&b\u2588&7\u2588&7\u2588" + CC.translate("   &6[KingOfTheHill]")));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7\u2588&b\u2588&b\u2588&b\u2588&7\u2588\u2588&7\u2588   " + ChatColor.YELLOW + eventFaction.getName() + "&eKOTH"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7\u2588&b\u2588&7\u2588\u2588&b\u2588&7\u2588&7\u2588" + CC.translate("   &6can be contested now. ")));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7\u2588&b\u2588&7\u2588\u2588\u2588&b\u2588&7&7\u2588"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7\u2588&b\u2588&7\u2588\u2588\u2588&b\u2588&7\u2588"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7\u2588\u2588\u2588\u2588\u2588\u2588&7\u2588"));
        if (Main.get().getKothwebhookConfig().getBoolean("DISCORD.ENABLED")) {
            try {
                DiscordWebhook webhook = new DiscordWebhook(Main.get().getKothwebhookConfig().getString("DISCORD.URL"));
                webhook.setTts(false);
                webhook.addEmbed(new DiscordWebhook.EmbedObject()
                        .setTitle("A new KoTH has started")
                        .setColor(Color.CYAN)
                        .addField("Modality:", Main.get().getKothwebhookConfig().getString("MODALITY"), false)
                        .addField("KoTH:", eventFaction.getName(), false)
                        .addField("Time:", DateTimeFormats.KOTH_FORMAT.format(eventTimer.getRemaining()), false)
                        .setFooter(Main.get().getKothwebhookConfig().getString("DISCORD.FOOTER"), Main.get().getKothwebhookConfig().getString("DISCORD.IMAGE")));
                webhook.execute();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @Override
    public boolean onControlTake(final Player player, final CaptureZone captureZone) {
        player.sendMessage(ChatColor.YELLOW + "You are now in control of " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.YELLOW + '.');
        return true;
    }

    @Override
    public boolean onControlLoss(final Player player, final CaptureZone captureZone, final EventFaction eventFaction) {
        player.sendMessage(ChatColor.GOLD + "You are no longer in control of " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + '.');
        final long remainingMillis = captureZone.getRemainingCaptureMillis();
        if (remainingMillis > 0L && captureZone.getDefaultCaptureMillis() - remainingMillis > KothTracker.MINIMUM_CONTROL_TIME_ANNOUNCE) {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + eventFaction.getEventType().getDisplayName() + "] " + ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.GOLD + " has lost control of " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + '.' + ChatColor.RED + " (" + DateTimeFormats.KOTH_FORMAT.format(captureZone.getRemainingCaptureMillis()) + ')');
        }
        return true;
    }

    @Override
    public void stopTiming() {
    }
}
