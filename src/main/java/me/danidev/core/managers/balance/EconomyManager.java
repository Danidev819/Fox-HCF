package me.danidev.core.managers.balance;

import gnu.trove.map.TObjectIntMap;

import java.util.UUID;

public interface EconomyManager {
    char ECONOMY_SYMBOL = '$';

    TObjectIntMap<UUID> getBalanceMap();

    int getBalance(final UUID p0);

    int setBalance(final UUID p0, final int p1);

    int addBalance(final UUID p0, final int p1);

    int subtractBalance(final UUID p0, final int p1);

    void reloadEconomyData();

    void saveEconomyData();
}
