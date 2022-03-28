package me.danidev.core.listeners;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.cuboid.CoordinatePair;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;

import net.minecraft.server.v1_7_R4.MobSpawnerAbstract;
import net.minecraft.util.gnu.trove.iterator.TObjectIntIterator;
import net.minecraft.util.gnu.trove.map.hash.TObjectIntHashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Optional;

public class MobstackListener extends BukkitRunnable implements Listener {

    private final String STACKED_PREFIX = "&ex";

    private final Table<CoordinatePair, EntityType, Integer> naturalSpawnStacks;
    private final TObjectIntHashMap<Location> spawnerStacks;
    private final Map<MobSpawnerAbstract, Integer> mobSpawnerAbstractIntegerMap;

    public MobstackListener(Main plugin) {
        this.mobSpawnerAbstractIntegerMap = Maps.newHashMap();
        this.naturalSpawnStacks = HashBasedTable.create();
        this.spawnerStacks = new TObjectIntHashMap<>();

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private CoordinatePair fromLocation(final Location location) {
        return new CoordinatePair(location.getWorld(), 81 * Math.round(location.getBlockX() / 81), 81 * Math.round(location.getBlockZ() / 81));
    }

    public void run() {
        for (final World world : Bukkit.getServer().getWorlds()) {
            if (world.getEnvironment() != World.Environment.THE_END && world.getLivingEntities() instanceof LivingEntity) {
                for (final LivingEntity entity : world.getLivingEntities()) {
                    if (entity.isValid() && !entity.isDead() && (entity instanceof Animals || entity instanceof Monster)) {
                        for (final Entity nearby : entity.getNearbyEntities(8.0, 8.0, 8.0)) {
                            if (nearby instanceof LivingEntity && !nearby.isDead() && nearby.isValid() && (nearby instanceof Animals || nearby instanceof Monster) && this.stack((LivingEntity) nearby, entity)) {
                                if (this.naturalSpawnStacks.containsValue(entity.getEntityId())) {
                                    for (final Map.Entry<CoordinatePair, Integer> entry : this.naturalSpawnStacks.column(entity.getType()).entrySet()) {
                                        if (entry.getValue() == entity.getEntityId()) {
                                            this.naturalSpawnStacks.put(entry.getKey(), entity.getType(), nearby.getEntityId());
                                            break;
                                        }
                                    }
                                    break;
                                }
                                if (!this.mobSpawnerAbstractIntegerMap.containsValue(entity.getEntityId())) {
                                    break;
                                }
                                for (final Map.Entry<MobSpawnerAbstract, Integer> entry2 : this.mobSpawnerAbstractIntegerMap.entrySet()) {
                                    if (entry2.getValue() == entity.getEntityId()) {
                                        this.mobSpawnerAbstractIntegerMap.put(entry2.getKey(), nearby.getEntityId());
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onSpawnerSpawn(final SpawnerSpawnEvent event) {
        if (event.getEntityType().equals(EntityType.ENDERMAN)) {
            return;
        }
        final CreatureSpawner spawner = event.getSpawner();
        final World world = spawner.getWorld();
        if (world != null || (world != null && world.getEnvironment().equals(World.Environment.THE_END))) {
            return;
        }
        final Location location = spawner.getLocation();
        final Optional<Integer> entityIdOptional = Optional.of(this.spawnerStacks.get(location));
        final int entityId = entityIdOptional.get();
        final net.minecraft.server.v1_7_R4.Entity nmsTarget = ((CraftWorld) location.getWorld()).getHandle().getEntity(entityId);
        final Entity target = (nmsTarget != null) ? nmsTarget.getBukkitEntity() : null;
        if (target instanceof LivingEntity) {
            final LivingEntity targetLiving = (LivingEntity) target;
            int stackedQuantity = this.getStackedQuantity(targetLiving);
            if (stackedQuantity == -1) {
                stackedQuantity = 1;
            }
            if (stackedQuantity <= 200) {
                this.setStackedQuantity(targetLiving, ++stackedQuantity);
                event.setCancelled(true);
                return;
            }
        }
        this.spawnerStacks.put(location, event.getEntity().getEntityId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onCreatureSpawn(final CreatureSpawnEvent event) {
        final EntityType entityType = event.getEntityType();
        if (entityType.equals(EntityType.ENDERMAN)) {
            return;
        }
        switch (event.getSpawnReason()) {
            case NATURAL:
            case CHUNK_GEN:
            case DEFAULT: {
                final Location location = event.getLocation();
                final CoordinatePair coordinatePair = this.fromLocation(location);
                final Optional<Integer> entityIdOptional = Optional.ofNullable(this.naturalSpawnStacks.get(coordinatePair, entityType));
                if (entityIdOptional.isPresent()) {
                    final int entityId = entityIdOptional.get();
                    final net.minecraft.server.v1_7_R4.Entity nmsTarget = ((CraftWorld) location.getWorld()).getHandle().getEntity(entityId);
                    final Entity target = (nmsTarget == null) ? null : nmsTarget.getBukkitEntity();
                    if (target instanceof LivingEntity) {
                        final LivingEntity targetLiving = (LivingEntity) target;
                        boolean canSpawn;
                        if (targetLiving instanceof Ageable) {
                            canSpawn = ((Ageable) targetLiving).isAdult();
                        } else {
                            canSpawn = (!(targetLiving instanceof Zombie) || !((Zombie) targetLiving).isBaby());
                        }
                        if (canSpawn) {
                            int stackedQuantity = this.getStackedQuantity(targetLiving);
                            if (stackedQuantity == -1) {
                                stackedQuantity = 1;
                            }
                            if (stackedQuantity < 200) {
                                this.setStackedQuantity(targetLiving, ++stackedQuantity);
                                event.setCancelled(true);
                                return;
                            }
                        }
                    }
                }
                this.naturalSpawnStacks.put(coordinatePair, entityType, event.getEntity().getEntityId());
                break;
            }
            default:
                break;
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDeath(final EntityDeathEvent event) {
        final LivingEntity livingEntity = event.getEntity();
        int stackedQuantity = this.getStackedQuantity(livingEntity);
        if (stackedQuantity > 1) {
            final LivingEntity respawned = (LivingEntity) livingEntity.getWorld().spawnEntity(livingEntity.getLocation(), event.getEntityType());
            this.setStackedQuantity(respawned, Math.min(200, --stackedQuantity));
            if (respawned instanceof Ageable) {
                ((Ageable) respawned).setAdult();
            }
            if (respawned instanceof Zombie) {
                ((Zombie) respawned).setBaby(false);
            }
            if (this.spawnerStacks.containsValue(livingEntity.getEntityId())) {
                final TObjectIntIterator<Location> iterator = this.spawnerStacks.iterator();
                while (iterator.hasNext()) {
                    iterator.advance();
                    if (iterator.value() == livingEntity.getEntityId()) {
                        iterator.setValue(respawned.getEntityId());
                    }
                }
            }
        }
    }

    private int getStackedQuantity(final LivingEntity livingEntity) {
        String customName = livingEntity.getCustomName();
        if (customName != null && customName.contains(STACKED_PREFIX)) {
            customName = customName.replace(STACKED_PREFIX, "");
            customName = ChatColor.stripColor(customName);
            return Integer.parseInt(customName);
        }
        return -1;
    }

    private boolean stack(final LivingEntity tostack, final LivingEntity toremove) {
        Integer newStack = 1;
        Integer removeStack = 1;
        if (this.hasStack(tostack)) {
            newStack = this.getStackedQuantity(tostack);
        }
        if (this.hasStack(toremove)) {
            removeStack = this.getStackedQuantity(toremove);
        } else if (this.getStackedQuantity(toremove) == -1 && toremove.getCustomName() != null && toremove.getCustomName().contains(ChatColor.WHITE.toString())) {
            return false;
        }
        if (toremove.getType() != tostack.getType()) {
            return false;
        }
        if (toremove.getType() == EntityType.ZOMBIE) {
            return false;
        }
        if (toremove.getType() == EntityType.SLIME || toremove.getType() == EntityType.MAGMA_CUBE || tostack.getType() == EntityType.SLIME || tostack.getType() == EntityType.MAGMA_CUBE || tostack.getType() == EntityType.VILLAGER) {
            return false;
        }
        toremove.remove();
        this.setStackedQuantity(tostack, Math.min(newStack + removeStack, 200));
        return true;
    }

    private boolean hasStack(final LivingEntity livingEntity) {
        return this.getStackedQuantity(livingEntity) != -1;
    }

    private void setStackedQuantity(final LivingEntity livingEntity, final int quantity) {
        if (quantity > 200) {
            return;
        }
        if (quantity < 1) {
            return;
        }
        if (quantity <= 1) {
            livingEntity.setCustomName(null);
        } else {
            livingEntity.setCustomName(CC.translate(STACKED_PREFIX + quantity));
            livingEntity.setCustomNameVisible(false);
        }
    }
}
