package me.danidev.core.listeners;

import me.danidev.core.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import org.bukkit.block.Furnace;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.event.Listener;

public class FurnaceListener implements Listener {

	public FurnaceListener(Main plugin) {
		ShapedRecipe shapedRecipe = new ShapedRecipe(new ItemStack(Material.SPECKLED_MELON, 1));

		shapedRecipe.shape("AAA", "CBA", "AAA").setIngredient('B', Material.MELON).setIngredient('C', Material.GOLD_NUGGET);
		Bukkit.getServer().addRecipe(shapedRecipe);

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private void startUpdate(Furnace tile) {
		new BukkitRunnable() {
			public void run() {
				if (tile.getCookTime() > 0 || tile.getBurnTime() > 0) {
					tile.setCookTime((short) (tile.getCookTime() + 8));
					tile.update();
				}
				else {
					this.cancel();
				}
			}
		}.runTaskTimer(Main.get(), 1L, 10L);
	}

	@EventHandler
	public void onFurnaceBurn(FurnaceBurnEvent event) {
		this.startUpdate((Furnace) event.getBlock().getState());
	}
}
