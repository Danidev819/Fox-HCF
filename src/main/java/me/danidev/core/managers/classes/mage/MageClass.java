package me.danidev.core.managers.classes.mage;

import me.danidev.core.Main;
import me.danidev.core.managers.classes.PvPClass;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.faction.type.PlayerFaction;
import me.danidev.core.utils.DurationFormatter;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class MageClass extends PvPClass implements Listener {

	private static final long BUFF_COOLDOWN_MILLIS = TimeUnit.SECONDS.toMillis(8L);
	private final Map<UUID, MageData> mageDataMap;
	private final Map<Material, MageEffect> mageEffects;
	private final MageRestorer mageRestorer;
	private final TObjectLongMap<UUID> msgCooldowns;

	public MageClass(Main plugin) {
		super("Mage", TimeUnit.SECONDS.toMillis(5L));
		this.mageDataMap = new HashMap<>();
		this.mageEffects = new EnumMap<>(Material.class);
		this.msgCooldowns = new TObjectLongHashMap<>();
		this.mageRestorer = new MageRestorer(plugin);

		this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
		this.passiveEffects.add(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
		this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));

		this.mageEffects.put(Material.INK_SACK, new MageEffect(45, new PotionEffect(PotionEffectType.POISON, 120, 0)));
		this.mageEffects.put(Material.SPIDER_EYE, new MageEffect(35, new PotionEffect(PotionEffectType.WITHER, 120, 1)));
		this.mageEffects.put(Material.GOLD_NUGGET, new MageEffect(35, new PotionEffect(PotionEffectType.SLOW, 120, 1)));
		this.mageEffects.put(Material.COAL, new MageEffect(30, new PotionEffect(PotionEffectType.WEAKNESS, 120, 1)));
		this.mageEffects.put(Material.SEEDS, new MageEffect(40, new PotionEffect(PotionEffectType.CONFUSION, 120, 1)));
		this.mageEffects.put(Material.ROTTEN_FLESH, new MageEffect(25, new PotionEffect(PotionEffectType.HUNGER, 120, 1)));
	}

	public boolean onEquip(Player player) {
		if (!super.onEquip(player)) return false;

		MageData mageData = new MageData();

		this.mageDataMap.put(player.getUniqueId(), mageData);

		mageData.startEnergyTracking();
		return true;
	}

	public void onUnequip(Player player) {
		super.onUnequip(player);
		clearMageData(player.getUniqueId());
	}

	private void clearMageData(UUID uuid) {
		this.mageDataMap.remove(uuid);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		clearMageData(event.getPlayer().getUniqueId());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerKick(PlayerKickEvent event) {
		clearMageData(event.getPlayer().getUniqueId());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onItemHeld(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		PvPClass equipped = Main.get().getPvpClassManager().getEquippedClass(player);

		if ((equipped == null) || (!equipped.equals(this))) return;

		UUID uuid = player.getUniqueId();
		long lastMessage = this.msgCooldowns.get(uuid);
		long millis = System.currentTimeMillis();

		if ((lastMessage != this.msgCooldowns.getNoEntryValue()) && (lastMessage - millis > 0L)) return;

		player.getInventory().getItem(event.getNewSlot());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!event.hasItem()) return;

		Action action = event.getAction();

		if ((action == Action.RIGHT_CLICK_AIR) || ((!event.isCancelled()) && (action == Action.RIGHT_CLICK_BLOCK))) {
			ItemStack stack = event.getItem();
			MageEffect mageEffect = this.mageEffects.get(stack.getType());

			if ((mageEffect == null) || (mageEffect.clickable == null)) return;

			event.setUseItemInHand(Result.DENY);

			Player player = event.getPlayer();
			MageData mageData = this.mageDataMap.get(player.getUniqueId());

			if (mageData != null) {
				if (!canUseMageEffect(player, mageData, mageEffect, true)) return;

				if (stack.getAmount() > 1) {
					stack.setAmount(stack.getAmount() - 1);
				}
				else {
					player.setItemInHand(new ItemStack(Material.AIR, 1));
				}

				if (!Main.get().getFactionManager().getFactionAt(player.getLocation()).isSafezone()) {
					PlayerFaction playerFaction = Main.get().getFactionManager().getPlayerFaction(player.getUniqueId());

					if (!mageEffect.clickable.getType().equals(PotionEffectType.POISON)) {
						Collection<Entity> nearbyEntities = player.getNearbyEntities(25.0D, 25.0D, 25.0D);

						for (Entity nearby : nearbyEntities) {

							if (((nearby instanceof Player)) && (!player.equals(nearby))) {
								Player target = (Player) nearby;

								if (playerFaction == null) {
									this.mageRestorer.setRestoreEffect(target, mageEffect.clickable);
								}
								else if (!playerFaction.getMembers().containsKey(target.getUniqueId())) {
									this.mageRestorer.setRestoreEffect(target, mageEffect.clickable);
								}
							}
						}
					}
					else if (!mageEffect.clickable.getType().equals(PotionEffectType.WITHER)) {
						Collection<Entity> nearbyEntities = player.getNearbyEntities(25.0D, 25.0D, 25.0D);

						for (Entity nearby : nearbyEntities) {

							if (((nearby instanceof Player)) && (!player.equals(nearby))) {
								Player target = (Player) nearby;

								if (playerFaction == null) {
									this.mageRestorer.setRestoreEffect(target, mageEffect.clickable);
								}
								else if (!playerFaction.getMembers().containsKey(target.getUniqueId())) {
									this.mageRestorer.setRestoreEffect(target, mageEffect.clickable);
								}
							}
						}
					}
					else if (!mageEffect.clickable.getType().equals(PotionEffectType.SLOW)) {
						Collection<Entity> nearbyEntities = player.getNearbyEntities(25.0D, 25.0D, 25.0D);

						for (Entity nearby : nearbyEntities) {

							if (((nearby instanceof Player)) && (!player.equals(nearby))) {
								Player target = (Player) nearby;

								if (playerFaction == null) {
									this.mageRestorer.setRestoreEffect(target, mageEffect.clickable);
								}
								else if (!playerFaction.getMembers().containsKey(target.getUniqueId())) {
									this.mageRestorer.setRestoreEffect(target, mageEffect.clickable);
								}
							}
						}
					}
					else if (!mageEffect.clickable.getType().equals(PotionEffectType.WEAKNESS)) {
						Collection<Entity> nearbyEntities = player.getNearbyEntities(25.0D, 25.0D, 25.0D);

						for (Entity nearby : nearbyEntities) {

							if (((nearby instanceof Player)) && (!player.equals(nearby))) {
								Player target = (Player) nearby;

								if (playerFaction == null) {
									this.mageRestorer.setRestoreEffect(target, mageEffect.clickable);
								}
								else if (!playerFaction.getMembers().containsKey(target.getUniqueId())) {
									this.mageRestorer.setRestoreEffect(target, mageEffect.clickable);
								}
							}
						}
					}
					else if (!mageEffect.clickable.getType().equals(PotionEffectType.CONFUSION)) {
						Collection<Entity> nearbyEntities = player.getNearbyEntities(25.0D, 25.0D, 25.0D);

						for (Entity nearby : nearbyEntities) {

							if (((nearby instanceof Player)) && (!player.equals(nearby))) {
								Player target = (Player) nearby;

								if (playerFaction == null) {
									this.mageRestorer.setRestoreEffect(target, mageEffect.clickable);
								}
								else if (!playerFaction.getMembers().containsKey(target.getUniqueId())) {
									this.mageRestorer.setRestoreEffect(target, mageEffect.clickable);
								}
							}
						}
					}
					else if (!mageEffect.clickable.getType().equals(PotionEffectType.HUNGER)) {
						Collection<Entity> nearbyEntities = player.getNearbyEntities(25.0D, 25.0D, 25.0D);

						for (Entity nearby : nearbyEntities) {

							if (((nearby instanceof Player)) && (!player.equals(nearby))) {
								Player target = (Player) nearby;

								if (playerFaction == null) {
									this.mageRestorer.setRestoreEffect(target, mageEffect.clickable);
								}
								else if (!playerFaction.getMembers().containsKey(target.getUniqueId())) {
									this.mageRestorer.setRestoreEffect(target, mageEffect.clickable);
								}
							}
						}
					}
				}
				this.mageRestorer.setRestoreEffect(player, mageEffect.clickable);
				setEnergy(player, mageData.getEnergy() - mageEffect.energyCost);
				mageData.buffCooldown = (System.currentTimeMillis() + BUFF_COOLDOWN_MILLIS);
			}
		}
	}

	private boolean canUseMageEffect(Player player, MageData mageData, MageEffect mageEffect, boolean sendFeedback) {
		String errorFeedback = null;
		double currentEnergy = mageData.getEnergy();

		if (mageEffect.energyCost > currentEnergy) {
			errorFeedback = ChatColor.RED + "You need at least " + ChatColor.BOLD + mageEffect.energyCost
					+ ChatColor.RED + " energy to use this Mage buff, whilst you only have " + ChatColor.BOLD
					+ currentEnergy + ChatColor.RED + '.';
		}

		long remaining = mageData.getRemainingBuffDelay();

		if (remaining > 0L) {
			errorFeedback = ChatColor.RED + "You still have a cooldown on this " + ChatColor.GREEN + ChatColor.BOLD
					+ "Mage" + ChatColor.RED + " buff for another " + DurationFormatter.getRemaining(remaining, true, false)
					+ ChatColor.RED + '.';
		}
		Faction factionAt = Main.get().getFactionManager().getFactionAt(player.getLocation());

		if (factionAt.isSafezone()) {
			errorFeedback = ChatColor.RED + "Mage Buffs are disabled in safe-zones.";
		}

		if ((sendFeedback) && (errorFeedback != null)) {
			player.sendMessage(errorFeedback);
		}

		return errorFeedback == null;
	}

	public boolean isApplicableFor(Player player) {
		ItemStack helmet = player.getInventory().getHelmet();
		if ((helmet == null) || (helmet.getType() != Material.GOLD_HELMET)) {
			return false;
		}
		ItemStack chestplate = player.getInventory().getChestplate();
		if ((chestplate == null) || (chestplate.getType() != Material.CHAINMAIL_CHESTPLATE)) {
			return false;
		}
		ItemStack leggings = player.getInventory().getLeggings();
		if ((leggings == null) || (leggings.getType() != Material.CHAINMAIL_LEGGINGS)) {
			return false;
		}
		ItemStack boots = player.getInventory().getBoots();
		return (boots != null) && (boots.getType() == Material.GOLD_BOOTS);
	}

	public long getRemainingBuffDelay(Player player) {
		synchronized (this.mageDataMap) {
			MageData mageData = this.mageDataMap.get(player.getUniqueId());
			return mageData == null ? 0L : mageData.getRemainingBuffDelay();
		}
	}

	public double getEnergy(Player player) {
		synchronized (this.mageDataMap) {
			MageData mageData = this.mageDataMap.get(player.getUniqueId());
			return mageData == null ? 0.0D : mageData.getEnergy();
		}
	}

	public long getEnergyMillis(Player player) {
		synchronized (this.mageDataMap) {
			MageData mageData = this.mageDataMap.get(player.getUniqueId());
			return mageData == null ? 0L : mageData.getEnergyMillis();
		}
	}

	public void setEnergy(Player player, double energy) {
		MageData mageData = this.mageDataMap.get(player.getUniqueId());
		if (mageData == null) return;

		mageData.setEnergy(energy);
	}
}
