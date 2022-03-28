package me.danidev.core.managers.timer.type;

import me.danidev.core.Main;
import me.danidev.core.managers.classes.others.ArcherClass;
import me.danidev.core.utils.service.ConfigurationService;
import me.danidev.core.managers.timer.PlayerTimer;
import me.danidev.core.managers.timer.event.TimerExpireEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Arrow;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.util.UUID;

import org.bukkit.ChatColor;
import java.util.concurrent.TimeUnit;

import org.bukkit.event.Listener;

public class ArcherTimer extends PlayerTimer implements Listener
{
	private Main plugin;
	private final Double ARCHER_DAMAGE;

	public String getScoreboardPrefix()
	{
		return ChatColor.translateAlternateColorCodes('&', Main.get().getScoreboardConfig().getString("ARCHER_TAG_COLOR"));
	}

	public ArcherTimer(Main plugin) {
		super(Main.get().getScoreboardConfig().getString("ARCHER_TAG_NAME"), TimeUnit.SECONDS.toMillis(ConfigurationService.ARCHER_TAG_TIMER));
		this.ARCHER_DAMAGE = Double.valueOf(0.15D);
		this.plugin = plugin;
	}

	@EventHandler
	public void onExpire(TimerExpireEvent e)
	{
		if (e.getUserUUID().isPresent() && e.getTimer().equals(this)) {
			final UUID userUUID = (UUID) e.getUserUUID().get();
			final Player player = Bukkit.getPlayer(userUUID);
			if (player == null) {
				return;
			}
			if (!player.isOnline()) {
				return;
			}
			Bukkit.getPlayer((UUID) ArcherClass.tagged.get(userUUID)).sendMessage(ChatColor.GOLD
					+ "Your archer mark on " + ChatColor.RED + player.getName() + ChatColor.GOLD + " has expired.");
			player.sendMessage(ChatColor.GOLD + "You are no longer archer marked.");
			ArcherClass.tagged.remove(player.getUniqueId());
		}
	}

	@EventHandler
	public void onHit(EntityDamageByEntityEvent e)
	{
		if (((e.getEntity() instanceof Player)) && ((e.getDamager() instanceof Player)))
		{
			Player entity = (Player)e.getEntity();
			Entity damager = (Player)e.getDamager();
			if (getRemaining(entity) > 0L)
			{
				Double damage = Double.valueOf(e.getDamage() * this.ARCHER_DAMAGE.doubleValue());
				e.setDamage(e.getDamage() + damage.doubleValue());
			}
		}
		if (((e.getEntity() instanceof Player)) && ((e.getDamager() instanceof Arrow)))
		{
			Player entity = (Player)e.getEntity();
			Entity damager = (Player)((Arrow)e.getDamager()).getShooter();
			if (((damager instanceof Player)) &&
					(getRemaining(entity) > 0L))
			{
				if (((UUID)ArcherClass.tagged.get(entity.getUniqueId())).equals(damager.getUniqueId())) {
					setCooldown(entity, entity.getUniqueId());
				}
				Double damage = Double.valueOf(e.getDamage() * this.ARCHER_DAMAGE.doubleValue());
				e.setDamage(e.getDamage() + damage.doubleValue());
			}
		}
	}
}