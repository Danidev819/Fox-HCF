package me.danidev.core.utils.menu.buttons;

import me.danidev.core.utils.item.ItemBuilder;
import me.danidev.core.utils.menu.Button;
import me.danidev.core.utils.menu.callback.TypeCallback;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class ConfirmationButton extends Button {

    private final boolean confirm;
    private final TypeCallback<Boolean> callback;
    private final boolean closeAfterResponse;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.WOOL)
                .data(this.confirm ? 5 : 14)
                .name(this.confirm ? "&aConfirm" : "&cCancel")
                .build();
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType, int hb) {
        if (this.confirm) {
            player.playSound(player.getLocation(), Sound.NOTE_PIANO, 20f, 0.1f);
        }
        else {
            player.playSound(player.getLocation(), Sound.DIG_GRAVEL, 20f, 0.1F);
        }

        if (this.closeAfterResponse) {
            player.closeInventory();
        }

        this.callback.callback(this.confirm);
    }
}
