package me.danidev.core.managers.classes.others;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.Cooldowns;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.event.block.Action;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.danidev.core.managers.classes.PvPClass;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.entity.Arrow;
import org.bukkit.event.entity.EntityShootBowEvent;
import java.util.concurrent.TimeUnit;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffect;
import java.util.UUID;
import java.util.HashMap;
import org.bukkit.event.Listener;

public class ArcherClass extends PvPClass implements Listener {
	
	private final Main plugin;
	
	public static final HashMap<UUID, UUID> tagged;
	public static final HashMap<UUID, Long> ARCHER_COOLDOWN;
	public static final PotionEffect ARCHER_CRITICAL_EFFECT;
	public static final PotionEffect ARCHER_SPEED_EFFECT;
	public static final long ARCHER_SPEED_COOLDOWN_DELAY;
	public static final int MARK_TIMEOUT_SECONDS = 10;
	public static final int MARK_EXECUTION_LEVEL = 3;
	public static final float MINIMUM_FORCE = 0.5f;
	public static final String ARROW_FORCE_METADATA = "ARROW_FORCE";
	public static PotionEffect ARCHER_JUMP_EFFECT;
	public static long ARCHER_JUMP_COOLDOWN_DELAY;
	
	static {
		tagged = new HashMap<>();
		ARCHER_COOLDOWN = new HashMap<>();
		ARCHER_CRITICAL_EFFECT = new PotionEffect(PotionEffectType.POISON, 60, 0);
		ARCHER_SPEED_EFFECT = new PotionEffect(PotionEffectType.SPEED, 160, 3);
		ARCHER_SPEED_COOLDOWN_DELAY = TimeUnit.MINUTES.toMillis(1L);
		ARCHER_JUMP_EFFECT = new PotionEffect(PotionEffectType.JUMP, 160, 3);
		ARCHER_JUMP_COOLDOWN_DELAY = TimeUnit.MINUTES.toMillis(1L);
	}

