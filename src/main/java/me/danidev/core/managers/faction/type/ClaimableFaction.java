package me.danidev.core.managers.faction.type;

import me.danidev.core.Main;
import me.danidev.core.managers.faction.event.FactionClaimChangedEvent;
import me.danidev.core.managers.faction.event.cause.ClaimChangeCause;
import me.danidev.core.utils.BukkitUtils;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.others.GenericUtils;
import me.danidev.core.managers.faction.claim.Claim;
import me.danidev.core.managers.faction.event.FactionClaimChangeEvent;

import com.google.common.collect.Lists;

import org.bukkit.Bukkit;
import java.util.Collections;
import org.bukkit.Location;
import java.util.List;
import org.bukkit.ChatColor;

import org.bukkit.command.CommandSender;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.World;
import com.google.common.collect.ImmutableMap;

public class ClaimableFaction extends Faction {

    protected static ImmutableMap<World.Environment, String> ENVIRONMENT_MAPPINGS;
    protected Set<Claim> claims;
    
    static {
        ENVIRONMENT_MAPPINGS = ImmutableMap.of(World.Environment.NETHER, "Nether", World.Environment.NORMAL, "Overworld", World.Environment.THE_END, "The End");
    }
    
    public ClaimableFaction(String name) {
        super(name);
        this.claims = new HashSet<>();
    }
    
    public ClaimableFaction(Map<String, Object> map) {
        super(map);
        (this.claims = new HashSet<>()).addAll(GenericUtils.createList(map.get("claims"), Claim.class));
    }
    
	@Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.put("claims", new ArrayList<>(this.claims));
        return map;
    }
    
    @Override
    public void printDetails(CommandSender sender) {
        List<String> toSend = new ArrayList<>();

        for (String string : Main.get().getConfig().getStringList("FACTION_GENERAL.SHOW.SYSTEM_FACTION")) {
            string = string.replace("%LINE%", BukkitUtils.STRAIGHT_LINE_DEFAULT);
            string = string.replace("%FACTION%", this.getDisplayName(sender));

            if (string.contains("%WORLD%")) {
                for (Claim claim : this.claims) {
                    Location location = claim.getCenter();
                    string = string.replace("%WORLD%", ENVIRONMENT_MAPPINGS.get(location.getWorld().getEnvironment()));
                }
            }
            if (string.contains("%X%")) {
                for (Claim claim : this.claims) {
                    Location location = claim.getCenter();
                    string = string.replace("%X%", String.valueOf(location.getBlockX()));
                }
            }
            if (string.contains("%Z%")) {
                for (Claim claim : this.claims) {
                    Location location = claim.getCenter();
                    string = string.replace("%Z%", String.valueOf(location.getBlockZ()));
                }
            }
            toSend.add(string);
        }
        for (String message : toSend) {
            sender.sendMessage(CC.translate(message));
        }
    }
    
    public Set<Claim> getClaims() {
        return this.claims;
    }
    
    public boolean addClaim(Claim claim, CommandSender sender) {
        return this.addClaims(Collections.singleton(claim), sender);
    }
    
    public boolean addClaims(Collection<Claim> adding, CommandSender sender) {
        if (sender == null) {
            sender = Bukkit.getConsoleSender();
        }
        FactionClaimChangeEvent event = new FactionClaimChangeEvent(sender, ClaimChangeCause.CLAIM, adding, this);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled() || !this.claims.addAll(adding)) {
            return false;
        }
        Bukkit.getPluginManager().callEvent(new FactionClaimChangedEvent(sender, ClaimChangeCause.CLAIM, adding));
        return true;
    }
    
    public void removeClaim(Claim claim, CommandSender sender) {
        this.removeClaims(Collections.singleton(claim), sender);
    }
    
    public boolean removeClaims(Collection<Claim> removing, CommandSender sender) {
        if (sender == null) {
            sender = Bukkit.getConsoleSender();
        }

        int previousClaims = this.claims.size();
        FactionClaimChangeEvent event = new FactionClaimChangeEvent(sender, ClaimChangeCause.UNCLAIM, removing, this);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }
        for (Claim claim : Lists.newArrayList(removing)) {
            while (this.claims.contains(claim)) {
                this.claims.remove(claim);
            }
        }
        if (this instanceof PlayerFaction) {
            PlayerFaction playerFaction = (PlayerFaction)this;
            Location home = playerFaction.getHome();
            Main plugin = Main.get();
            int refund = 0;
            for (Claim claim2 : removing) {
                refund += plugin.getClaimHandler().calculatePrice(claim2, previousClaims, true);
                if (previousClaims > 0) {
                    --previousClaims;
                }
                if (home != null) {
                    if (!claim2.contains(home)) {
                        continue;
                    }
                    playerFaction.setHome(null);
                    playerFaction.broadcast(String.valueOf(ChatColor.RED.toString()) + ChatColor.BOLD + "Your factions' home was unset as its residing claim was removed.");
                    break;
                }
            }
            plugin.getEconomyManager().addBalance(playerFaction.getLeader().getUniqueId(), refund);
            playerFaction.broadcast(ChatColor.YELLOW + "Faction leader was refunded " + ChatColor.GREEN + '$' + refund + ChatColor.YELLOW + " due to a land unclaim.");
        }
        Bukkit.getPluginManager().callEvent(new FactionClaimChangedEvent(sender, ClaimChangeCause.UNCLAIM, removing));
        return true;
    }
}
