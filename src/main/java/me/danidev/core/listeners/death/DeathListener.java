package me.danidev.core.listeners.death;

import me.danidev.core.Main;
import me.danidev.core.managers.deathban.Deathban;
import me.danidev.core.managers.deathban.DeathbanManager;
import me.danidev.core.managers.user.FactionUser;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.service.ConfigurationService;
import me.danidev.core.managers.faction.struct.Role;
import me.danidev.core.managers.faction.type.Faction;
import me.danidev.core.managers.faction.type.PlayerFaction;

import me.danidev.core.utils.JavaUtils;
import org.bukkit.*;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import java.util.concurrent.TimeUnit;

import org.bukkit.inventory.ItemStack;
import java.util.UUID;
import java.util.HashMap;
import org.bukkit.event.Listener;

public class DeathListener implements Listener {

    private static final long REGEN_DELAY = TimeUnit.MINUTES.toMillis(60L);
    private Deathban deathban;
    private DeathbanManager deathbanManager;
    public static HashMap<UUID, ItemStack[]> contents = new HashMap<UUID, ItemStack[]>();
    public static HashMap<UUID, ItemStack[]> armor = new HashMap<UUID, ItemStack[]>();

    public DeathListener(Main plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerDeathKillIncrement(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();

        if (killer != null) {
            FactionUser user = Main.get().getUserManager().getUser(killer.getUniqueId());
            user.setKills(user.getKills() + 1);
            if (Main.get().isKitMap()) {
                user.setKeys(user.getKeys() + 1);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void onDeathKitMap(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();

        if (killer != null) {
            PlayerFaction killerFaction = Main.get().getFactionManager().getPlayerFaction(killer.getUniqueId());

            if (killerFaction != null) {
                killerFaction.setPoints(killerFaction.getPoints() + ConfigurationService.POINTS_PER_KILL);
                killerFaction.broadcast(CC.translate("&a" + killer.getName() + " &ehas gotten &6" + ConfigurationService.POINTS_PER_KILL + " &epoint for your faction."));
            }
        }
    }

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity().getPlayer();
        PlayerFaction playerFaction = Main.get().getFactionManager().getPlayerFaction(player.getUniqueId());
        FactionUser factionUser = Main.get().getUserManager().getUser(player.getUniqueId());

        if (playerFaction != null) {
            Faction factionAt = Main.get().getFactionManager().getFactionAt(player.getLocation());
            Role role = playerFaction.getMember(player.getUniqueId()).getRole();

            if (playerFaction.getDeathsUntilRaidable() >= -5.0) {
                if (player.getLocation().getWorld().getEnvironment() == World.Environment.NORMAL) {
                    playerFaction.setDeathsUntilRaidable(playerFaction.getDeathsUntilRaidable() - factionAt.getDtrLossWorldMultiplier());
                }
                if (player.getLocation().getWorld().getEnvironment() == World.Environment.NETHER) {
                    playerFaction.setDeathsUntilRaidable(playerFaction.getDeathsUntilRaidable() - factionAt.getDtrLossNetherMultiplier());
                }
                if (player.getLocation().getWorld().getEnvironment() == World.Environment.THE_END) {
                    playerFaction.setDeathsUntilRaidable(playerFaction.getDeathsUntilRaidable() - factionAt.getDtrLossEndMultiplier());
                }
                if(deathbanManager.isDeathbanned(player.getUniqueId())) {
                    playerFaction.setDeathsUntilRaidable(playerFaction.getDeathsUntilRaidable() - factionAt.getDtrLossDeathRoom());
                }
            }
            else {
                playerFaction.setRemainingRegenerationTime(DeathListener.REGEN_DELAY);
            }

            playerFaction.broadcast(ChatColor.RED + "Member Death: " + ChatColor.WHITE + role.getAstrix() + player.getName() + ChatColor.YELLOW + " DTR:" + ChatColor.GRAY + " [" + playerFaction.getDtrColour() + JavaUtils.format(playerFaction.getDeathsUntilRaidable()) + ChatColor.WHITE + '/' + ChatColor.WHITE + playerFaction.getMaximumDeathsUntilRaidable() + ChatColor.GRAY + "].");
            playerFaction.setPoints(playerFaction.getPoints() - ConfigurationService.POINTS_PER_DEATH);
            playerFaction.broadcast(CC.translate("&eYour faction has lost &6" + ConfigurationService.POINTS_PER_DEATH + " &epoint because &a" + player.getName() + " &edied!"));
        }

        if (playerFaction != null) {
            if (Main.get().isKitMap()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "faction setdtrregen " + player.getName() + " " + ConfigurationService.DEATH_TIME_KITMAP);
            }
            else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "faction setdtrregen " + player.getName() + " " + ConfigurationService.DEATH_TIME_HCF);
            }
        }

        if (!Main.get().isKitMap()) {
            DeathListener.armor.put(player.getUniqueId(), player.getInventory().getArmorContents());
            DeathListener.contents.put(player.getUniqueId(), player.getInventory().getContents());
        }

        factionUser.setDeaths(factionUser.getDeaths() + 1);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void addTotalKillsDeaths(PlayerDeathEvent event) {
        Player player = event.getEntity();
        PlayerFaction playerFaction = Main.get().getFactionManager().getPlayerFaction(player.getUniqueId());

        if (playerFaction != null) {
             playerFaction.setDeaths(playerFaction.getDeaths() + 1);
        }

        if (event.getEntity().getKiller() != null) {
            Player killer = event.getEntity().getKiller();
            PlayerFaction killerFaction = Main.get().getFactionManager().getPlayerFaction(killer.getUniqueId());

            if (killerFaction != null) {
                killerFaction.setKills(killerFaction.getKills() + 1);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        final Player p = e.getEntity();
        if (getPlayerContents().containsKey(p.getUniqueId())){
            getPlayerContents().remove(p.getUniqueId());
            getArmor().remove(p.getUniqueId());
        }
        getPlayerContents().put(p.getUniqueId(), p.getInventory().getContents());
        getArmor().put(p.getUniqueId(), p.getInventory().getArmorContents());
    }

    public static HashMap<UUID, ItemStack[]> getPlayerContents(){
        return contents;
    }

    public static HashMap<UUID, ItemStack[]> getArmor() {
        return armor;
    }
}
