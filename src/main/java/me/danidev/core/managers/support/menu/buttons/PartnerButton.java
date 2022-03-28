package me.danidev.core.managers.support.menu.buttons;

import me.danidev.core.Main;
import me.danidev.core.managers.support.Partner;
import me.danidev.core.managers.support.PartnerManager;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.file.FileConfig;
import me.danidev.core.utils.item.ItemBuilder;
import me.danidev.core.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class PartnerButton extends Button {

    private final Partner partner;
    private final FileConfig suggestionsConfig = Main.get().getSuggestionsConfig();

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.SKULL_ITEM)
                .data(3)
                .owner(partner.getPartner())
                .name(partner.getDisplayName())
                .lore(partner.getDescription())
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        PartnerManager partnerManager = Main.get().getPartnerManager();

        if (!partnerManager.getSupported().containsKey(player.getUniqueId())) {
            Main.get().getPartnerManager().setSupported(player.getUniqueId(), false);
        }
        if (partnerManager.isSupported(player.getUniqueId())) {
            playFail(player);
            CC.player(player, Main.get().getSupportConfig().getConfiguration().getString("SUPPORT-MESSAGES.ALREADY"));
            return;
        }
        playSuccess(player);
        partnerManager.setSupported(player.getUniqueId(), true);
        partner.getCommands().forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName())));
        player.sendMessage(CC.translate(Main.get().getSupportConfig().getConfiguration().getString("SUPPORT-MESSAGES.SUPPORT")
                .replace("%partner%", partner.getPartner())));
        player.closeInventory();
    }
}