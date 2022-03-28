package me.danidev.core.utils;

import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.craftbukkit.v1_7_R4.entity.*;
import java.util.*;
import javax.annotation.*;
import com.google.common.base.*;
import net.minecraft.server.v1_7_R4.*;
import org.bukkit.*;

public enum ParticleEffect
{
    HUGE_EXPLODE("hugeexplosion", 0),
    LARGE_EXPLODE("largeexplode", 1),
    FIREWORK_SPARK("fireworksSpark", 2),
    AIR_BUBBLE("bubble", 3),
    SUSPEND("suspend", 4),
    DEPTH_SUSPEND("depthSuspend", 5),
    TOWN_AURA("townaura", 6),
    CRITICAL_HIT("crit", 7),
    MAGIC_CRITICAL_HIT("magicCrit", 8),
    MOB_SPELL("mobSpell", 9),
    MOB_SPELL_AMBIENT("mobSpellAmbient", 10),
    SPELL("spell", 11),
    INSTANT_SPELL("instantSpell", 12),
    BLUE_SPARKLE("witchMagic", 13),
    NOTE_BLOCK("note", 14),
    ENDER("portal", 15),
    ENCHANTMENT_TABLE("enchantmenttable", 16),
    EXPLODE("explode", 17),
    FIRE("flame", 18),
    LAVA_SPARK("lava", 19),
    FOOTSTEP("footstep", 20),
    SPLASH("splash", 21),
    LARGE_SMOKE("largesmoke", 22),
    CLOUD("cloud", 23),
    REDSTONE_DUST("reddust", 24),
    SNOWBALL_HIT("snowballpoof", 25),
    DRIP_WATER("dripWater", 26),
    DRIP_LAVA("dripLava", 27),
    SNOW_DIG("snowshovel", 28),
    SLIME("slime", 29),
    HEART("heart", 30),
    ANGRY_VILLAGER("angryVillager", 31),
    GREEN_SPARKLE("happyVillager", 32),
    ICONCRACK("iconcrack", 33),
    TILECRACK("tilecrack", 34);

    private final String name;
    @Deprecated
    private final int id;
    private static final ParticleEffect[] $VALUES;

    private ParticleEffect(final String name, final int id) {
        this.name = name;
        this.id = id;
    }

    @Deprecated
    String getName() {
        return this.name;
    }

    @Deprecated
    public int getId() {
        return this.id;
    }

    public void display(final Player player, final float n, final float n2, final float n3, final float n4, final int n5) {
        this.display(player, n, n2, n3, 0.0f, 0.0f, 0.0f, n4, n5);
    }

    public void display(final Player player, final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final int n8) {
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket((Packet)this.createPacket(n, n2, n3, n4, n5, n6, n7, n8));
    }

    public void display(final Player player, final Location location, final float n, final int n2) {
        this.display(player, location, 0.0f, 0.0f, 0.0f, n, n2);
    }

    public void display(final Player player, final Location location, final float n, final float n2, final float n3, final float n4, final int n5) {
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket((Packet)this.createPacket(location, n, n2, n3, n4, n5));
    }

    public void broadcast(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final int n8) {
        final PacketPlayOutWorldParticles packet = this.createPacket(n, n2, n3, n4, n5, n6, n7, n8);
        final Iterator<Player> iterator = (Iterator<Player>)Bukkit.getOnlinePlayers().iterator();
        while (iterator.hasNext()) {
            ((CraftPlayer)iterator.next()).getHandle().playerConnection.sendPacket((Packet)packet);
        }
    }

    public void broadcast(final Location location, final float n, final float n2, final float n3, final float n4, final int n5) {
        this.broadcast(location, n, n2, n3, n4, n5, null, null);
    }

    public void broadcast(final Location location, final float n, final float n2, final float n3, final float n4, final int n5, @Nullable final Player player) {
        this.broadcast(location, n, n2, n3, n4, n5, player, null);
    }

    public void broadcast(final Location location, final float n, final float n2, final float n3, final float n4, final int n5, @Nullable final Player player, @Nullable final Predicate<Player> predicate) {
        final PacketPlayOutWorldParticles packet = this.createPacket(location, n, n2, n3, n4, n5);
        for (final Player player2 : Bukkit.getOnlinePlayers()) {
            if ((player == null || player2.canSee(player)) && (predicate == null || predicate.apply(player2))) {
                ((CraftPlayer)player2).getHandle().playerConnection.sendPacket((Packet)packet);
            }
        }
    }

