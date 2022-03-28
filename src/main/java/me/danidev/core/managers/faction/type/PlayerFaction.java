package me.danidev.core.managers.faction.type;

import java.util.Collections;

import me.danidev.core.Main;
import me.danidev.core.managers.deathban.Deathban;
import me.danidev.core.managers.faction.event.FactionDtrChangeEvent;
import me.danidev.core.managers.faction.event.PlayerLeaveFactionEvent;
import me.danidev.core.managers.faction.event.PlayerLeftFactionEvent;
import me.danidev.core.managers.faction.event.cause.FactionLeaveCause;
import me.danidev.core.managers.faction.struct.Raidable;
import me.danidev.core.managers.faction.struct.RegenStatus;
import me.danidev.core.managers.faction.struct.Relation;
import me.danidev.core.managers.faction.struct.Role;
import me.danidev.core.managers.timer.type.TeleportTimer;
import me.danidev.core.managers.user.FactionUser;
import me.danidev.core.utils.BukkitUtils;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.service.ConfigurationService;
import me.danidev.core.utils.JavaUtils;
import me.danidev.core.utils.others.GenericUtils;
import me.danidev.core.utils.others.PersistableLocation;
import com.google.common.base.Preconditions;

import com.google.common.collect.Sets;
import me.danidev.core.managers.faction.FactionMember;
import me.danidev.core.managers.faction.event.PlayerJoinedFactionEvent;
import org.apache.commons.lang.time.DurationFormatUtils;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Objects;
import org.bukkit.Location;

import java.util.HashSet;
import org.bukkit.command.CommandSender;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import com.google.common.collect.Maps;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;

import java.util.Iterator;
import java.util.Collection;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import java.util.UUID;

public class PlayerFaction extends ClaimableFaction implements Raidable {

    private static final UUID[] EMPTY_UUID_ARRAY;
    protected Map<UUID, Relation> requestedRelations;
    protected Map<UUID, Relation> relations;
    protected Map<UUID, FactionMember> members;
    protected Set<String> invitedPlayerNames;
    protected PersistableLocation home;
    protected String announcement;
    protected boolean open;
    protected int balance;
    private int kothCaptures;
    protected double deathsUntilRaidable;
    protected long regenCooldownTimestamp;
    private long lastDtrUpdateTimestamp;
    private PlayerFaction focused;
    private int points;
    private int kills;
    private int deaths;
    public Boolean rally;
    private Location rallyloc;
    private Player rallyPlayer;
    private BukkitTask rallyTask;
    private boolean friendlyFire;
    
    static {
        EMPTY_UUID_ARRAY = new UUID[0];
    }
    
    public PlayerFaction(String name) {
        super(name);
        this.requestedRelations = new HashMap<>();
        this.relations = new HashMap<>();
        this.members = new HashMap<>();
        this.invitedPlayerNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        this.deathsUntilRaidable = 1.0;
        this.rally = false;
        this.rallyloc = null;
        this.rallyPlayer = null;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public PlayerFaction(Map map) {
        super(map);
        this.requestedRelations = new HashMap<>();
        this.relations = new HashMap<>();
        this.members = new HashMap<>();
        this.invitedPlayerNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        this.deathsUntilRaidable = 1.0;

        for (Map.Entry entry : GenericUtils.castMap(map.get("members"), String.class, FactionMember.class).entrySet()) {
            this.members.put(UUID.fromString((String) entry.getKey()), (FactionMember) entry.getValue());
        }

        this.invitedPlayerNames.addAll(GenericUtils.createList(map.get("invitedPlayerNames"), String.class));

        Object object2 = map.get("home");

        if (object2 != null) {
            this.home = (PersistableLocation)object2;
        }
        object2 = map.get("announcement");
        if (object2 != null) {
            this.announcement = (String)object2;
        }
        for (Map.Entry entry2 : GenericUtils.castMap(map.get("relations"), String.class, String.class).entrySet()) {
            this.relations.put(UUID.fromString((String) entry2.getKey()), Relation.valueOf((String) entry2.getValue()));
        }
        for (Map.Entry entry2 : GenericUtils.castMap(map.get("requestedRelations"), String.class, String.class).entrySet()) {
            this.requestedRelations.put(UUID.fromString((String) entry2.getKey()), Relation.valueOf((String) entry2.getValue()));
        }
        this.open = (boolean)map.get("open");
        this.balance = (int)map.get("balance");
        this.deathsUntilRaidable = (double)map.get("deathsUntilRaidable");
        this.kills = (int)map.get("kills");
        this.deaths = (int)map.get("deaths");
        this.kothCaptures = (int)map.get("kothCaptures");
        this.points = (int)map.get("points");
        this.regenCooldownTimestamp = Long.parseLong((String) map.get("regenCooldownTimestamp"));
        this.lastDtrUpdateTimestamp = Long.parseLong((String) map.get("lastDtrUpdateTimestamp"));
        this.rally = (Boolean)map.getOrDefault("rally", false);
        this.rally = false;
        this.rallyloc = null;
        this.rallyPlayer = null;
    }
    
    public int getKills() {
        return this.kills;
    }
    
    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return this.deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }
    
