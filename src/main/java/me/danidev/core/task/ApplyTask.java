package me.danidev.core.task;

import me.danidev.core.Main;
import me.danidev.core.handlers.ApplyHandler;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.file.FileConfig;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

public class ApplyTask implements Runnable {

    private final FileConfig langConfig = Main.get().getLangConfig();

    @Override
    public void run() {
        if (!Bukkit.getServer().getOnlinePlayers().isEmpty()) {
            if (ApplyHandler.isGlobal()) sendApply("GLOBAL");
            if (ApplyHandler.isHoster()) sendApply("HOSTER");
            if (ApplyHandler.isStaff()) sendApply("STAFF");
            if (ApplyHandler.isWebsite()) sendApply("WEBSITE");
            if (ApplyHandler.isDeveloper()) sendApply("DEVELOPER");
        }
    }

    private void sendApply(String apply) {
        Bukkit.getServer().getOnlinePlayers().forEach(online ->
                online.playSound(online.getLocation(), Sound.WITHER_SPAWN, 1F, 1F));
        langConfig.getStringList("APPLY." + apply).forEach(lines ->
                Bukkit.broadcastMessage(CC.translate(lines)));
    }
}
