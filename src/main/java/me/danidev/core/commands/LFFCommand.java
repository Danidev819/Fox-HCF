package me.danidev.core.commands;

import me.danidev.core.Main;
import me.danidev.core.managers.faction.type.PlayerFaction;
import me.danidev.core.utils.CC;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class LFFCommand implements CommandExecutor {

    private static Map<UUID, Long> cooldownMap = new HashMap<>();

    public LFFCommand(Main plugin) {
        plugin.getCommand("lff").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        PlayerFaction playerFaction = Main.get().getFactionManager().getPlayerFaction(player);
        if (playerFaction != null) {
            player.sendMessage(ChatColor.RED + "You cannot issue this command while you're in a faction");
            return true;
        }
        if (isOnCooldown(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are currently on cooldown for another " + DurationFormatUtils.formatDurationWords(getCooldownLeft(player.getUniqueId()), true, true) + ".");
            return true;
        }
        Bukkit.getOnlinePlayers().forEach(loopPlayer -> {
            loopPlayer.sendMessage(CC.translate("&7&m------------------------------------------"));
            loopPlayer.sendMessage(CC.translate(player.getDisplayName() + " &6is looking for a faction!"));
            loopPlayer.sendMessage(CC.translate("&7&m------------------------------------------"));
            applyCooldown(player.getUniqueId());
        });
        return false;
    }


    private static boolean isOnCooldown(UUID uuid) {
        return getCooldownLeft(uuid) > 0L;
    }

    private static long getCooldownLeft(UUID uuid) {
        if (!cooldownMap.containsKey(uuid))
            return 0L;

        long timeLeft = cooldownMap.getOrDefault(uuid, 0L) - System.currentTimeMillis();

        if (timeLeft <= 0L)
            clearCooldown(uuid);

        return timeLeft;
    }

    public static void applyCooldown(UUID uuid) {
        cooldownMap.put(uuid, System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));
    }

    private static void clearCooldown(UUID uuid) {
        cooldownMap.remove(uuid);
    }

}