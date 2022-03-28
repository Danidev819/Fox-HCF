package me.danidev.core.managers.killstreaks;

import me.danidev.core.Main;
import me.danidev.core.utils.Messager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class KillStreaks {
	
	public static void x3(Player player) {
		player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 5));
		Messager.broadcast(streakMessage(player, "x5 Gapples", 3));
	}

	public static void x6(Player player) {
		int time = 60;
		int seconds = time * 20;
		int minutes = seconds * 8;
		player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, minutes, 0), true);
		Messager.broadcast(streakMessage(player, "Fire Resistance (8m)", 6));
	}

	public static void x10(Player player) {
		int time = 60;
		int seconds = time * 20;
		int minutes = seconds * 8;
		player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, minutes, 0), true);
		Messager.broadcast(streakMessage(player, "Invisibility (8m)", 10));
	}

	public static void x15(Player player) {
		player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 8));
		Messager.broadcast(streakMessage(player, "x8 Gapples", 15));
	}

	public static void x20(Player player) {
		int time = 60;
		int seconds = time * 20;
		int minutes = seconds * 8;
		player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, minutes, 0), true);
		Messager.broadcast(streakMessage(player, "Damage Resistance (8m)", 20));
	}

	public static void x25(Player player) {
		int time = 60;
		int seconds = time * 20;
		int minutes = seconds * 3;
		player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, minutes, 0), true);
		Messager.broadcast(streakMessage(player, "Strength (3m)", 25));
	}

	public static void x30(Player player) {
		player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1));
		Messager.broadcast(streakMessage(player, "x1 Gopple", 30));
	}

	public static void x45(Player player) {
		player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 5, (short) 1));
		player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 32));
		Messager.broadcast(streakMessage(player, "x5 Gopples&7 & &c&lx32 Gapples", 45));
	}

	private static String streakMessage(Player player, String string, Integer killStreak) {
		return Main.get().getLangConfig().getString("KILL_STREAK")
				.replace("%PLAYER%", player.getName())
				.replace("%ITEM%", string)
				.replace("%STREAK%", killStreak.toString());
	}
}
