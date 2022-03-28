package me.danidev.core.managers.support.menu;

import me.danidev.core.Main;
import me.danidev.core.managers.support.menu.buttons.PartnerButton;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.menu.Button;
import me.danidev.core.utils.menu.Menu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SupportMenu extends Menu {

    private final Main plugin = Main.get();

    {
        setUpdateAfterClick(false);
        setAutoUpdate(false);
    }

    @Override
    public String getTitle(Player player) {
        return CC.translate(Main.get().getSupportConfig().getConfiguration().getString("SUPPORT-MENU.TITLE"));
    }

    @Override
    public int getSize() {
        return 9 *  Main.get().getSupportConfig().getConfiguration().getInt("SUPPORT-MENU.SIZE");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        plugin.getPartnerManager().getPartners().forEach(partner ->
                buttons.put(partner.getSlot(), new PartnerButton(partner)));

        return buttons;
    }
}