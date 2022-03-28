package me.danidev.core.managers.menu.leaderboard.buttons;

import me.danidev.core.Main;
import me.danidev.core.managers.faction.type.PlayerFaction;
import me.danidev.core.utils.item.ItemBuilder;
import me.danidev.core.utils.menu.Button;
import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class LeaderboardKothsButton extends Button {

    private final Comparator<PlayerFaction> KOTHS_COMPARATOR = Comparator.comparingInt(PlayerFaction::getKothCaptures);

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> lore = Lists.newArrayList();

        List<PlayerFaction> data = Main.get().getFactionManager().getFactions().stream()
                .filter(x -> x instanceof PlayerFaction)
                .map(fac -> (PlayerFaction) fac)
                .filter(x -> x.getKothCaptures() > 0).distinct()
                .sorted(KOTHS_COMPARATOR)
                .collect(Collectors.toList());

        Collections.reverse(data);

        lore.add("&7&m----------------------");

        if (data.isEmpty()) {
            lore.add("&cNone");
        }
        else {
            for (int i = 0; i < 10 && i < data.size(); ++i) {
                PlayerFaction next = data.get(i);
                lore.add("&7" + (i + 1) + ") &b" + next.getName() + ": &f" + next.getKothCaptures());
            }
        }

        lore.add("&7&m----------------------");

        return new ItemBuilder(Material.BOOK)
                .name("&b&lFaction Koths")
                .lore(lore)
                .build();
    }
}
