package me.danidev.core.utils.visualise;

import com.google.common.collect.Iterables;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;

abstract class BlockFiller {

    abstract VisualBlockData generate(final Player p0, final Location p1);

    ArrayList<VisualBlockData> bulkGenerate(final Player player, final Iterable<Location> locations) {
        final ArrayList<VisualBlockData> data = new ArrayList<>(Iterables.size(locations));
        for (final Location location : locations) {
            data.add(this.generate(player, location));
        }
        return data;
    }
}
