package me.danidev.core.managers.classes;

import me.danidev.core.utils.CC;
import org.bukkit.entity.Player;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import org.bukkit.potion.PotionEffect;
import java.util.Set;

public abstract class PvPClass {

	public static final long DEFAULT_MAX_DURATION;
	protected final Set<PotionEffect> passiveEffects;
	protected final String name;
	protected final long warmupDelay;

	static {
		DEFAULT_MAX_DURATION = TimeUnit.MINUTES.toMillis(8L);
	}

	public PvPClass(final String name, final long warmupDelay) {
		this.passiveEffects = new HashSet<>();
		this.name = name;
		this.warmupDelay = warmupDelay;
	}

	public String getName() {
		return this.name;
	}

	public long getWarmupDelay() {
		return this.warmupDelay;
	}

	public boolean onEquip(final Player player) {
		for (final PotionEffect effect : this.passiveEffects) {
			player.addPotionEffect(effect, true);
		}
		player.sendMessage(CC.translate("&6Class: &6&l%CLASS% &7--> &aEnabled!").replace("%CLASS%", this.name));
		return true;
	}

	public void onUnequip(final Player player) {
		for (final PotionEffect effect : this.passiveEffects) {
			for (final PotionEffect active : player.getActivePotionEffects()) {
				if (active.getDuration() > PvPClass.DEFAULT_MAX_DURATION && active.getType().equals(effect.getType())) {
					if (active.getAmplifier() != effect.getAmplifier()) continue;

					player.removePotionEffect(effect.getType());
					break;
				}
			}
		}
		player.sendMessage(CC.translate("&6Class: &6&l%CLASS% &7--> &cDisabled!").replace("%CLASS%", this.name));
	}

	public abstract boolean isApplicableFor(final Player p0);
}
