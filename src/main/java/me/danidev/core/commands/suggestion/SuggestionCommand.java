package me.danidev.core.commands.suggestion;

import me.danidev.core.Main;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.Cooldowns;
import me.danidev.core.utils.DurationFormatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SuggestionCommand implements CommandExecutor {

    public SuggestionCommand(Main plugin) {
        plugin.getCommand("suggestion").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (Cooldowns.isOnCooldown("SUGGESTION_COOLDOWN", player)) {
            player.sendMessage(CC.translate("&cYou can make another suggestion in &l"
                    + DurationFormatter.getRemaining(Cooldowns.getCooldownForPlayerLong("SUGGESTION_COOLDOWN", player), true) + "&c."));
            return true;
        }

        if (Main.get().getSuggestionManager().getMakingSuggestion().contains(player.getUniqueId())) {
            player.sendMessage(CC.translate("&cYou're already making a suggestion."));
            return true;
        }

        Main.get().getSuggestionManager().getMakingSuggestion().add(player.getUniqueId());

        player.sendMessage(CC.translate("&7&m------------------------------"));
        player.sendMessage(CC.translate("&6&lSuggestion"));
        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.translate("&7Write the suggestions in the chat."));
        player.sendMessage(CC.translate("&7Type &ccancel &bin the chat to cancel your suggestion."));
        player.sendMessage(CC.translate("&7&m------------------------------"));
        return true;
    }
}
