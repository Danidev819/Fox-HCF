package me.danidev.core.managers.rank;

import org.bukkit.OfflinePlayer;

public interface Rank {

    String getName(OfflinePlayer offlinePlayer);
    String getPrefix(OfflinePlayer offlinePlayer);
    String getSuffix(OfflinePlayer offlinePlayer);
    String getColor(OfflinePlayer offlinePlayer);

}
