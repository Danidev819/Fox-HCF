package me.danidev.core.utils.staff;

import java.util.ArrayList;
import java.util.UUID;

public enum OptionType {
    DAMAGE("Damage", new ArrayList<UUID>()),
    PLACE("Place", new ArrayList<UUID>()),
    BREAK("Break", new ArrayList<UUID>()),
    PICKUP("Pickup", new ArrayList<UUID>()),
    INTERACT("Interact", new ArrayList<UUID>()),
    CHEST("Chest", new ArrayList<UUID>());

    private String name;
    private ArrayList<UUID> players;

    OptionType(final String name, final ArrayList<UUID> players) {
        this.name = name;
        this.players = players;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public ArrayList<UUID> getPlayers() {
        return this.players;
    }

    public void setPlayers(final ArrayList<UUID> players) {
        this.players = players;
    }
}
