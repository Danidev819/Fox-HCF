package me.danidev.core.managers.support.data;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.file.FileConfig;

import java.util.Map;
import java.util.UUID;

public class SupportData {

    private FileConfig dataConfig = Main.get().getDataConfig();

    public static void save() {
        if (!Main.get().getPartnerManager().getSupported().isEmpty()) {
            for (Map.Entry<UUID, Boolean> entry : Main.get().getPartnerManager().getSupported().entrySet()) {
                Main.get().getDataConfig().getConfiguration().set("DATA." + entry.getKey() + ".SUPPORTED", entry.getValue());
            }
        }
        Main.get().getDataConfig().getConfiguration().saveToString();
        CC.log("&aSaving data...");
    }

    public static void load() {
        Main.get().getDataConfig().getConfiguration().getConfigurationSection("DATA").getKeys(false).forEach(uuid -> {
            boolean supported = Main.get().getDataConfig().getConfiguration().getBoolean("DATA." + uuid + ".SUPPORTED");
            Main.get().getPartnerManager().getSupported().put(UUID.fromString(uuid), supported);
        });
        CC.log("&aLoading data...");
    }
}