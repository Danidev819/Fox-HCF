package me.danidev.core.commands;

import me.danidev.core.Main;
import me.danidev.core.managers.abilities.Ability;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.JavaUtils;
import me.danidev.core.utils.file.FileConfig;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Getter
@Setter
public class AbilityCommand implements CommandExecutor {

    private Main plugin = Main.get();
    private FileConfig abilitiesConfig = Main.get().getAbilitiesConfig();

    public AbilityCommand(Main plugin) {
        plugin.getCommand("ability").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            this.getUsage(sender, label);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "give":
                if (args.length < 4) {
                    CC.sender(sender, "&cUsage: /" + label + " give <player> <ability|all> <amount>");
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);

                if (target == null) {
                    CC.sender(sender, "&cPlayer '" + args[1] + "' not found.");
                    return true;
                }

                Integer amount = JavaUtils.tryParseInt(args[3]);

                if (amount == null) {
                    CC.sender(sender, "&cAmount must be a number.");
                    return true;
                }
                if (amount <= 0) {
                    CC.sender(sender, "&cAmount must be positive.");
                    return true;
                }

                plugin.getAbilityManager().getAbilities().forEach(ability -> {
                    String displayName = abilitiesConfig.getString(ability + ".ICON.DISPLAYNAME");
                    if (args[2].equalsIgnoreCase(ability)) {
                        plugin.getAbilityManager().giveAbility(sender, target, ability, displayName, amount);
                        return;
                    }
                    if (args[2].equals("all")) {
                        plugin.getAbilityManager().giveAbility(sender, target, ability, displayName, amount);
                    }
                });
                break;
            case "list":
                CC.sender(sender, "&7&m-----------------------------");
                CC.sender(sender, "&6&lAbilities List &7(" + Ability.getAbilities().size() + ")");
                CC.sender(sender, "");
                plugin.getAbilityManager().getAbilities().forEach(
                        ability -> CC.sender(sender, " &7- " + ability));
                CC.sender(sender, "&7&m-----------------------------");
                break;
        }
        return true;
    }

    private void getUsage(CommandSender sender, String label) {
        CC.sender(sender, "&7&m-----------------------------");
        CC.sender(sender, "&6&lAbility Help");
        CC.sender(sender, "");
        CC.sender(sender, "&7/" + label + " give <player> <ability|all> <amount>");
        CC.sender(sender, "&7/" + label + " list");
        CC.sender(sender, "&7&m-----------------------------");
    }
}
