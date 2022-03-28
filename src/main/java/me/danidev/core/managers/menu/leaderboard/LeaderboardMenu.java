package me.danidev.core.managers.menu.leaderboard;

import me.danidev.core.managers.menu.leaderboard.buttons.*;
import me.danidev.core.utils.menu.Button;
import me.danidev.core.utils.menu.Menu;
import com.google.common.collect.Maps;
import org.bukkit.entity.Player;

import java.util.Map;

public class LeaderboardMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "&6&lLeaderboard";
    }

    @Override
    public int getSize() {
        return 9;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        buttons.put(2, new LeaderboardKillsButton());
        buttons.put(3, new LeaderboardDeathsButton());
        buttons.put(4, new LeaderboardPointsButton());
        buttons.put(5, new LeaderboardBalanceButton());
        buttons.put(6, new LeaderboardKothsButton());

        return buttons;
    }
}
