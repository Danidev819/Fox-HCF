package me.danidev.core.managers.abilities;

import me.danidev.core.Main;
import me.danidev.core.managers.abilities.impl.*;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.TaskUtils;
import me.danidev.core.utils.file.FileConfig;
import me.danidev.core.utils.item.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

@Getter
public class AbilityManager {

    private final FileConfig langConfig = Main.get().getLangConfig();
    private final FileConfig abilitiesConfig = Main.get().getAbilitiesConfig();

    private Beacon beacon;
    private GuardianAngel guardianAngel;
    private Combo combo;
    private EffectDisabler effectDisabler;
    private NinjaStar ninjaStar;
    private PocketBard pocketBard;
    private Scrammbler scrammbler;
    private Strength strength;
    private SwapperAxe swapperAxe;
    private Switcher switcher;
    private TankIngot tankIngot;
    private Waypoint waypoint;
    private AntiTrapper antitrapper;
    private Cookie cookie;
    private Rocket rocket;
    public TimeWarp timeWarp;
    private FakeLogger fakeLogger;

    public AbilityManager() {
        this.register();
    }

    private void register() {
        this.beacon = new Beacon();
        this.guardianAngel = new GuardianAngel();
        this.combo = new Combo();
        this.antitrapper = new AntiTrapper();
        this.effectDisabler = new EffectDisabler();
        this.ninjaStar = new NinjaStar();
        this.pocketBard = new PocketBard();
        this.scrammbler = new Scrammbler();
        this.strength = new Strength();
        this.swapperAxe = new SwapperAxe();
        this.switcher = new Switcher();
        this.tankIngot = new TankIngot();
        this.waypoint = new Waypoint();
        this.rocket = new Rocket();
        this.cookie = new Cookie();
        this.timeWarp = new TimeWarp();
        this.fakeLogger = new FakeLogger();
    }

    public void load() {
        Ability.getAbilities().forEach(Ability::register);
    }

    public ItemStack getAbility(String ability, int amount) {
        return new ItemBuilder(getMaterial(ability))
                .amount(amount)
                .data(getData(ability))
                .name(getDisplayName(ability))
                .lore(getDescription(ability))
                .build();
    }

    public String getDisplayName(String ability) {
        return abilitiesConfig.getString( ability + ".ICON.DISPLAYNAME");
    }

    public List<String> getDescription(String ability) {
        return abilitiesConfig.getStringList( ability + ".ICON.DESCRIPTION");
    }

    public Material getMaterial(String ability) {
        return Material.valueOf(abilitiesConfig.getString(ability + ".ICON.MATERIAL"));
    }

    public int getData(String ability) {
        return abilitiesConfig.getInt(ability + ".ICON.DATA");
    }

    public int getCooldown(String ability) {
        return abilitiesConfig.getInt(ability + ".COOLDOWN");
    }

    public Set<String> getAbilities() {
        return abilitiesConfig.getConfiguration().getKeys(false);
    }

    public void giveAbility(CommandSender sender, Player player, String key, String abilityName, int amount) {
        player.getInventory().addItem(this.getAbility(key, amount));
        if (player == sender) {
            CC.message(player, langConfig.getString("RECEIVED_ABILITY")
                    .replace("%ABILITY%", abilityName)
                    .replace("%AMOUNT%", String.valueOf(amount)));
        }
        else {
            CC.message(player, langConfig.getString("RECEIVED_ABILITY")
                    .replace("%ABILITY%", abilityName)
                    .replace("%AMOUNT%", String.valueOf(amount)));
            CC.sender(sender, langConfig.getString("GIVE_ABILITY")
                    .replace("%ABILITY%", abilityName)
                    .replace("%AMOUNT%", String.valueOf(amount))
                    .replace("%PLAYER%", player.getName()));
        }
    }

    public void playerMessage(Player player, String ability) {
        String displayName = getDisplayName(ability);
        String cooldown = String.valueOf(getCooldown(ability));

        abilitiesConfig.getStringList(ability + ".MESSAGE.PLAYER").forEach(
                message -> CC.message(player, message
                    .replace("%ABILITY%", displayName)
                    .replace("%COOLDOWN%", cooldown)));
    }

    public void targetMessage(Player target, Player player, String ability) {
        String displayName = getDisplayName(ability);

        abilitiesConfig.getStringList(ability + ".MESSAGE.TARGET").forEach(
                message -> CC.message(target, message
                        .replace("%ABILITY%", displayName)
                        .replace("%PLAYER%", player.getName())));
    }

    public void cooldown(Player player, String abilityName, String cooldown) {
        CC.message(player, langConfig.getString("STILL_ON_COOLDOWN")
                .replace("%ABILITY%", abilityName)
                .replace("%COOLDOWN%", cooldown));
    }

    public void cooldownExpired(Player player, String abilityName, String ability) {
        TaskUtils.runLaterAsync(() ->
                CC.message(player, langConfig.getString("COOLDOWN_EXPIRED")
                        .replace("%ABILITY%", abilityName)), getCooldown(ability) * 20L);
    }
}
