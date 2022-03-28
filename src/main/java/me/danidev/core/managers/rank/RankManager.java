package me.danidev.core.managers.rank;

import me.danidev.core.managers.rank.impl.AquaCore;
import me.danidev.core.managers.rank.impl.Default;
import me.danidev.core.managers.rank.impl.LuckPerms;
import me.danidev.core.managers.rank.impl.PermissionsEx;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

@Getter @Setter
public class RankManager {

    @Getter
    private static RankManager instance;
    private Plugin plugin;
    private String rankSystem;
    private Rank rank;
    private Chat chat;

    public RankManager(Plugin plugin) {
        instance = this;
        this.plugin = plugin;
    }

    public void loadRank() {
        if (Bukkit.getPluginManager().getPlugin("AquaCore") != null) {
            this.setRank(new AquaCore());
            this.setRankSystem("AquaCore");
        }
        else if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            this.loadVault();

            if (this.getChat() == null) {
                this.setRank(new Default());
                this.setRankSystem("None");
                return;
            }

            if (this.getChat().getName().contains("PermissionsEx")) {
                this.setRank(new PermissionsEx());
                this.setRankSystem("PermissionsEx");
            }
            else if (this.getChat().getName().contains("LuckPerms")) {
                this.setRank(new LuckPerms());
                this.setRankSystem("LuckPerms");
            }
        }
        else {
            this.setRank(new Default());
            this.setRankSystem("None");
        }
    }

    private void loadVault() {
        RegisteredServiceProvider<Chat> rsp = plugin.getServer().getServicesManager().getRegistration(Chat.class);
        if (rsp != null) chat = rsp.getProvider();
    }
}