    public void sphere(@Nullable final Player player, final Location location, final float n) {
        this.sphere(player, location, n, 20.0f, 2);
    }

    public void sphere(@Nullable final Player player, final Location location, final float n, final float n2, final int n3) {
        Preconditions.checkNotNull((Object)location, (Object)"Location cannot be null");
        Preconditions.checkArgument(n >= 0.0f, (Object)"Radius must be positive");
        Preconditions.checkArgument(n2 >= 0.0f, (Object)"Density must be positive");
        Preconditions.checkArgument(n3 >= 0, (Object)"Intensity must be positive");
        final float n4 = 180.0f / n2;
        final float n5 = 360.0f / n2;
        final World world = location.getWorld();
        for (int n6 = 0; n6 < n2; ++n6) {
            for (int n7 = 0; n7 < n2; ++n7) {
                final float n8 = -90.0f + n7 * n4;
                final float n9 = -180.0f + n6 * n5;
                final Location location2 = new Location(world, (double)(n * MathHelper.sin(-n9 * 0.017453292f - 3.1415927f) * -MathHelper.cos(-n8 * 0.017453292f) + (float)location.getX()), (double)(n * MathHelper.sin(-n8 * 0.017453292f) + (float)location.getY()), (double)(n * MathHelper.cos(-n9 * 0.017453292f - 3.1415927f) * -MathHelper.cos(-n8 * 0.017453292f) + (float)location.getZ()));
                if (player == null) {
                    this.broadcast(location2, 0.0f, 0.0f, 0.0f, 0.0f, n3);
                }
                else {
                    this.display(player, location2, 0.0f, 0.0f, 0.0f, 0.0f, n3);
                }
            }
        }
    }

    private PacketPlayOutWorldParticles createPacket(final Location location, final float n, final float n2, final float n3, final float n4, final int n5) {
        return this.createPacket((float)location.getX(), (float)location.getY(), (float)location.getZ(), n, n2, n3, n4, n5);
    }

    private PacketPlayOutWorldParticles createPacket(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final int n8) {
        Preconditions.checkArgument(n7 >= 0.0f, (Object)"Speed must be positive");
        Preconditions.checkArgument(n8 > 0, (Object)"Cannot use less than one particle.");
        return new PacketPlayOutWorldParticles(this.name, n, n2, n3, n4, n5, n6, n7, n8);
    }

    static {
        $VALUES = new ParticleEffect[] { ParticleEffect.HUGE_EXPLODE, ParticleEffect.LARGE_EXPLODE, ParticleEffect.FIREWORK_SPARK, ParticleEffect.AIR_BUBBLE, ParticleEffect.SUSPEND, ParticleEffect.DEPTH_SUSPEND, ParticleEffect.TOWN_AURA, ParticleEffect.CRITICAL_HIT, ParticleEffect.MAGIC_CRITICAL_HIT, ParticleEffect.MOB_SPELL, ParticleEffect.MOB_SPELL_AMBIENT, ParticleEffect.SPELL, ParticleEffect.INSTANT_SPELL, ParticleEffect.BLUE_SPARKLE, ParticleEffect.NOTE_BLOCK, ParticleEffect.ENDER, ParticleEffect.ENCHANTMENT_TABLE, ParticleEffect.EXPLODE, ParticleEffect.FIRE, ParticleEffect.LAVA_SPARK, ParticleEffect.FOOTSTEP, ParticleEffect.SPLASH, ParticleEffect.LARGE_SMOKE, ParticleEffect.CLOUD, ParticleEffect.REDSTONE_DUST, ParticleEffect.SNOWBALL_HIT, ParticleEffect.DRIP_WATER, ParticleEffect.DRIP_LAVA, ParticleEffect.SNOW_DIG, ParticleEffect.SLIME, ParticleEffect.HEART, ParticleEffect.ANGRY_VILLAGER, ParticleEffect.GREEN_SPARKLE, ParticleEffect.ICONCRACK, ParticleEffect.TILECRACK };
    }
}