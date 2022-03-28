package me.danidev.core.task;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.service.ConfigurationService;
import me.danidev.core.utils.file.FileConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class OnlineDonatorTask implements Runnable {

    private final FileConfig mainConfig = Main.get().getMainConfig();

    @Override
    public void run() {
        if (!Bukkit.getServer().getOnlinePlayers().isEmpty()) {

            String donators = Bukkit.getServer().getOnlinePlayers().stream()
                    .filter(online -> online.hasPermission("fhcf.donator") && !online.isOp() && !online.hasPermission("*"))
                    .map(Player::getName)
                    .collect(Collectors.joining(", "));

            if (donators.isEmpty()) {
                donators = "None";
            }

            for (String lines : mainConfig.getStringList("ONLINE_DONATOR.BROADCAST")) {
                CC.broadcast(lines
                        .replace("%DONATORS%", donators)
                        .replace("%STORE%", ConfigurationService.STORE));
            }
        }
    }
}