	public ArcherClass(final Main plugin) {
		super("Archer", TimeUnit.SECONDS.toMillis(5L));
		this.plugin = plugin;
		this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
		this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onEntityShootBow(final EntityShootBowEvent event) {
		final Entity projectile = event.getProjectile();
		if (projectile instanceof Arrow) {
			projectile.setMetadata("ARROW_FORCE",
					(MetadataValue) new FixedMetadataValue((Plugin) this.plugin, (Object) event.getForce()));
		}
	}

	@EventHandler
	public void onPlayerClickSugar(final PlayerInteractEvent e) {
		final Player p = e.getPlayer();
		if (this.plugin.getPvpClassManager().getEquippedClass(p) != null
				&& this.plugin.getPvpClassManager().getEquippedClass(p).equals(this)
				&& p.getItemInHand().getType() == Material.SUGAR) {
			if (Cooldowns.isOnCooldown("ARCHER_ITEM_COOLDOWN", p)) {
				p.sendMessage(ChatColor.RED + "You cannot use this for another "
						+ Cooldowns.getCooldownForPlayerInt("ARCHER_ITEM_COOLDOWN", p) + ChatColor.RED.toString()
						+ " seconds.");
				e.setCancelled(true);
				return;
			}
			Cooldowns.addCooldown("ARCHER_ITEM_COOLDOWN", p, 25);
			p.sendMessage(String.valueOf(ChatColor.RED.toString()) + "&cSpeed 4 now activated.");
			if (p.getItemInHand().getAmount() == 1) {
				p.getInventory().remove(p.getItemInHand());
			} else {
				p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
			}
			p.removePotionEffect(PotionEffectType.SPEED);
			p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 120, 3));
			new BukkitRunnable() {
				public void run() {
					if (ArcherClass.this.isApplicableFor(p)) {
						p.removePotionEffect(PotionEffectType.SPEED);
						p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
					}
				}
			}.runTaskLater((Plugin) this.plugin, 120L);
		}
	}

	@EventHandler
	public void onQuit(final PlayerQuitEvent e) {
		if (ArcherClass.tagged.containsKey(e.getPlayer().getUniqueId())) {
			ArcherClass.tagged.remove(e.getPlayer().getUniqueId());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onEntityDamage(final EntityDamageByEntityEvent event) {
		final Entity entity = event.getEntity();
		final Entity damager = event.getDamager();
		if (entity instanceof Player && damager instanceof Arrow) {
			final Arrow arrow = (Arrow) damager;
			final ProjectileSource source = arrow.getShooter();
			if (source instanceof Player) {
				final Player damaged = (Player) event.getEntity();
				final Player shooter = (Player) source;
				final PvPClass equipped = this.plugin.getPvpClassManager().getEquippedClass(shooter);
				if (equipped == null || !equipped.equals(this)) {
					return;
				}
				if (this.plugin.getTimerManager().archerTimer.getRemaining((Player) entity) == 0L) {
					if (this.plugin.getPvpClassManager().getEquippedClass(damaged) != null
							&& this.plugin.getPvpClassManager().getEquippedClass(damaged).equals(this)) {
						return;
					}
					this.plugin.getTimerManager().archerTimer.setCooldown((Player) entity, entity.getUniqueId());
					ArcherClass.tagged.put(damaged.getUniqueId(), shooter.getUniqueId());

					long distance = Math.round(shooter.getLocation().distance(damaged.getLocation()));

					shooter.sendMessage(CC.translate(Main.get().getLangConfig().getString("ARCHER_TAG")
							.replace("%DISTANCE%", String.valueOf(distance))));
					damaged.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5, 1));
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
	public void onArcherJumpClick(final PlayerInteractEvent event) {
		final Player p = event.getPlayer();
		final Action action = event.getAction();
		if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) && event.hasItem()
				&& event.getItem().getType() == Material.FEATHER) {
			if (this.plugin.getPvpClassManager().getEquippedClass(event.getPlayer()) != this) {
				return;
			}
			if (Cooldowns.isOnCooldown("ARCHER_JUMP_COOLDOWN", p)) {
				p.sendMessage(ChatColor.RED + "You cannot use this for another "
						+ Cooldowns.getCooldownForPlayerInt("ARCHER_JUMP_COOLDOWN", p) + ChatColor.RED.toString()
						+ " seconds.");
				event.setCancelled(true);
				return;
			}
			Cooldowns.addCooldown("ARCHER_JUMP_COOLDOWN", p, 25);
			p.sendMessage(String.valueOf(ChatColor.RED.toString()) + "&cArcher Jump boost enabled.");
			if (p.getItemInHand().getAmount() == 1) {
				p.getInventory().remove(p.getItemInHand());
			} else {
				p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
			}
			p.removePotionEffect(PotionEffectType.JUMP);
			p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 120, 6));
			new BukkitRunnable() {
				public void run() {
					if (ArcherClass.this.isApplicableFor(p)) {
						p.removePotionEffect(PotionEffectType.JUMP);
					}
				}
			}.runTaskLater((Plugin) this.plugin, 120L);
		}
	}

	@Override
	public boolean isApplicableFor(final Player player) {
		final PlayerInventory playerInventory = player.getInventory();
		final ItemStack helmet = playerInventory.getHelmet();
		if (helmet == null || helmet.getType() != Material.LEATHER_HELMET) {
			return false;
		}
		final ItemStack chestplate = playerInventory.getChestplate();
		if (chestplate == null || chestplate.getType() != Material.LEATHER_CHESTPLATE) {
			return false;
		}
		final ItemStack leggings = playerInventory.getLeggings();
		if (leggings == null || leggings.getType() != Material.LEATHER_LEGGINGS) {
			return false;
		}
		final ItemStack boots = playerInventory.getBoots();
		return boots != null && boots.getType() == Material.LEATHER_BOOTS;
	}
}
