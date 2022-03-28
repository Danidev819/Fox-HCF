package me.danidev.core.managers.menu.suggestion.buttons;

import me.danidev.core.managers.suggestions.Suggestion;
import me.danidev.core.utils.item.ItemBuilder;
import me.danidev.core.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class SuggestionButton extends Button {

    private final Suggestion suggestion;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.PAPER)
                .name("&6&l" + suggestion.getID())
                .lore(
                        "",
                        "&bAuthor&7: &f" + suggestion.getAuthor(),
                        "&bSuggestion&7: &f" + suggestion.getSuggestion(),
                        ""
                )
                .build();
    }
}
