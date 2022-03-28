package me.danidev.core.listeners.fixes;

import me.danidev.core.Main;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.games.koth.faction.EventFaction;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.Listener;

public class EnderPearlFix implements Listener
{

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerEnderpearl(final PlayerInteractEvent event) {
        final Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR) {
            final ItemStack itemStack = event.getItem();
            if (itemStack != null && itemStack.getType() == Material.ENDER_PEARL) {
                Player player = event.getPlayer();
                Faction factionAt = Main.get().getFactionManager().getFactionAt(player.getLocation());
                if (factionAt instanceof EventFaction) {
                    event.setUseItemInHand(Event.Result.DENY);
                    player.sendMessage(ChatColor.RED + "You cannot " + Main.get().getTimerManager().enderPearlTimer.getDisplayName() + ChatColor.RED + " in an active event zone.");
                }
            }
        }
    }
}
