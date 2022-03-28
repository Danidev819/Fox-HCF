package me.danidev.core.managers.menu.media.buttons;

import me.danidev.core.Main;
import me.danidev.core.utils.item.ItemBuilder;
import me.danidev.core.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class MediaButton extends Button {

    private final String name;
    private final String displayName;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.SKULL_ITEM)
                .data(3)
                .owner("CCTV")
                .name(displayName)
                .lore(Main.get().getLangConfig().getStringList("MEDIA." + name))
                .build();
    }
}
