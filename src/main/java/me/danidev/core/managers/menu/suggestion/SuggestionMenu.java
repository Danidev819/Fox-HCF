package me.danidev.core.managers.menu.suggestion;

import me.danidev.core.Main;
import me.danidev.core.managers.menu.suggestion.buttons.SuggestionButton;
import me.danidev.core.managers.suggestions.Suggestion;
import me.danidev.core.utils.menu.Button;
import me.danidev.core.utils.menu.buttons.CloseButton;
import me.danidev.core.utils.menu.pagination.PageButton;
import me.danidev.core.utils.menu.pagination.PaginatedMenu;
import com.google.common.collect.Maps;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class SuggestionMenu extends PaginatedMenu {

    {
        setAutoUpdate(false);
        setUpdateAfterClick(false);
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Suggestions";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        List<Suggestion> suggestions = Main.get().getSuggestionManager().getSuggestions();

        for (int i = 0; i < suggestions.size(); i++) {
            buttons.put(i, new SuggestionButton(suggestions.get(i)));
        }

        return buttons;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        buttons.put(getSize() - 6, new PageButton(-1, this));
        buttons.put(getSize() - 5, new CloseButton());
        buttons.put(getSize() - 4, new PageButton(1, this));
        return buttons;
    }

    @Override
    public int getSize() {
        return 9 * 6;
    }

    @Override
    public int getMaxItemsPerPage(Player player) {
        return 9 * 5;
    }
}
