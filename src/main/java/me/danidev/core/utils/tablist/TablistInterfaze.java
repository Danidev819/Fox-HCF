package me.danidev.core.utils.tablist;

import net.minecraft.util.com.google.common.collect.Table;
import org.bukkit.entity.Player;

public interface TablistInterfaze
{
    Table<Integer, Integer, String> getEntries(final Player p0);
    
    String getHeader(final Player p0);
    
    String getFooter(final Player p0);
}