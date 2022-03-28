package me.danidev.core.utils.nametags;

import org.bukkit.entity.Player;

import java.util.List;

public interface NametagAdapter {

    List<BufferedNametag> getPlate(Player player);
}
