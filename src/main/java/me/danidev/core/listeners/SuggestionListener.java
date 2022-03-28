package me.danidev.core.listeners;

import me.danidev.core.Main;
import me.danidev.core.managers.suggestions.Suggestion;
import me.danidev.core.managers.suggestions.SuggestionManager;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.Cooldowns;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SuggestionListener implements Listener {

    private final SuggestionManager suggestionManager = Main.get().getSuggestionManager();

    public SuggestionListener(Main plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (suggestionManager.getMakingSuggestion().contains(player.getUniqueId())) {
            event.setCancelled(true);

            String message = event.getMessage();

            if (message.equalsIgnoreCase("cancel")) {
                suggestionManager.getMakingSuggestion().remove(player.getUniqueId());

                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1F, 1F);
                player.sendMessage(CC.translate("&cYour suggestion process has been cancelled."));
                return;
            }

            int count = suggestionManager.getSuggestions().size() + 1;

            String ID = "SGG-" + count;
            String author = player.getName();
            String suggestion = event.getMessage();

            suggestionManager.getSuggestions().add(new Suggestion(ID, author, suggestion));
            suggestionManager.getMakingSuggestion().remove(player.getUniqueId());

            Cooldowns.addCooldown("SUGGESTION_COOLDOWN", player, 30 * 60);

            player.playSound(player.getLocation(), Sound.VILLAGER_YES, 1F, 1F);
            player.sendMessage(CC.translate("&aYour suggestion '&f" + ID + "&a' has been create."));
        }
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        suggestionManager.getMakingSuggestion().remove(event.getPlayer().getUniqueId());
    }
}
