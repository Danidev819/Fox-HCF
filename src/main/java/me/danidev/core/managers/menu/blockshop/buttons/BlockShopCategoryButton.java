package me.danidev.core.managers.menu.blockshop.buttons;

import me.danidev.core.managers.blockshop.category.BlockShopCategory;
import me.danidev.core.managers.menu.blockshop.BlockShopBlockMenu;
import me.danidev.core.utils.item.ItemBuilder;
import me.danidev.core.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class BlockShopCategoryButton extends Button {

    private final BlockShopCategory category;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(category.getMaterial())
                .data(category.getData())
                .name(category.getDisplayName())
                .lore(category.getDescription())
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        if (!category.isSubMenu()) return;

        playNeutral(player);
        new BlockShopBlockMenu(category.getCategory()).openMenu(player);
    }
}
