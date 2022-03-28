package me.danidev.core.utils;

import org.bukkit.scoreboard.Team;

import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.Bukkit;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.entity.Firework;
import org.bukkit.Location;
import org.bukkit.FireworkEffect;
import java.util.Set;
import org.bukkit.GameMode;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PlayerUtil {

	public static void sendToHub(Player player) {
		teleport(player);
	}

	public static void teleport(Player player) {
		player.performCommand("hub");
	}

	public static void setFirstSlotOfType(final Player player, final Material type, final ItemStack itemStack) {
		for (int i = 0; i < player.getInventory().getContents().length; ++i) {
			final ItemStack itemStack2 = player.getInventory().getContents()[i];
			if (itemStack2 == null || itemStack2.getType() == type || itemStack2.getType() == Material.AIR) {
				player.getInventory().setItem(i, itemStack);
				break;
			}
		}
	}

	public static void decrement(Player player) {
		ItemStack itemStack = player.getItemInHand();
		if (itemStack.getAmount() <= 1) player.setItemInHand(new ItemStack(Material.AIR, 1));
		else itemStack.setAmount(itemStack.getAmount() - 1);
		player.updateInventory();
	}

	public static void denyMovement(final Player player) {
		player.setWalkSpeed(0.0f);
		player.setFlySpeed(0.0f);
		player.setFoodLevel(1);
		player.setSprinting(false);
		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200));
		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 200));
	}

	public static void allowMovement(final Player player) {
		player.setWalkSpeed(0.2f);
		player.setFlySpeed(0.1f);
		player.setFoodLevel(20);
		player.setSprinting(true);
		player.removePotionEffect(PotionEffectType.JUMP);
		player.removePotionEffect(PotionEffectType.BLINDNESS);
	}

	public static void clearPlayer(final Player player) {
		player.setHealth(20.0);
		player.setFoodLevel(20);
		player.setSaturation(12.8f);
		player.setMaximumNoDamageTicks(20);
		player.setFireTicks(0);
		player.setFallDistance(0.0f);
		player.setLevel(0);
		player.setExp(0.0f);
		player.setWalkSpeed(0.2f);
		player.getInventory().setHeldItemSlot(0);
		player.setAllowFlight(false);
		player.setFlying(false);
		player.getInventory().clear();
		player.getInventory().setArmorContents((ItemStack[]) null);
		player.closeInventory();
		player.setGameMode(GameMode.SURVIVAL);
		player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
		player.updateInventory();
	}

	public static void sendMessage(final String message, final Player... players) {
		for (final Player player : players) {
			player.sendMessage(message);
		}
	}

	public static void sendMessage(final String message, final Set<Player> players) {
		for (final Player player : players) {
			player.sendMessage(message);
		}
	}

	public static void sendFirework(final FireworkEffect effect, final Location location) {
		final Firework f = (Firework) location.getWorld().spawn(location, Firework.class);
		final FireworkMeta fm = f.getFireworkMeta();
		fm.addEffect(effect);
		f.setFireworkMeta(fm);
		try {
			final Class<?> entityFireworkClass = getClass("net.minecraft.server.", "EntityFireworks");
			final Class<?> craftFireworkClass = getClass("org.bukkit.craftbukkit.", "entity.CraftFirework");
			final Object firework = craftFireworkClass.cast(f);
			final Method handle = firework.getClass().getMethod("getHandle", (Class<?>[]) new Class[0]);
			final Object entityFirework = handle.invoke(firework, new Object[0]);
			final Field expectedLifespan = entityFireworkClass.getDeclaredField("expectedLifespan");
			final Field ticksFlown = entityFireworkClass.getDeclaredField("ticksFlown");
			ticksFlown.setAccessible(true);
			ticksFlown.setInt(entityFirework, expectedLifespan.getInt(entityFirework) - 1);
			ticksFlown.setAccessible(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static Class<?> getClass(final String prefix, final String nmsClassString) throws ClassNotFoundException {
		final String version = String
				.valueOf(Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3]) + ".";
		final String name = String.valueOf(prefix) + version + nmsClassString;
		final Class<?> nmsClass = Class.forName(name);
		return nmsClass;
	}

	private static Team getTeam(final Scoreboard board, final String prefix, final String color) {
		Team team = board.getTeam(prefix);
		if (team == null) {
			team = board.registerNewTeam(prefix);
			team.setPrefix(CC.translate(color));
		}
		return team;
	}

	public static void unregister(final Scoreboard board, final String name) {
		final Team team = board.getTeam(name);
		if (team != null) {
			team.unregister();
		}
	}
}
