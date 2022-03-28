package me.danidev.core.listeners;

import me.danidev.core.Main;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PearlGlitchListener implements Listener {

    private final ImmutableSet<Material> blockedPearlTypes = Sets.immutableEnumSet(Material.IRON_FENCE, Material.FENCE,
            Material.NETHER_FENCE, Material.FENCE_GATE, Material.ACACIA_STAIRS, Material.BIRCH_WOOD_STAIRS,
            Material.BRICK_STAIRS, Material.COBBLESTONE_STAIRS, Material.DARK_OAK_STAIRS, Material.JUNGLE_WOOD_STAIRS,
            Material.NETHER_BRICK_STAIRS, Material.QUARTZ_STAIRS, Material.SANDSTONE_STAIRS, Material.SMOOTH_STAIRS,
            Material.SPRUCE_WOOD_STAIRS, Material.WOOD_STAIRS, Material.STEP, Material.THIN_GLASS, Material.WOOD_STEP,
            Material.FENCE_GATE, Material.TRAP_DOOR);

    private BlockFace[] faces;

    @SuppressWarnings("deprecation")
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPearlClip(PlayerTeleportEvent event) {
        if (!Main.get().getMainConfig().getBoolean("ENDERPEARL-GLITCH")) {
            return;
        }

        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            Location to = event.getTo();
            if (blockedPearlTypes.contains(to.getBlock().getType())) {
                Player player = event.getPlayer();
                boolean refund = Main.get().getMainConfig().getBoolean("ENDERPEARL-GLITCH");

                player.sendMessage(ChatColor.RED + "Pearl glitching detected" + (refund ? ", used Enderpearl has been refunded" : "") + ".");
                if (refund)
                    Main.get().getTimerManager().getEnderPearlTimer().refund(player);

                event.setCancelled(true);
                return;
            }
            if (to.getBlock().getType().equals(Material.STEP) || to.getBlock().getType().equals(Material.WOOD_STEP)) {
                to.setX(to.getBlockX() + 0.5D);
                to.setZ(to.getBlockZ() + 0.5D);
                to.setY(to.getBlockY() + 0.5D);
                event.setTo(to);
                return;
            }
            to.setX(to.getBlockX() + 0.5D);
            to.setZ(to.getBlockZ() + 0.5D);
            event.setTo(to);
        }
    }

    public boolean isGlitch(Location localLocation2) {
        final int radius = 1;
        BlockFace[] faces2;
        for (int length2 = (faces2 = this.faces).length, l = 0; l < length2; ++l) {
            final BlockFace BlockFace2 = faces2[l];
            final Location locationAhead = localLocation2.getBlock().getRelative(BlockFace2, 0).getLocation();
            for (int x = -radius; x <= radius; ++x) {
                for (int y = -radius; y <= radius; ++y) {
                    for (int z = -radius; z <= radius; ++z) {
                        final Location loc = locationAhead.getBlock().getRelative(x, y, z).getLocation();
                        if (loc.getBlock().getType().equals(Material.FENCE) || loc.getBlock().getType().equals(Material.FENCE_GATE) || loc.getBlock().getType().equals(Material.IRON_FENCE) || loc.getBlock().getType().equals(Material.NETHER_FENCE)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}