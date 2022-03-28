package me.danidev.core.managers.menu.media;

import me.danidev.core.managers.menu.media.buttons.MediaButton;
import me.danidev.core.utils.menu.Button;
import me.danidev.core.utils.menu.Menu;
import com.google.common.collect.Maps;
import org.bukkit.entity.Player;

import java.util.Map;

public class MediaMenu extends Menu {

    {
        setAutoUpdate(false);
        setUpdateAfterClick(false);
    }

    @Override
    public String getTitle(Player player) {
        return "Media";
    }

    @Override
    public int getSize() {
        return 9 * 3;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        buttons.put(2, new MediaButton("OWNER_MEDIA", "&c&lOwner Media"));
        buttons.put(4, new MediaButton("PARTNER", "&d&lPartner"));
        buttons.put(6, new MediaButton("FAMOUS", "&9&lFamous"));
        buttons.put(21, new MediaButton("YOUTUBER", "&c&lYoutuber"));
        buttons.put(23, new MediaButton("MEDIA", "&6&lMedia"));

        return buttons;
    }
}
