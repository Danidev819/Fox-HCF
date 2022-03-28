package me.danidev.core.managers;

import java.util.Map;
import java.util.UUID;
import me.danidev.core.Main;
import me.danidev.core.utils.TaskUtils;
import lombok.Getter;
import net.minecraft.util.com.google.common.collect.Maps;

public class LivesManager {

    @Getter
    private final Map<UUID, Integer> livesMap;

    private final int AUTO_SAVE_INTERVAL = 300 * 20;

    public LivesManager() {
        this.livesMap = Maps.newLinkedHashMap();

        TaskUtils.runTaskTimerAsynchronously(() -> Main.get().getLivesConfig().save(), this.AUTO_SAVE_INTERVAL);
    }

    public void load(UUID uuid) {
        if (Main.get().getLivesConfig().getConfiguration().contains("LIVES." + uuid.toString())) {
            int lives = Main.get().getLivesConfig().getInt("LIVES." + uuid.toString());

            if (lives == 0) {
                return;
            }

            this.livesMap.put(uuid, lives);
        }
    }

    public void save(UUID uuid) {
        int lives = this.getLives(uuid);

        if (lives == 0) {
            Main.get().getLivesConfig().getConfiguration().set("LIVES." + uuid.toString(), null);
            return;
        }

        Main.get().getLivesConfig().getConfiguration().set("LIVES." + uuid.toString(), lives);
    }

    public int getLives(UUID uuid) {
        return this.livesMap.getOrDefault(uuid, 0);
    }

    public void addLives(UUID uuid, int amount) {
        int lives = this.getLives(uuid);

        this.livesMap.put(uuid, lives + amount);
    }

    public void takeLives(UUID uuid, int amount) {
        int lives = this.getLives(uuid);

        this.livesMap.put(uuid, Math.max(0, lives - amount));
    }

    public void setLives(UUID uuid, int amount) {
        this.livesMap.put(uuid, amount);
    }

    public void saveAll() {
        this.livesMap.keySet().forEach(uuid -> {
            this.save(uuid);
        });

        Main.get().getLivesConfig().save();
    }
}