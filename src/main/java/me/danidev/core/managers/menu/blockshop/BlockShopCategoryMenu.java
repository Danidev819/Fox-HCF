package me.danidev.core.managers.menu.blockshop;

import me.danidev.core.Main;
import me.danidev.core.managers.menu.blockshop.buttons.BlockShopCategoryButton;
import me.danidev.core.utils.menu.Button;
import me.danidev.core.utils.menu.Menu;
import com.google.common.collect.Maps;
import org.bukkit.entity.Player;

import java.util.Map;

public class BlockShopCategoryMenu extends Menu {

    {
        setAutoUpdate(false);
        setUpdateAfterClick(false);
    }

    @Override
    public String getTitle(Player player) {
        return Main.get().getBlockShopConfig().getString("BLOCKSHOP.TITLE");
    }

    @Override
    public int getSize() {
        return 9 * Main.get().getBlockShopConfig().getInt("BLOCKSHOP.SIZE");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> button = Maps.newHashMap();

        Main.get().getBlockShopManager().getCategories().forEach(category -> {
            int slot = category.getSlot();
            button.put(slot, new BlockShopCategoryButton(category));
        });

        return button;
    }
}
