package me.danidev.core.managers.menu.keyall.buttons;

import me.danidev.core.Main;
import me.danidev.core.utils.item.ItemBuilder;
import me.danidev.core.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
@AllArgsConstructor
public class NormalKeyAllButton extends Button {

    private String name;
    private String displayName;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.STORAGE_MINECART)
                .name(displayName)
                .lore(Main.get().getLangConfig().getStringList("KEY_ALL." + name))
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        final List<String> commands = Main.get().getLangConfig().getStringList("NORMAL-KEYALL.COMMANDS");
        for (final String command : commands) {
            Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), command);
            player.closeInventory();
        }
    }
}
