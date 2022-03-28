package me.danidev.core.managers.support;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

@Getter
public class PartnerManager {

    private final List<Partner> partners = new ArrayList<>();
    private final Map<UUID, Boolean> supported = new HashMap<>();

    public void loadSupport() {
        partners.clear();

        ConfigurationSection section =  Main.get().getSupportConfig().getConfiguration().getConfigurationSection("SUPPORT-MENU.PARTNERS");
        section.getKeys(false).forEach(partner -> {
            String displayName =  Main.get().getSupportConfig().getConfiguration().getString("SUPPORT-MENU.PARTNERS." + partner + ".ICON.DISPLAYNAME");
            List<String> description =  Main.get().getSupportConfig().getConfiguration().getStringList("SUPPORT-MENU.PARTNERS." + partner + ".ICON.DESCRIPTION");
            int slot =  Main.get().getSupportConfig().getConfiguration().getInt("SUPPORT-MENU.PARTNERS." + partner + ".ICON.SLOT");
            List<String> commands =  Main.get().getSupportConfig().getConfiguration().getStringList("SUPPORT-MENU.PARTNERS." + partner + ".COMMANDS");

            partners.add(new Partner(partner, displayName, description, slot, commands));
        });

        CC.log("&aSuccessfully loaded &f" + partners.size() + " &apartners.");
    }

    public boolean isSupported(UUID uuid) {
        return supported.get(uuid);
    }

    public void setSupported(UUID uuid, boolean b0) {
        supported.put(uuid, b0);
    }
} 