    public static String format(String format) {
        return ChatColor.translateAlternateColorCodes('&', format);
    }
    
    public PlayerFaction getFocused() {
        return this.focused;
    }
    
    public void setFocused(PlayerFaction focused) {
        this.focused = focused;
    }
    
    public boolean isFriendlyFire() {
    	return this.friendlyFire;
    }
    
    public void setFriendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
    }
    

	@Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        Map<String, String> relationSaveMap = new HashMap<>(this.relations.size());
        for (Map.Entry<UUID, Relation> entry : this.relations.entrySet()) {
            relationSaveMap.put(entry.getKey().toString(), entry.getValue().name());
        }
        map.put("relations", relationSaveMap);
        Map<String, String> requestedRelationsSaveMap = new HashMap<>(this.requestedRelations.size());
        for (Map.Entry<UUID, Relation> entry2 : this.requestedRelations.entrySet()) {
            requestedRelationsSaveMap.put(entry2.getKey().toString(), entry2.getValue().name());
        }
        map.put("requestedRelations", requestedRelationsSaveMap);
        Set<Map.Entry<UUID, FactionMember>> entrySet = this.members.entrySet();
        Map<String, FactionMember> saveMap = new LinkedHashMap<>(this.members.size());
        for (Map.Entry<UUID, FactionMember> entry3 : entrySet) {
            saveMap.put(entry3.getKey().toString(), entry3.getValue());
        }
        map.put("members", saveMap);
        map.put("invitedPlayerNames", new ArrayList(this.invitedPlayerNames));

        if (this.home != null) {
            map.put("home", this.home);
        }
        if (this.announcement != null) {
            map.put("announcement", this.announcement);
        }
        map.put("open", this.open);
        map.put("balance", this.balance);
        map.put("deathsUntilRaidable", this.deathsUntilRaidable);
        map.put("kills", this.kills);
        map.put("deaths", this.deaths);
        map.put("kothCaptures", this.kothCaptures);
        map.put("points", this.points);
        map.put("regenCooldownTimestamp", Long.toString(this.regenCooldownTimestamp));
        map.put("lastDtrUpdateTimestamp", Long.toString(this.lastDtrUpdateTimestamp));
        map.put("rally", this.rally);
        return map;
    }
    
    public boolean setMember(UUID playerUUID, FactionMember factionMember) {
        return this.setMember(null, playerUUID, factionMember, false);
    }
    
    public boolean setMember(UUID playerUUID, FactionMember factionMember, boolean force) {
        return this.setMember(null, playerUUID, factionMember, force);
    }
    
    public boolean setMember(Player player, FactionMember factionMember) {
        return this.setMember(player, player.getUniqueId(), factionMember, false);
    }
    
    public boolean setMember(Player player, FactionMember factionMember, boolean force) {
        return this.setMember(player, player.getUniqueId(), factionMember, force);
    }
    
    private boolean setMember(Player player, UUID playerUUID, FactionMember factionMember, boolean force) {
        if (factionMember == null) {
            if (!force) {
                PlayerLeaveFactionEvent event = (player == null) ? new PlayerLeaveFactionEvent(playerUUID, this, FactionLeaveCause.LEAVE) : new PlayerLeaveFactionEvent(player, this, FactionLeaveCause.LEAVE);
                Bukkit.getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    return false;
                }
            }
            this.members.remove(playerUUID);
            this.setDeathsUntilRaidable(Math.min(this.deathsUntilRaidable, this.getMaximumDeathsUntilRaidable()));
            PlayerLeftFactionEvent event2 = (player == null) ? new PlayerLeftFactionEvent(playerUUID, this, FactionLeaveCause.LEAVE) : new PlayerLeftFactionEvent(player, this, FactionLeaveCause.LEAVE);
            Bukkit.getPluginManager().callEvent(event2);
            return true;
        }
        PlayerJoinedFactionEvent eventPre = (player == null) ? new PlayerJoinedFactionEvent(playerUUID, this) : new PlayerJoinedFactionEvent(player, this);
        Bukkit.getPluginManager().callEvent(eventPre);
        this.lastDtrUpdateTimestamp = System.currentTimeMillis();
        this.invitedPlayerNames.remove(factionMember.getName());
        this.members.put(playerUUID, factionMember);
        return true;
    }
    
	public Collection<UUID> getAllied() {
        return Maps.filterValues(this.relations, relation -> relation == Relation.ALLY).keySet();
    }
    
    public List<PlayerFaction> getAlliedFactions() {
        Collection<UUID> allied = this.getAllied();
        Iterator<UUID> iterator = allied.iterator();
        List<PlayerFaction> results = new ArrayList<>(allied.size());
        while (iterator.hasNext()) {
            Faction faction = Main.get().getFactionManager().getFaction(iterator.next());
            if (faction instanceof PlayerFaction) {
                results.add((PlayerFaction)faction);
            }
            else {
                iterator.remove();
            }
        }
        return results;
    }
    
    public Map<UUID, Relation> getRequestedRelations() {
        return this.requestedRelations;
    }
    
    public Map<UUID, Relation> getRelations() {
        return this.relations;
    }
    
	public Map<UUID, FactionMember> getMembers() {
        return ImmutableMap.copyOf(this.members);
    }
    
    public Set<Player> getOnlinePlayers() {
        return this.getOnlinePlayers(null);
    }
    
    public Set<Player> getOnlinePlayers(CommandSender sender) {
        Set<Map.Entry<UUID, FactionMember>> entrySet = this.getOnlineMembers(sender).entrySet();
        Set<Player> results = new HashSet<>(entrySet.size());
        for (Map.Entry<UUID, FactionMember> entry : entrySet) {
            results.add(Bukkit.getPlayer(entry.getKey()));
        }
        return results;
    }
    
    public Map<?, ?> getOnlineMembers() {
        return this.getOnlineMembers(null);
    }
    
    public Map<UUID, FactionMember> getOnlineMembers(CommandSender sender) {
        Player senderPlayer = (sender instanceof Player) ? ((Player) sender) : null;
        HashMap<UUID, FactionMember> results = new HashMap<>();
        for (Map.Entry<UUID, FactionMember> entry : this.members.entrySet()) {
            Player target = Bukkit.getPlayer(entry.getKey());
            if (target != null && (senderPlayer == null || senderPlayer.canSee(target))) {
                results.put(entry.getKey(), entry.getValue());
            }
        }
        return results;
    }
    
    public FactionMember getLeader() {
        Map<UUID, FactionMember> members = this.members;
        for (Map.Entry<UUID, FactionMember> uuidFactionMemberEntry : members.entrySet()) {
            Map.Entry<UUID, FactionMember> entry;
            if ((entry = uuidFactionMemberEntry).getValue().getRole() == Role.LEADER) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    @Deprecated
    public FactionMember getMember(String memberName) {
        UUID uuid = Bukkit.getOfflinePlayer(memberName).getUniqueId();
        if (uuid == null) {
            return null;
        }
        return this.members.get(uuid);
    }
    
    public FactionMember getMember(Player player) {
        return this.getMember(player.getUniqueId());
    }
    
    public FactionMember getMember(UUID memberUUID) {
        return this.members.get(memberUUID);
    }
    
    public Set<String> getInvitedPlayerNames() {
        return this.invitedPlayerNames;
    }
    
    public Location getHome() {
        return (this.home == null) ? null : this.home.getLocation();
    }
    
    public void setHome(Location home) {
        if (home == null && this.home != null) {
            TeleportTimer timer = Main.get().getTimerManager().getTeleportTimer();
            for (Player player : this.getOnlinePlayers()) {
                Location destination = (Location)timer.getDestination(player);
                if (Objects.equal(destination, this.home.getLocation())) {
                    timer.clearCooldown(player);
                    player.sendMessage(ChatColor.RED + "Your home was unset, so your " + timer.getName() + ChatColor.RED + " timer has been cancelled");
                }
            }
        }
        this.home = ((home == null) ? null : new PersistableLocation(home));
    }
    
    public String getAnnouncement() {
        return this.announcement;
    }
    
    public void setAnnouncement(@Nullable String announcement) {
        this.announcement = announcement;
    }
    
    public boolean isOpen() {
        return this.open;
    }
    
    public void setOpen(boolean open) {
        this.open = open;
    }
    
    public int getBalance() {
        return this.balance;
    }
    
    public int getPoints() {
        return this.points;
    }
    
    public int getKothCaptures() {
        return this.kothCaptures;
    }
    
    public void setKothCaptures(int kothCaptures) {
        this.kothCaptures = kothCaptures;
    }
    
    public void setBalance(int balance) {
        this.balance = balance;
    }
    
    public void setPoints(int points) {
        this.points = points;
    }
    
    @Override
    public boolean isRaidable() {
        return this.deathsUntilRaidable <= 0.0;
    }
    
    @Override
    public double getDeathsUntilRaidable() {
        return this.getDeathsUntilRaidable(true);
    }
    
    @Override
    public double getMaximumDeathsUntilRaidable() {
        if (this.members.size() == 1) {
            return 1.1;
        }
        return Math.min(5.5, this.members.size() * 1.0 + 0.1);
    }
    
    public double getDeathsUntilRaidable(boolean updateLastCheck) {
        if (updateLastCheck) {
            this.updateDeathsUntilRaidable();
        }
        return this.deathsUntilRaidable;
    }
    
    public ChatColor getDtrColour() {
        this.updateDeathsUntilRaidable();

        if (this.deathsUntilRaidable < 0.0) {
            return ChatColor.DARK_RED;
        }
        return ChatColor.GREEN;
    }

    public String getDtrColourString() {
        this.updateDeathsUntilRaidable();

        if (this.deathsUntilRaidable < 0.0) {
            return "&4";
        }
        return "&a";
    }
    
    private void updateDeathsUntilRaidable() {
        if (this.getRegenStatus() == RegenStatus.REGENERATING) {
            long now = System.currentTimeMillis();
            long millisPassed = now - this.lastDtrUpdateTimestamp;
            if (millisPassed >= ConfigurationService.DTR_MILLIS_BETWEEN_UPDATES) {
                long remainder = millisPassed % ConfigurationService.DTR_MILLIS_BETWEEN_UPDATES;
                int multiplier = (int)((millisPassed + remainder) / ConfigurationService.DTR_MILLIS_BETWEEN_UPDATES);
                double increase = multiplier * 1.0;
                this.lastDtrUpdateTimestamp = now - remainder;
                this.setDeathsUntilRaidable(this.deathsUntilRaidable + increase);
            }
        }
    }
    
    @Override
    public double setDeathsUntilRaidable(double deathsUntilRaidable) {
        return this.setDeathsUntilRaidable(deathsUntilRaidable, true);
    }
    
    private double setDeathsUntilRaidable(double deathsUntilRaidable, boolean limit) {
        deathsUntilRaidable = deathsUntilRaidable * 100.0 / 100.0;
        if (limit) {
            deathsUntilRaidable = Math.min(deathsUntilRaidable, this.getMaximumDeathsUntilRaidable());
        }
        if (deathsUntilRaidable - this.deathsUntilRaidable != 0.0) {
            FactionDtrChangeEvent event = new FactionDtrChangeEvent(FactionDtrChangeEvent.DtrUpdateCause.REGENERATION, this, this.deathsUntilRaidable, deathsUntilRaidable);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                deathsUntilRaidable = event.getNewDtr();
                if (deathsUntilRaidable > 0.0 && deathsUntilRaidable <= 0.0) {
                    Main.get().getLogger().info("Faction " + this.getName() + " is now raidable.");
                }
                this.lastDtrUpdateTimestamp = System.currentTimeMillis();
                return this.deathsUntilRaidable = deathsUntilRaidable;
            }
        }
        return this.deathsUntilRaidable;
    }
    
    protected long getRegenCooldownTimestamp() {
        return this.regenCooldownTimestamp;
    }
    
    @Override
    public long getRemainingRegenerationTime() {
        return (this.regenCooldownTimestamp == 0L) ? 0L : (this.regenCooldownTimestamp - System.currentTimeMillis());
    }
    
    @Override
    public void setRemainingRegenerationTime(long millis) {
        long systemMillis = System.currentTimeMillis();
        this.regenCooldownTimestamp = systemMillis + millis;
        this.lastDtrUpdateTimestamp = systemMillis + ConfigurationService.DTR_MILLIS_BETWEEN_UPDATES * 2L;
    }
    
    @Override
    public RegenStatus getRegenStatus() {
        if (this.getRemainingRegenerationTime() > 0L) {
            return RegenStatus.PAUSED;
        }
        if (this.getMaximumDeathsUntilRaidable() > this.deathsUntilRaidable) {
            return RegenStatus.REGENERATING;
        }
        return RegenStatus.FULL;
    }
    
    @SuppressWarnings({ "unused", "rawtypes", "deprecation" })
	@Override
    public void printDetails(CommandSender sender) {
        String leaderName = null;
        HashSet<String> coleaderName = new HashSet<>();
        HashSet<String> allyNames = new HashSet<>(1);
        for (Map.Entry<UUID, Relation> memberNames : this.relations.entrySet()) {
            Faction captainNames = Main.get().getFactionManager().getFaction(memberNames.getKey());
            if (captainNames instanceof PlayerFaction) {
                PlayerFaction playerFaction = (PlayerFaction)captainNames;
                allyNames.add(String.valueOf(playerFaction.getDisplayName(sender)) + ChatColor.GRAY + '[' + ChatColor.GRAY + playerFaction.getOnlinePlayers(sender).size() + ChatColor.GRAY + '/' + ChatColor.GRAY + playerFaction.members.size() + ChatColor.GRAY + ']');
            }
        }
        HashSet<String> memberNames2 = new HashSet<>();
        HashSet<String> captainNames2 = new HashSet<>();

        for (Map.Entry entry : this.members.entrySet()) {
            FactionMember factionMember = (FactionMember) entry.getValue();
            Player target = factionMember.toOnlinePlayer();
            FactionUser user = Main.get().getUserManager().getUser((UUID) entry.getKey());
            Deathban deathban = user.getDeathban();
            int kills = user.getKills();
            ChatColor colour;
            if (target == null || (sender instanceof Player && !((Player)sender).canSee(target))) {
                colour = ChatColor.GRAY;
            }
            else {
                colour = ChatColor.GREEN;
            }
            if (deathban != null && deathban.isActive()) {
                colour = ChatColor.RED;
            }
            String memberName = colour + factionMember.getName() + ChatColor.GRAY + '[' + ChatColor.WHITE + kills + ChatColor.GRAY + ']';
            /*Roles*/
            if(factionMember.getRole() == Role.LEADER) {
                leaderName = memberName;
            }
            else if(factionMember.getRole() == Role.COLEADER) {
                coleaderName.add(memberName);
            }
            else if(factionMember.getRole() == Role.CAPTAIN) {
                captainNames2.add(memberName);
            } else {
                memberNames2.add(memberName);
            }
        }
        long dtrRegenRemaining = this.getRemainingRegenerationTime();
        sender.sendMessage(CC.translate(BukkitUtils.STRAIGHT_LINE_DEFAULT));
        sender.sendMessage(CC.translate("&6&l" + this.getName() + " &7[" + this.getOnlineMembers().size() + "/" + this.getMembers().size() + "] " + "&3-"
                + " &7HQ&7: &f" + (this.home == null ? "None" : this.home.getLocation().getBlockX() + ", " + this.home.getLocation().getBlockZ())));
        if (leaderName != null) {
            sender.sendMessage(CC.translate("&bLeader&7: " + leaderName));
        }
        if (!coleaderName.isEmpty()) {
            sender.sendMessage(CC.translate("&bCo-Leader&7: " + StringUtils.join(coleaderName, ChatColor.GRAY + ", ")));
        }
        if (!captainNames2.isEmpty()) {
            sender.sendMessage(CC.translate("&bCaptains&7: " + StringUtils.join(captainNames2, ChatColor.GRAY + ", ")));
        }
        if (!memberNames2.isEmpty()) {
            sender.sendMessage(CC.translate("&bMembers&7: " + StringUtils.join(memberNames2, ChatColor.GRAY + ", ")));
        }
        if (!allyNames.isEmpty()) {
            sender.sendMessage(CC.translate("&bAllies&7: " + StringUtils.join(allyNames, ChatColor.GRAY + ", ")));
        }

        sender.sendMessage(CC.translate("&bKills&7: &f" + this.kills));
        sender.sendMessage(CC.translate("&bDeaths&7: &f" + this.deaths));
        sender.sendMessage(CC.translate("&bPoints&7: &f" + this.points));
        sender.sendMessage(CC.translate("&bBalance&7: &9$" + this.balance));

        if (this.kothCaptures > 0) {
            sender.sendMessage(CC.translate("&bKOTH Captured&7: &f" + this.kothCaptures));
        }

        sender.sendMessage(CC.translate("&bDeaths Until Raidable&7: " + this.getDtrColourString() + JavaUtils.format(this.getDeathsUntilRaidable(false)) + "&7/" + this.getMaximumDeathsUntilRaidable() + this.getRegenStatus().getSymbol()));

        if (dtrRegenRemaining > 0L) {
            sender.sendMessage(CC.translate("&bTime Until Regen&7: &c" + DurationFormatUtils.formatDurationWords(dtrRegenRemaining, true, true)));
        }

        if (sender instanceof Player) {
            Faction playerFaction2 = Main.get().getFactionManager().getPlayerFaction((Player)sender);
            if (playerFaction2 != null && this.announcement != null && playerFaction2.equals(this)) {
                sender.sendMessage(CC.translate("&bAnnouncement&7: &f" + this.announcement));
            }
        }
        sender.sendMessage(CC.translate(BukkitUtils.STRAIGHT_LINE_DEFAULT));
    }
    
    public void broadcast(String message) {
        this.broadcast(message, PlayerFaction.EMPTY_UUID_ARRAY);
    }
    
    public void broadcast(String[] messages) {
        this.broadcast(messages, PlayerFaction.EMPTY_UUID_ARRAY);
    }

    public void broadcast(String message, @Nullable UUID... ignore) {
        this.broadcast(new String[] { message }, ignore);
    }
    
    public void broadcast(String[] messages, UUID... ignore) {
        Preconditions.checkNotNull(messages, "Messages cannot be null");
        Preconditions.checkArgument(messages.length > 0, "MessageManager array cannot be empty");
        Collection<Player> players = this.getOnlinePlayers();
        Collection<UUID> ignores = ((ignore.length == 0) ? Collections.emptySet() : Sets.newHashSet(ignore));
        for (Player player : players) {
            if (!ignores.contains(player.getUniqueId())) {
                player.sendMessage(messages);
            }
        }
    }

    public Location getRally() {
        return (this.rallyloc == null) ? null : this.rallyloc;
    }
    public void setRally(final Location rallyloc) {
        this.rallyloc = rallyloc;
    }

    public void setRallyEnabled(final boolean b) {
        this.rally = b;
    }

    public Boolean isRallyEnabled() {
        return this.rally;
    }

    public Player getRallyPlayer() {
        return this.rallyPlayer;
    }

    public void setRallyPlayer(final Player rallyPlayer) {
        this.rallyPlayer = rallyPlayer;
    }

    /*public BukkitTask getRallyTask() {
        return this.rallyTask;
    }

    public void setRallyTask(final BukkitTask rallyTask) {
        this.rallyTask = rallyTask;
    }*/
}
