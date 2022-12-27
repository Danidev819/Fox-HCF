package me.danidev.core;

import me.danidev.core.commands.apply.ApplyExecutor;
import me.danidev.core.commands.balance.BalanceCommand;
import me.danidev.core.commands.balance.PayCommand;
import me.danidev.core.commands.chat.ChatExecutor;
import me.danidev.core.commands.customtimer.CustomTimerExecutor;
import me.danidev.core.commands.koth.KothExecutor;
import me.danidev.core.commands.network.DiscordCommand;
import me.danidev.core.commands.network.TeamspeakCommand;
import me.danidev.core.commands.network.TwitterCommand;
import me.danidev.core.commands.network.WebsiteCommand;
import me.danidev.core.commands.reclaim.ReclaimManager;
import me.danidev.core.commands.suggestion.SuggestionCheckCommand;
import me.danidev.core.commands.suggestion.SuggestionCommand;
import me.danidev.core.commands.wrench.WrenchCommand;
import me.danidev.core.commands.wrench.WrenchGiveCommand;
import me.danidev.core.listeners.*;
import me.danidev.core.listeners.death.DeathListener;
import me.danidev.core.listeners.death.DeathMessageListener;
import me.danidev.core.listeners.fixes.*;
import me.danidev.core.managers.LivesManager;
import me.danidev.core.managers.MessageManager;
import me.danidev.core.managers.PlayTimeManager;
import me.danidev.core.managers.abilities.AbilityManager;
import me.danidev.core.managers.balance.EconomyManager;
import me.danidev.core.managers.balance.FlatFileEconomyManager;
import me.danidev.core.managers.blockshop.BlockShopManager;
import me.danidev.core.managers.classes.mage.MageClass;
import me.danidev.core.managers.customtimer.CustomTimerManager;
import me.danidev.core.managers.deathban.DeathbanManager;
import me.danidev.core.managers.eotw.EOTWCommand;
import me.danidev.core.managers.eotw.EOTWHandler;
import me.danidev.core.managers.eotw.EOTWListener;
import me.danidev.core.managers.faction.type.*;
import me.danidev.core.managers.games.citadel.CitadelCommand;
import me.danidev.core.managers.classes.PvPClassManager;
import me.danidev.core.managers.classes.others.ArcherClass;
import me.danidev.core.managers.lunar.WaypointManager;
import me.danidev.core.managers.rank.RankManager;
import me.danidev.core.managers.suggestions.SuggestionManager;
import me.danidev.core.managers.support.PartnerManager;
import me.danidev.core.managers.support.data.SupportData;
import me.danidev.core.managers.timer.type.sotw.SOTWCommand;
import me.danidev.core.managers.timer.type.sotw.SOTWTimer;
import me.danidev.core.providers.NametagProvider;
import me.danidev.core.providers.TablistProvider;
import me.danidev.core.task.ApplyTask;
import me.danidev.core.task.OnlineDonatorTask;
import me.danidev.core.managers.timer.TimerExecutor;
import me.danidev.core.managers.timer.TimerManager;
import me.danidev.core.utils.Cooldown;
import me.danidev.core.utils.DateTimeFormats;
import me.danidev.core.utils.cuboid.Cuboid;
import me.danidev.core.utils.cuboid.NamedCuboid;
import me.danidev.core.utils.file.FileConfig;
import me.danidev.core.utils.itemdb.ItemDb;
import me.danidev.core.utils.itemdb.SimpleItemDb;
import me.danidev.core.utils.menu.ButtonListener;
import me.danidev.core.utils.nametags.Nametag;
import me.danidev.core.utils.others.PersistableLocation;
import me.danidev.core.utils.others.SignHandler;
import me.danidev.core.utils.scoreboard.Assemble;
import me.danidev.core.utils.scoreboard.AssembleStyle;
import me.danidev.core.utils.service.ScoreboardService;
import me.danidev.core.utils.tablist.TablistManager;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import me.danidev.core.commands.message.MessageCommand;
import me.danidev.core.commands.message.ReplyCommand;
import me.danidev.core.commands.message.TogglePMCommand;
import me.danidev.core.commands.death.DeathExecutor;
import me.danidev.core.commands.essentials.BroadcastCommand;
import me.danidev.core.commands.essentials.EnchantCommand;
import me.danidev.core.commands.essentials.FeedCommand;
import me.danidev.core.commands.essentials.FlyCommand;
import me.danidev.core.commands.essentials.GamemodeCommand;
import me.danidev.core.commands.essentials.HealCommand;
import me.danidev.core.commands.essentials.KillCommand;
import me.danidev.core.commands.essentials.ListCommand;
import me.danidev.core.commands.essentials.NearCommand;
import me.danidev.core.commands.essentials.PingCommand;
import me.danidev.core.commands.essentials.PlayTimeCommand;
import me.danidev.core.commands.essentials.RenameCommand;
import me.danidev.core.commands.essentials.RepairAllCommand;
import me.danidev.core.commands.essentials.RepairCommand;
import me.danidev.core.commands.network.StoreCommand;
import me.danidev.core.commands.essentials.SudoCommand;
import me.danidev.core.commands.inventory.ClearInvCommand;
import me.danidev.core.commands.inventory.GiveCommand;
import me.danidev.core.commands.inventory.InvSeeCommand;
import me.danidev.core.commands.inventory.ItemCommand;
import me.danidev.core.commands.inventory.MoreCommand;
import me.danidev.core.commands.inventory.SkullCommand;
import me.danidev.core.commands.lives.LivesExecutor;
import me.danidev.core.commands.reclaim.Reclaim;
import me.danidev.core.commands.reclaim.argument.ReclaimCommand;
import me.danidev.core.commands.reclaim.argument.SetReclaimCommand;
import me.danidev.core.commands.teleport.BackCommand;
import me.danidev.core.commands.teleport.TeleportAllCommand;
import me.danidev.core.commands.teleport.TeleportCommand;
import me.danidev.core.commands.teleport.TeleportHereCommand;
import me.danidev.core.commands.teleport.WorldCommand;
import me.danidev.core.managers.faction.FactionExecutor;
import me.danidev.core.managers.faction.FactionManager;
import me.danidev.core.managers.faction.FactionMember;
import me.danidev.core.managers.faction.FlatFileFactionManager;
import me.danidev.core.managers.faction.claim.Claim;
import me.danidev.core.managers.faction.claim.ClaimHandler;
import me.danidev.core.managers.faction.claim.ClaimWandListener;
import me.danidev.core.managers.faction.claim.Subclaim;
import me.danidev.core.managers.killstreaks.KillStreakListener;
import me.danidev.core.managers.kit.FlatFileKitManager;
import me.danidev.core.managers.kit.Kit;
import me.danidev.core.managers.kit.KitExecutor;
import me.danidev.core.managers.kit.KitListener;
import me.danidev.core.managers.kit.KitManager;
import me.danidev.core.managers.games.koth.CaptureZone;
import me.danidev.core.managers.games.koth.EventExecutor;
import me.danidev.core.managers.games.koth.conquest.ConquestExecutor;
import me.danidev.core.managers.games.koth.faction.CapturableFaction;
import me.danidev.core.managers.games.koth.faction.ConquestFaction;
import me.danidev.core.managers.games.koth.faction.KothFaction;
import me.danidev.core.providers.ScoreboardProvider;
import me.danidev.core.managers.user.BaseUser;
import me.danidev.core.managers.user.BaseUserManager;
import me.danidev.core.managers.user.ConsoleUser;
import me.danidev.core.managers.user.FactionUser;
import me.danidev.core.managers.user.NameHistory;
import me.danidev.core.managers.user.ServerParticipator;
import me.danidev.core.managers.user.UserManager;
import me.danidev.core.utils.service.ConfigurationService;
import me.danidev.core.utils.Cooldowns;
import me.danidev.core.utils.visualise.ProtocolLibHook;
import me.danidev.core.utils.visualise.VisualiseHandler;
import me.danidev.core.utils.visualise.WallBorderListener;
import me.danidev.core.commands.*;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Main extends JavaPlugin {

    private FileConfig mainConfig;
    private FileConfig scoreboardConfig;
    private FileConfig langConfig;
    private FileConfig locationsConfig;
    private FileConfig reclaimConfig;
    private FileConfig suggestionsConfig;
    private FileConfig blockShopConfig;
    private FileConfig abilitiesConfig;
    private FileConfig supportConfig;
    private FileConfig dataConfig;
    private FileConfig kothwebhookConfig;
    private FileConfig deathbanConfig;
    private FileConfig livesConfig;
    private FactionManager factionManager;
    private EconomyManager economyManager;
    private DeathbanManager deathbanManager;
    private Reclaim reclaimManager;
    private MessageManager messageManager;
    private PvPClassManager pvpClassManager;
    private TimerManager timerManager;
    private UserManager userManager;
    private BaseUserManager baseUserManager;
    private KitManager kitManager;
    private PlayTimeManager playTimeManager;
    private RankManager rankManager;
    private WaypointManager waypointManager;
    private SuggestionManager suggestionManager;
    private BlockShopManager blockShopManager;
    @Getter private CombatLogListener combatLogListener;
    private CustomTimerManager customTimerManager;
    private AbilityManager abilityManager;
    private ClaimHandler claimHandler;
    private VisualiseHandler visualiseHandler;
    private SignHandler signHandler;
    private KitExecutor kitExecutor;
    private WorldEditPlugin worldEdit;
    private ItemDb itemDb;
    private boolean kitMap;
    private boolean protocolLib;
    private boolean pandaAbility;
    private Cooldown partneritem = new Cooldown();
    private LivesManager livesManager;
    public Cooldown getPartnerItem(){ return this.partneritem; }
    private Cooldown beacom = new Cooldown();
    public Cooldown getBeacon(){ return this.beacom; }
    private Cooldown combo  = new Cooldown();
    public Cooldown getCombo(){ return this.combo; }
    private Cooldown fakelogger = new Cooldown();
    public Cooldown getFakelogger(){ return this.fakelogger; }
    private Cooldown effectdisabler = new Cooldown();
    public Cooldown getEffectDisabler(){ return this.effectdisabler; }
    private Cooldown guardianangel = new Cooldown();
    public Cooldown getGuardianAngel(){ return this.guardianangel; }
    private Cooldown ninjastar = new Cooldown();
    public Cooldown getNinjaStar(){ return this.ninjastar; }
    private Cooldown pocketbard = new Cooldown();
    public Cooldown getPocketBard(){ return this.pocketbard; }
    private Cooldown scrammbler = new Cooldown();
    public Cooldown getScrammbler(){ return this.scrammbler; }
    private Cooldown strength = new Cooldown();
    public Cooldown getStrength(){ return this.strength; }
    private Cooldown swapperaxe = new Cooldown();
    public Cooldown getSwapperAxe(){ return this.swapperaxe; }
    private Cooldown timewarp = new Cooldown();
    public Cooldown getTimeWarp(){ return this.timewarp; }
    private Cooldown switcher = new Cooldown();
    public Cooldown getSwitcher(){ return this.switcher; }
    private Cooldown tankingot = new Cooldown();
    public Cooldown getTankIngot(){ return this.tankingot; }
    private Cooldown cookie = new Cooldown();
    public Cooldown getCookie(){ return this.cookie; }
    private Cooldown waypoint = new Cooldown();
    public Cooldown getWaypoint(){ return this.waypoint; }
    private Cooldown rocket = new Cooldown();
    public Cooldown getRocket(){ return this.rocket; }
    private Cooldown antitrapper = new Cooldown();
    public Cooldown getAntiTrapper(){ return this.antitrapper; }
    private PartnerManager partnerManager;
    @Getter private SOTWTimer sotwTimer;
    @Getter private EOTWHandler eotwHandler;
    public static final long MINUTE = TimeUnit.MINUTES.toMillis(1L);
    public static final long HOUR = TimeUnit.HOURS.toMillis(1L);

    private static Main plugin;

    @Override
    public void onEnable() {
        Main.plugin = this;

        this.loadConfigs();

        this.loadClass();
        this.loadCooldowns();

        ProtocolLibHook.hook();
        ConfigurationService.init(this.getMainConfig());
        ScoreboardService.init(this.getScoreboardConfig());

        Plugin wep = Bukkit.getPluginManager().getPlugin("WorldEdit");
        this.worldEdit = ((wep instanceof WorldEditPlugin && wep.isEnabled()) ? ((WorldEditPlugin) wep) : null);

        if (ConfigurationService.KITMAP) {
            this.kitMap = true;
        }

        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
            this.protocolLib = true;
        }

        if (Bukkit.getPluginManager().getPlugin("PandaAbility") != null) {
            this.pandaAbility = true;
        }

        this.loadOthers();
        this.loadCommands();
        this.loadManagers();
        SupportData.load();
        this.getPartnerManager().loadSupport();
        this.loadListeners();
        this.reloadSchedulers();
        this.getRankManager().loadRank();
        this.loadData();
        this.getAbilityManager().load();
        Assemble assemble = new Assemble(this, new ScoreboardProvider());
        assemble.setTicks(2);
        assemble.setAssembleStyle(AssembleStyle.FOX);
        if(Main.get().getMainConfig().getBoolean("TABLIST")){
            new TablistManager(this, new TablistProvider(), 500L);
        }
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, new ApplyTask(), 20L * 300L, 20L * 300L);

        if (this.getMainConfig().getBoolean("ONLINE_DONATOR.ENABLE")) {
            int minutes = this.getMainConfig().getInt("ONLINE_DONATOR.INTERVAL") * 60 * 20;
            this.getServer().getScheduler().runTaskTimerAsynchronously(this, new OnlineDonatorTask(), minutes, minutes);
        }
    }

    @Override
    public void onDisable() {
        CombatLogListener.removeCombatLoggers();
        Bukkit.getServer().savePlayers();
        SupportData.save();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
        this.pvpClassManager.onDisable();
        this.signHandler.cancelTasks(null);
        this.saveData();
    }

    public void saveData() {
        this.deathbanManager.save();
        this.livesManager.saveAll();
        CombatLogListener.removeCombatLoggers();
        this.getEconomyManager().saveEconomyData();
        this.getFactionManager().saveFactionData();
        this.getBaseUserManager().saveParticipatorData();
        this.getUserManager().saveUserData();
        this.getUserManager().saveParticipatorData();
        this.getKitManager().saveKitData();
        this.getPlayTimeManager().savePlaytimeData();
        this.getSuggestionManager().save();
        timerManager.saveTimerData();
    }

    private void loadData() {
        this.getUserManager().reloadParticipatorData();
        this.getUserManager().reloadUserData();
        this.getSuggestionManager().load();
        this.getBlockShopManager().loadCategories();
        this.getBlockShopManager().loadBlocks();
    }

    private void loadConfigs() {
        this.mainConfig = new FileConfig(this, "config.yml");
        this.scoreboardConfig = new FileConfig(this, "providers/scoreboard.yml");
        this.langConfig = new FileConfig(this, "language/lang.yml");
        this.locationsConfig = new FileConfig(this, "locations.yml");
        this.reclaimConfig = new FileConfig(this, "miscellaneous/reclaim.yml");
        this.suggestionsConfig = new FileConfig(this, "miscellaneous/suggestions.yml");
        this.blockShopConfig = new FileConfig(this, "miscellaneous/blockshop.yml");
        this.abilitiesConfig = new FileConfig(this, "miscellaneous/abilities.yml");
        this.supportConfig = new FileConfig(this, "support/partners.yml");
        this.dataConfig = new FileConfig(this, "support/data.yml");
        this.kothwebhookConfig = new FileConfig(this,"webhooks/koth.yml");
        this.deathbanConfig = new FileConfig(this, "deathbans.yml");
        this.livesConfig = new FileConfig(this, "lives.yml");
    }

    private void loadClass() {
        ConfigurationSerialization.registerClass(CaptureZone.class);
        ConfigurationSerialization.registerClass(Claim.class);
        ConfigurationSerialization.registerClass(Subclaim.class);
        ConfigurationSerialization.registerClass(FactionUser.class);
        ConfigurationSerialization.registerClass(ClaimableFaction.class);
        ConfigurationSerialization.registerClass(ConquestFaction.class);
        ConfigurationSerialization.registerClass(CapturableFaction.class);
        ConfigurationSerialization.registerClass(KothFaction.class);
        ConfigurationSerialization.registerClass(EndPortalFaction.class);
        ConfigurationSerialization.registerClass(EndPortalFaction.EndPortalFaction1.class);
        ConfigurationSerialization.registerClass(EndPortalFaction.EndPortalFaction2.class);
        ConfigurationSerialization.registerClass(EndPortalFaction.EndPortalFaction3.class);
        ConfigurationSerialization.registerClass(EndPortalFaction.EndPortalFaction4.class);
        ConfigurationSerialization.registerClass(Faction.class);
        ConfigurationSerialization.registerClass(FactionMember.class);
        ConfigurationSerialization.registerClass(PlayerFaction.class);
        ConfigurationSerialization.registerClass(SpawnFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.NorthRoadFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.EastRoadFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.SouthRoadFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.WestRoadFaction.class);
        ConfigurationSerialization.registerClass(GlowstoneFaction.class);
        ConfigurationSerialization.registerClass(Kit.class);
        ConfigurationSerialization.registerClass(ServerParticipator.class);
        ConfigurationSerialization.registerClass(BaseUser.class);
        ConfigurationSerialization.registerClass(ConsoleUser.class);
        ConfigurationSerialization.registerClass(NameHistory.class);
        ConfigurationSerialization.registerClass(PersistableLocation.class);
        ConfigurationSerialization.registerClass(Cuboid.class);
        ConfigurationSerialization.registerClass(NamedCuboid.class);
    }

    private void loadCooldowns() {
        Cooldowns.createCooldown("DENY-BLOCK");
        Cooldowns.createCooldown("TELEPORT");
        Cooldowns.createCooldown("SUGGESTION_COOLDOWN");
        Cooldowns.createCooldown("ARCHER_ITEM_COOLDOWN");
        Cooldowns.createCooldown("ARCHER_JUMP_COOLDOWN");
        Cooldowns.createCooldown("chat_delay");
    }

    private void loadManagers() {
        this.timerManager = new TimerManager(this);
        this.partnerManager = new PartnerManager();
        this.claimHandler = new ClaimHandler(this);
        this.deathbanManager = new DeathbanManager();
        this.economyManager = new FlatFileEconomyManager(this);
        this.factionManager = new FlatFileFactionManager(this);
        this.livesManager = new LivesManager();
        this.pvpClassManager = new PvPClassManager(this);
        this.timerManager = new TimerManager(this);
        this.userManager = new UserManager(this);
        this.baseUserManager = new BaseUserManager(this);
        this.visualiseHandler = new VisualiseHandler();
        this.messageManager = new MessageManager();
        this.eotwHandler = new EOTWHandler(this);
        this.reclaimManager = new ReclaimManager();
        this.kitManager = new FlatFileKitManager(this);
        this.signHandler = new SignHandler(this);
        this.itemDb = new SimpleItemDb(this);
        this.rankManager = new RankManager(this);
        this.playTimeManager = new PlayTimeManager(this);
        this.waypointManager = new WaypointManager();
        this.suggestionManager = new SuggestionManager();
        this.blockShopManager = new BlockShopManager();
        this.customTimerManager = new CustomTimerManager();
        this.abilityManager = new AbilityManager();
        this.sotwTimer = new SOTWTimer();
    }

    private void loadCommands() {
        new ApplyExecutor(this);
        new BalanceCommand(this);
        new PayCommand(this);
        new ChatExecutor(this);
        new DeathExecutor(this);
        new CustomTimerExecutor(this);
        new EOTWCommand(this);
        new TimerExecutor(this);
        new ConquestExecutor(this);
        new KitExecutor(this);
        new KothExecutor(this);
        new EventExecutor(this);
        new KeyAllCommand(this);
        new BroadcastCommand(this);
        new EnchantCommand(this);
        new FeedCommand(this);
        new FlyCommand(this);
        new GamemodeCommand(this);
        new HealCommand(this);
        new KillCommand(this);
        new ListCommand(this);
        new NearCommand(this);
        new RenameCommand(this);
        new RepairCommand(this);
        new RepairAllCommand(this);
        new SudoCommand(this);
        new SetDeathbanSpawnCommand();
        new ClearInvCommand(this);
        new GiveCommand(this);
        new InvSeeCommand(this);
        new ItemCommand(this);
        new MoreCommand(this);
        new SkullCommand(this);
        new ReclaimCommand(this);
        new SetReclaimCommand(this);
        new SuggestionCheckCommand(this);
        new BackCommand(this);
        new TeleportAllCommand(this);
        new TeleportCommand(this);
        new TeleportHereCommand(this);
        new WorldCommand(this);
        new WrenchCommand(this);
        new WrenchGiveCommand(this);
        new CoordsCommand(this);
        new CraftCommand(this);
        new EnderchestCommand(this);
        new FFACommand(this);
        new PvPTimerCommand(this);
        new SafeStopCommand(this);
        new SetCommand(this);
        new SpawnCommand(this);
        new SpawnerCommand(this);
        new StatsResetCommand(this);
        new CitadelCommand(this);
        new NewVideoCommand(this);
        new StreamCommand(this);
        new AbilityCommand(this);
        if (isKitMap()) new KeysCommand(this);
        Map<String, Map<String, Object>> map = this.getDescription().getCommands();

        for (Map.Entry<String, Map<String, Object>> entry : map.entrySet()) {
            PluginCommand command = this.getCommand(entry.getKey());
            command.setPermission("fhcf.command." + entry.getKey());
        }
    }

    private void loadOthers(){
        new FactionExecutor(this);
        new LeaderboardCommand(this);
        new BlockShopCommand(this);
        new SOTWCommand(this);
        new MediaCommand(this);
        new StatsCommand(this);
        new SendCoordsCommand(this);
        new HelpCommand(this);
        new LogoutCommand(this);
        new OresCommand(this);
        new CobbleCommand(this);
        new LFFCommand(this);
        new SuggestionCommand(this);
        new MessageCommand(this);
        new ReplyCommand(this);
        new TogglePMCommand(this);
        if (!this.isKitMap()) new LivesExecutor(this);
        new DiscordCommand(this);
        new StoreCommand(this);
        new TeamspeakCommand(this);
        new TwitterCommand(this);
        new WebsiteCommand(this);
        new PingCommand(this);
        new PlayTimeCommand(this);
        new HCFCoreCommand(this);
        new SupportCommand(this);
    }

    private void loadListeners() {
        if (this.isKitMap()) {
            new KillStreakListener(this);
        }

        if (this.getMainConfig().getBoolean("LUNAR_NAMETAG.ENABLE")) {
            new LunarListener(this);
        }

        new Nametag(new NametagProvider(), this);

        new ArmorFixListener(this);
        new HungerFixListener(this);
        new BlockHitFixListener();
        new SOTWListener(this);
        new EOTWListener(this);
        new LivesListener();
        new PortalFixListener(this);
        new PotionLimitListener(this);
        new EnderPearlListener();
        if(Main.get().getMainConfig().getBoolean("ENDERPEARL-GLITCH")) {
            new PearlGlitchListener();
        }
        if(Main.get().getMainConfig().getBoolean("EVENT-PEARL")) {
            new EnderPearlFix();
        }
        new ArcherBeaconSpeedFixListener(this);
        new BlockJumpGlitchFixListener();
        new DupeGlitchFix();
        new WeatherFixListener(this);
        new CobbleListener(this);
        new ColouredSignListener(this);
        new KitMapListener(this);

        new ElevatorListener(this);
        new CombatLogListener(this);
        new ArcherClass(this);
        new MageClass(this);
        new BorderListener(this);
        new ChatListener(this);
        new ClaimWandListener(this);
        new CoreListener(this);
        new CreeperFriendlyListener(this);
        new WrenchListener(this);
        new DeathListener(this);
        new DeathMessageListener(this);
        new DeathbanListener();
        new EntityLimitListener(this);
        new FlatFileFactionManager(this);
        new EndListener(this);
        new ExpMultiplierListener(this);
        new FactionListener(this);
        new FurnaceListener(this);
        new KitListener(this);
        new PotionLimitListener(this);
        new FactionsCoreListener(this);
        new SubclaimListener(this);
        new ShopSignListener(this);
        new SkullListener(this);
        new WallBorderListener(this);
        new WorldListener(this);
        new BrewingListener(this);
        new ChatListener(this);
        new PlayerMonitorListener(this);
        new KitListener(this);
        new MoveFixListener();
        new MobstackListener(this);
        new ButtonListener(this);
        new SuggestionListener(this);
    }

    private void reloadSchedulers() {
        MobstackListener mobstackListener = new MobstackListener(this);
        mobstackListener.runTaskTimerAsynchronously(this, 20L, 20L);
    }

    public static String getRemaining(long millis, boolean milliseconds) {
        return getRemaining(millis, milliseconds, true);
    }
    public static String getRemaining(long duration, boolean milliseconds, boolean trail) {
        if ((milliseconds) && (duration < MINUTE)) {
            return (trail ? DateTimeFormats.REMAINING_SECONDS_TRAILING
                    : DateTimeFormats.REMAINING_SECONDS).get().format(duration * 0.001D) + 's';
        }
        return DurationFormatUtils.formatDuration(duration, (duration >= HOUR ? "HH:" : "") + "mm:ss");
    }

    public static Main get() {
        return getPlugin(Main.class);
    }
}
