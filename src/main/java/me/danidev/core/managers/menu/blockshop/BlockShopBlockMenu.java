package me.danidev.core.managers.menu.blockshop;

import me.danidev.core.Main;
import me.danidev.core.managers.menu.blockshop.buttons.BlockShopBlockButton;
import me.danidev.core.utils.menu.Button;
import me.danidev.core.utils.menu.Menu;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.Map;

@AllArgsConstructor
public class BlockShopBlockMenu extends Menu {

    private final String category;

    {
        setAutoUpdate(false);
        setUpdateAfterClick(false);
    }

    @Override
    public String getTitle(Player player) {
        return Main.get().getBlockShopConfig().getString("BLOCKS.TITLE")
                .replace("%CATEGORY%", this.category);
    }

    @Override
    public int getSize() {
        return 9 * Main.get().getBlockShopConfig().getInt("BLOCKS.SIZE");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> button = Maps.newHashMap();

        Main.get().getBlockShopManager().getBlocks().stream().filter(block -> block.getCategory().equalsIgnoreCase(this.category)).forEach(product -> {

            int slot = product.getSlot();

            button.put(slot, new BlockShopBlockButton(product));
        });

        return button;
    }
}
