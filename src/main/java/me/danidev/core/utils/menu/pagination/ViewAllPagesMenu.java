package me.danidev.core.utils.menu.pagination;

import me.danidev.core.utils.menu.Button;
import me.danidev.core.utils.menu.Menu;
import me.danidev.core.utils.menu.buttons.BackButton;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ViewAllPagesMenu extends Menu {

    @NonNull @Getter PaginatedMenu menu;

    @Override
    public String getTitle(Player player) {
        return "Jump to page";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new BackButton(menu));

        int index = 10;

        for (int i = 1; i <= menu.getPages(player); i++) {
            buttons.put(index++, new JumpToPageButton(i, menu, menu.getPage() == i));

            if ((index - 8) % 9 == 0) {
                index += 2;
            }
        }

        return buttons;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }
}
