package me.danidev.core.utils.cuboid;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

public class CoordinatePair {

    private final String worldName;
    private final int x;
    private final int z;

    public CoordinatePair(final Block block) {
        this(block.getWorld(), block.getX(), block.getZ());
    }

    public CoordinatePair(final World world, final int x, final int z) {
        this.worldName = world.getName();
        this.x = x;
        this.z = z;
    }

    public String getWorldName() {
        return this.worldName;
    }

    public World getWorld() {
        return Bukkit.getWorld(this.worldName);
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CoordinatePair)) {
            return false;
        }
        final CoordinatePair that = (CoordinatePair) o;
        if (this.x != that.x) {
            return false;
        }
        if (this.z != that.z) {
            return false;
        }
        if (this.worldName != null) {
            return this.worldName.equals(that.worldName);
        }
        return that.worldName == null;
    }

    @Override
    public int hashCode() {
        int result = (this.worldName != null) ? this.worldName.hashCode() : 0;
        result = 31 * result + this.x;
        result = 31 * result + this.z;
        return result;
    }
}
