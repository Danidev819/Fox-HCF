package me.danidev.core.managers.menu.blockshop.buttons;

import me.danidev.core.Main;
import me.danidev.core.managers.blockshop.block.BlockShopBlock;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.item.ItemBuilder;
import me.danidev.core.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class BlockShopBlockButton extends Button {

    private final BlockShopBlock block;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(block.getIcon())
                .amount(block.getAmount())
                .data(block.getData())
                .name(block.getDisplayName())
                .lore(block.getDescription())
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        int balance = Main.get().getEconomyManager().getBalance(player.getUniqueId());

        if (balance < block.getPrice()) {
            playFail(player);
            player.sendMessage(CC.translate("&cYou don't have enough money to purchase this item!"));
            return;
        }

        Main.get().getEconomyManager().setBalance(player.getUniqueId(), balance - block.getPrice());

        playSuccess(player);
        addItem(player);

        player.closeInventory();
        player.sendMessage(CC.translate("&aPurchase complete! Your new balance is: $"
                + Main.get().getEconomyManager().getBalance(player.getUniqueId())));
    }

    private void addItem(Player player) {
        ItemStack itemStack = new ItemBuilder(getButtonItem(player).getType())
                .amount(getButtonItem(player).getAmount())
                .data(getButtonItem(player).getDurability())
                .build();
        player.getInventory().addItem(itemStack);
    }
}
