package me.danidev.core.listeners.death;

import me.danidev.core.Main;
import com.google.common.base.Preconditions;
import net.minecraft.server.v1_7_R4.EntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.Listener;

public class DeathMessageListener implements Listener {

	public DeathMessageListener(Main plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	public static String replaceLast(final String text, final String regex, final String replacement) {
		return text.replaceFirst("(?s)" + regex + "(?!.*?" + regex + ')', replacement);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		final String message = event.getDeathMessage();
		if(message == null || message.isEmpty()) {
			return;
		}
		event.setDeathMessage(getDeathMessage(message,  event.getEntity(),  this.getKiller(event)));
	}

	private String getEntityName(final Entity entity) {
		Preconditions.checkNotNull((Object) entity,  "Entity cannot be null");
		return (entity instanceof Player) ? ((Player) entity).getName() : ((CraftEntity) entity).getHandle().getName();
	}

	private String getDeathMessage(String input, final Entity entity, final Entity killer) {
		input = input.replaceFirst("\\[", "");
		input = replaceLast(input, "]", "");
		if(entity != null) {
			input = input.replaceFirst("(?i)" + this.getEntityName(entity), ChatColor.RED + this.getDisplayName(entity) + ChatColor.YELLOW);
		}
		if(killer != null && (entity == null || !killer.equals(entity))) {
			input = input.replaceFirst("(?i)" + this.getEntityName(killer), ChatColor.RED + this.getDisplayName(killer) + ChatColor.YELLOW);
		}
		return input;
	}

	private String getDisplayName(final Entity entity) {
		Preconditions.checkNotNull((Object) entity,  "Entity cannot be null");
		if(entity instanceof Player) {
			final Player player = (Player) entity;
			return player.getName() + ChatColor.DARK_RED + '[' + ChatColor.DARK_RED + Main.get().getUserManager().getUser(player.getUniqueId()).getKills() + ChatColor.DARK_RED + ']';
		}
		return WordUtils.capitalizeFully(entity.getType().name().replace('_', ' '));
	}

	public String toReadable(ItemStack item) {
		if (item == null || item.getType() == Material.AIR) {
			return "";
		}
		if (item.hasItemMeta()) {
			ItemMeta meta = item.getItemMeta();
			if (meta.hasDisplayName()) {
				return ChatColor.AQUA + " using " + ChatColor.DARK_AQUA + meta.getDisplayName() + ChatColor.AQUA + ".";
			}
		}
		return ChatColor.AQUA + " using " + ChatColor.DARK_AQUA + this.toReadable(item.getType()) + ChatColor.AQUA+ ".";
	}

	public String toReadable(Enum<?> enu) {
		return WordUtils.capitalize(enu.name().replace("_", " ").toLowerCase());
	}

	private CraftEntity getKiller(PlayerDeathEvent event) {
		EntityLiving lastAttacker = ((CraftPlayer) event.getEntity()).getHandle();
		return (lastAttacker == null) ? null : lastAttacker.getBukkitEntity();
	}

	private String getDeathMessage(Player player, Entity killer, EntityDamageEvent.DamageCause cause, boolean isLogger) {
		String input = null;

		if (killer instanceof Player) {
			ItemStack item = ((Player) killer).getItemInHand();
			if (item != null) {
				if (item.getType() == Material.BOW) {
					input = ChatColor.DARK_AQUA + this.getName(player) + ChatColor.AQUA + " was shot by "
							+ ChatColor.DARK_AQUA + this.getName(killer);
					input = input + ChatColor.AQUA + " from " + ChatColor.LIGHT_PURPLE
							+ (int) player.getLocation().distance(killer.getLocation()) + ChatColor.BLUE + " blocks"
							+ ChatColor.AQUA + ".";
				}
				else {
					input = ChatColor.DARK_AQUA + this.getName(player) + ChatColor.AQUA + " was slain by "
							+ ChatColor.DARK_AQUA + this.getName(killer);
					input = input + this.toReadable(item);
				}
			}
		}
		else if (cause == EntityDamageEvent.DamageCause.FALL) {
			input = ChatColor.DARK_AQUA + this.getName(player) + ChatColor.AQUA + " fell from a high place.";
		} 
		else if (cause == EntityDamageEvent.DamageCause.FIRE) {
			input = ChatColor.DARK_AQUA + this.getName(player) + ChatColor.AQUA + " died to fire.";
		} 
		else if (cause == EntityDamageEvent.DamageCause.LIGHTNING) {
			input = ChatColor.DARK_AQUA + this.getName(player) + ChatColor.AQUA + " died to lightning.";
		} 
		else if (cause == EntityDamageEvent.DamageCause.WITHER) {
			input = ChatColor.DARK_AQUA + this.getName(player) + ChatColor.AQUA + " withered away.";
		} 
		else if (cause == EntityDamageEvent.DamageCause.DROWNING) {
			input = ChatColor.DARK_AQUA + this.getName(player) + ChatColor.AQUA + " drowned.";
		} 
		else if (cause == EntityDamageEvent.DamageCause.FALLING_BLOCK) {
			input = ChatColor.DARK_AQUA + this.getName(player) + ChatColor.AQUA + " died to a falling block.";
		} 
		else if (cause == EntityDamageEvent.DamageCause.MAGIC) {
			input = ChatColor.DARK_AQUA + this.getName(player) + ChatColor.AQUA + " died to magic.";
		} 
		else if (cause == EntityDamageEvent.DamageCause.VOID) {
			input = ChatColor.DARK_AQUA + this.getName(player) + ChatColor.AQUA + " fell into the void.";
		} 
		else if (cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
			input = ChatColor.DARK_AQUA + this.getName(player) + ChatColor.AQUA + " died to an explosion.";
		} 
		else if (cause == EntityDamageEvent.DamageCause.LAVA) {
			input = ChatColor.DARK_AQUA + this.getName(player) + ChatColor.AQUA + " burnt to a crisp.";
		} 
		else if (cause == EntityDamageEvent.DamageCause.STARVATION) {
			input = ChatColor.DARK_AQUA + this.getName(player) + ChatColor.AQUA + " starved to death.";
		} 
		else {
			input = ChatColor.DARK_AQUA + this.getName(player) + ChatColor.AQUA + " died.";
		}
		return input;
	}

	private String getName(Entity entity) {
		Preconditions.checkNotNull(entity, "Entity cannot be null");
		if (entity instanceof Player) {
			Player player = (Player) entity;
			return ChatColor.DARK_AQUA + player.getName() + ChatColor.GRAY + '[' + ChatColor.WHITE
					+ Main.get().getUserManager().getUser(player.getUniqueId()).getKills() + ChatColor.GRAY + ']';
		}
		return WordUtils.capitalizeFully(entity.getType().name().replace('_', ' '));
	}
}
