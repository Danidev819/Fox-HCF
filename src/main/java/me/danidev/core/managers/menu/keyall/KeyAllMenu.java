package me.danidev.core.managers.menu.keyall;

import me.danidev.core.managers.menu.keyall.buttons.MiniKeyAllButton;
import me.danidev.core.managers.menu.keyall.buttons.NormalKeyAllButton;
import me.danidev.core.managers.menu.keyall.buttons.OPKeyAllButton;
import me.danidev.core.utils.menu.Button;
import me.danidev.core.utils.menu.Menu;
import com.google.common.collect.Maps;
import org.bukkit.entity.Player;

import java.util.Map;

public class KeyAllMenu extends Menu {

    {
        setAutoUpdate(false);
        setUpdateAfterClick(false);
    }

    public String getTitle(Player player) {
        return "&b&lKeyAll Types";
    }

    @Override
    public int getSize() {
        return 9 * 3;
    }

    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();
        buttons.put(11, new MiniKeyAllButton("MINI", "&6&lMini KeyAll"));
        buttons.put(13, new NormalKeyAllButton("NORMAL", "&6&lNormal KeyAll"));
        buttons.put(15, new OPKeyAllButton("OP", "&6&lOP KeyAll"));
        return buttons;
    }
}
