package me.danidev.core.managers.rank.impl;

import me.danidev.core.managers.rank.Rank;
import me.activated.core.api.player.PlayerData;
import me.activated.core.plugin.AquaCoreAPI;
import org.bukkit.OfflinePlayer;

public class AquaCore implements Rank {

    @Override
    public String getName(OfflinePlayer p0) {
        PlayerData data = AquaCoreAPI.INSTANCE.getPlayerData(p0.getUniqueId());
        return (data == null) ? "No Data" : data.getHighestRank().getName();
    }

    @Override
    public String getPrefix(OfflinePlayer p0) {
        PlayerData data = AquaCoreAPI.INSTANCE.getPlayerData(p0.getUniqueId());
        return (data == null) ? "No Data" : data.getHighestRank().getPrefix();
    }

    @Override
    public String getSuffix(OfflinePlayer p0) {
        PlayerData data = AquaCoreAPI.INSTANCE.getPlayerData(p0.getUniqueId());
        return (data == null) ? "No Data" : data.getHighestRank().getSuffix();
    }

    @Override
    public String getColor(OfflinePlayer p0) {
        PlayerData data = AquaCoreAPI.INSTANCE.getPlayerData(p0.getUniqueId());
        return (data == null) ? "No Data" : data.getHighestRank().getColor() + data.getHighestRank().getName();
    }
}
