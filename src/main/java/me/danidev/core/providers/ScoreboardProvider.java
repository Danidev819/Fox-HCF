package me.danidev.core.providers;

import me.danidev.core.Main;
import me.danidev.core.managers.balance.EconomyManager;
import me.danidev.core.managers.customtimer.CustomTimer;
import me.danidev.core.managers.eotw.EOTWHandler;
import me.danidev.core.managers.games.citadel.CitadelFaction;
import me.danidev.core.managers.timer.GlobalTimer;
import me.danidev.core.managers.timer.PlayerTimer;
import me.danidev.core.managers.timer.Timer;
import me.danidev.core.managers.timer.type.sotw.SOTWCommand;
import me.danidev.core.managers.timer.type.sotw.SOTWTimer;
import me.danidev.core.managers.user.FactionUser;
import me.danidev.core.managers.faction.type.PlayerFaction;
import me.danidev.core.managers.games.koth.EventTimer;
import me.danidev.core.managers.games.koth.faction.ConquestFaction;
import me.danidev.core.managers.games.koth.faction.EventFaction;
import me.danidev.core.managers.games.koth.faction.KothFaction;
import me.danidev.core.managers.games.koth.tracker.ConquestTracker;
import me.danidev.core.utils.*;
import me.danidev.core.utils.scoreboard.AssembleAdapter;
import dev.panda.ability.PandaAbilityAPI;
import me.activated.core.plugin.AquaCoreAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ScoreboardProvider implements AssembleAdapter {

	private String STRAIGHT_LINE;

	private static String handleBardFormat(long millis, boolean trailingZero) {
		return (trailingZero ? DateTimeFormats.REMAINING_SECONDS_TRAILING : DateTimeFormats.REMAINING_SECONDS).get().format(millis * 0.001);
	}
	public static DecimalFormat TPS_FORMAT = new DecimalFormat("0.0");
	public static final ThreadLocal<DecimalFormat> CONQUEST_FORMATTER = new ThreadLocal<DecimalFormat>() {
		@Override
		protected DecimalFormat initialValue() {
			return new DecimalFormat("00.0");
		}
	};

	@Override
	public String getTitle(Player player) {
		return CC.translate(Main.get().getScoreboardConfig().getString("TITLE").replace("|", "\u2503"));
	}

	@Override
	public List<String> getLines(Player player) {
		List<String> toReturn = new ArrayList<>();
		Collection<Timer> timers = Main.get().getTimerManager().getTimers();
		Collection<CustomTimer> customTimers = Main.get().getCustomTimerManager().getCustomTimers();
		EOTWHandler.EotwRunnable eotwRunnable = Main.get().getEotwHandler().getRunnable();
		EventTimer eventTimer = Main.get().getTimerManager().eventTimer;
		EventFaction eventFaction = eventTimer.getEventFaction();
		EconomyManager economyManager = Main.get().getEconomyManager();
		PlayerFaction playerFaction = Main.get().getFactionManager().getPlayerFaction(player.getUniqueId());
		FactionUser factionUser = Main.get().getUserManager().getUser(player.getUniqueId());

		if (eotwRunnable != null) {
			long remaining = eotwRunnable.getMillisUntilStarting();
			if (remaining > 0L) {
				toReturn.add(ChatColor.RED.toString() + ChatColor.BOLD + "EOTW" + ChatColor.RED + " starts in " + ChatColor.BOLD + DurationFormatter.getRemaining(remaining, true));
			} else if ((remaining = eotwRunnable.getMillisUntilCappable()) > 0L) {
				toReturn.add(ChatColor.RED.toString() + ChatColor.BOLD + "EOTW" + ChatColor.RED + " cappable in " + ChatColor.BOLD + DurationFormatter.getRemaining(remaining, true));
			}
		}

		SOTWTimer.SotwRunnable sotwRunnable = Main.get().getSotwTimer().getSotwRunnable();
		if (sotwRunnable != null) {
			if(!SOTWCommand.enabled.contains(player.getUniqueId())) {
				for (String s : Main.get().getScoreboardConfig().getStringList("SOTW")) {
					String formatted = s.replace("%cooldown%", Main.getRemaining(sotwRunnable.getRemaining(), true)
							.replaceAll("%d_arrow%", SymbolUtil.UNICODE_ARROWS_RIGHT));
					formatted = formatted.replace("%d_arrow%", SymbolUtil.UNICODE_ARROWS_RIGHT);

					toReturn.add(formatted);
				}
			} else {
				for (String s : Main.get().getScoreboardConfig().getStringList("SOTW-ENABLE")) {
					String formatted = s.replace("%cooldown%", Main.getRemaining(sotwRunnable.getRemaining(), true)
							.replaceAll("%d_arrow%", SymbolUtil.UNICODE_ARROWS_RIGHT));

					formatted = formatted.replace("%d_arrow%", SymbolUtil.UNICODE_ARROWS_RIGHT);

					toReturn.add(formatted);
				}
			}
		}

		if (Main.get().isKitMap() &&  Main.get().getFactionManager().getFactionAt(player.getLocation()).isSafezone()) {
				for (String s : Main.get().getScoreboardConfig().getStringList("KITMAP.STATISTICS")) {
					String formatted = s.replaceAll("%d_arrow%", SymbolUtil.UNICODE_ARROWS_RIGHT);
					formatted = formatted.replace("%KILLS%", String.valueOf(factionUser.getKills()));
					formatted = formatted.replace("%DEATHS%", String.valueOf(factionUser.getDeaths()));
					formatted = formatted.replace("%KEYS%", String.valueOf(factionUser.getKeys()));
					formatted = formatted.replace("%BALANCE", String.valueOf(economyManager.getBalance(player.getUniqueId())));
					toReturn.add(CC.translate(formatted));
			}
		}

		if (eventFaction instanceof ConquestFaction) {
			ConquestFaction conquestFaction = (ConquestFaction) eventFaction;
			toReturn.add(CC.translate("&6&lConquest Event"));
			toReturn.add(CC.translate(" &c"
					+ CONQUEST_FORMATTER.get().format(conquestFaction.getRed().getRemainingCaptureMillis() / 1000.0)
					+ "s &8\u23a2 &e"
					+ CONQUEST_FORMATTER.get().format(conquestFaction.getYellow().getRemainingCaptureMillis() / 1000.0)
					+ "s"));
			toReturn.add(CC.translate(" &a"
					+ CONQUEST_FORMATTER.get().format(conquestFaction.getGreen().getRemainingCaptureMillis() / 1000.0)
					+ "s &8\u23a2 &b"
					+ CONQUEST_FORMATTER.get().format(conquestFaction.getBlue().getRemainingCaptureMillis() / 1000.0)
					+ "s"));
			ConquestTracker conquestTracker1 = (ConquestTracker) conquestFaction.getEventType().getEventTracker();
			List<Map.Entry<PlayerFaction, Integer>> entries = new ArrayList<>(conquestTracker1.getFactionPointsMap().entrySet());

			int max = 4;

			if (entries.size() > max) {
				entries = entries.subList(0, max);
			}
			int i = 0;
			for (Map.Entry<PlayerFaction, Integer> entry : entries) {
				if (i < 4) {
					toReturn.add(CC.translate("&7" + (i + 1) + ") "
							+ entry.getKey().getDisplayName(player) + "&7: &f" + entry.getValue()));
					++i;
				}
			}
		}


		if (eventFaction instanceof CitadelFaction) {
			CitadelFaction citadelFaction = (CitadelFaction) eventFaction;
			long remaining = citadelFaction.getCaptureZone().getRemainingCaptureMillis();

			if (remaining > 0L) {
				toReturn.add(Main.get().getScoreboardConfig().getString("CITADEL").replace("%TIME%", DurationFormatter.getRemaining(remaining, true)));
			}
		}


		if (eventFaction instanceof KothFaction) {
			KothFaction kothFaction = (KothFaction) eventFaction;
			long remaining = kothFaction.getCaptureZone().getRemainingCaptureMillis();

			if (remaining > 0L) {
				toReturn.add(Main.get().getScoreboardConfig().getString("KOTH").replace("%KOTH%", eventFaction.getName()).replace("%TIME%", DurationFormatter.getRemaining(remaining, true)));
			}
		}

		for (CustomTimer customTimer : customTimers) {
			toReturn.add(Main.get().getScoreboardConfig().getString("CUSTOM_TIMER").replace("%TIMER%", customTimer.getScoreboard()).replace("%TIME%", DurationFormatter.getRemaining(customTimer.getRemaining(), true)));
		}

		if (Main.get().getPartnerItem().onCooldown(player)) {
			toReturn.add("&d&lPartner Item&7:&c " + DurationFormatter.getRemaining(Main.get().getPartnerItem().getRemainingMilis(player), true, true));
		}


		if (Main.get().isPandaAbility()) {
			PandaAbilityAPI abilityAPI = new PandaAbilityAPI();

			if (abilityAPI.getGlobalCooldown().hasGlobalCooldown(player)) {
				toReturn.add(Main.get().getScoreboardConfig().getString("PANDA_ABILITY.GLOBAL_COOLDOWN").replace("%NAME%", abilityAPI.getGlobalCooldown().getGlobalCooldownName()).replace("%COOLDOWN%", abilityAPI.getGlobalCooldown().getGlobalCooldown(player)));
			}

			abilityAPI.getActiveAbility(player).forEach(abilityHandler -> {
				String ability = abilityHandler.getName();
				String cooldown = abilityHandler.getCooldown(player);

				toReturn.add(Main.get().getScoreboardConfig().getString("PANDA_ABILITY.ABILITY_COOLDOWN").replace("%ABILITY%", ability).replace("%COOLDOWN%", cooldown));
			});
		}

		for (Timer timer : timers) {
			if (timer instanceof EventTimer) {
				EventTimer event = (EventTimer) timer;
				if (event.getEventFaction() instanceof ConquestFaction) continue;
				if (event.getEventFaction() instanceof KothFaction) continue;
			}

			if (timer instanceof PlayerTimer) {
				PlayerTimer playerTimer = (PlayerTimer) timer;

				long remaining = playerTimer.getRemaining(player);

				if (remaining <= 0L) continue;

				String timerDisplayName = playerTimer.getDisplayName();

				toReturn.add(Main.get().getScoreboardConfig().getString("TIMER").replace("%TIMER%", timerDisplayName).replace("%TIME%", DurationFormatter.getRemaining(remaining, true)));
			} else {
				if (!(timer instanceof GlobalTimer)) continue;

				GlobalTimer globalTimer = (GlobalTimer) timer;

				long remaining = globalTimer.getRemaining();

				if (remaining <= 0L) continue;

				String timerDisplayName = globalTimer.getDisplayName();

				toReturn.add(Main.get().getScoreboardConfig().getString("TIMER").replace("%TIMER%", timerDisplayName).replace("%TIME%", DurationFormatter.getRemaining(remaining, true)));
			}
		}

		if (playerFaction != null && playerFaction.getFocused() != null) {
			for (String s : Main.get().getScoreboardConfig().getStringList("FACTION_FOCUS")) {
				String formatted = s.replaceAll("%d_arrow%", SymbolUtil.UNICODE_ARROWS_RIGHT);

				String home = playerFaction.getFocused().getHome() == null ? "None" : playerFaction.getFocused().getHome().getBlockX() + ", " + playerFaction.getFocused().getHome().getBlockZ();

				String DTR = playerFaction.getFocused().getDtrColourString() + JavaUtils.format(playerFaction.getFocused().getDeathsUntilRaidable(false)) + playerFaction.getFocused().getRegenStatus().getSymbol();

				String online = String.valueOf(playerFaction.getFocused().getOnlineMembers().size());

				formatted = formatted.replace("%FACTION%", playerFaction.getFocused().getName());
				formatted = formatted.replace("%HOME%", home);
				formatted = formatted.replace("%DTR%", DTR);
				formatted = formatted.replace("%ONLINE%", online);
				toReturn.add(CC.translate(formatted));
			}
		}

		if (!toReturn.isEmpty()) {
			if (Main.get().getScoreboardConfig().getBoolean("SEPARATOR-LINES-ENABLED")) {
				toReturn.add(0, Main.get().getScoreboardConfig().getString("SEPARATOR-LINES"));
			}
			if (Main.get().getScoreboardConfig().getBoolean("FOOTER-ENABLED")) {
				toReturn.add(Main.get().getScoreboardConfig().getString("FOOTER"));
			}
			if (Main.get().getScoreboardConfig().getBoolean("SEPARATOR-LINES-ENABLED")) {
				toReturn.add(toReturn.size(), Main.get().getScoreboardConfig().getString("SEPARATOR-LINES"));
			}
		}
		return toReturn;
	}
}