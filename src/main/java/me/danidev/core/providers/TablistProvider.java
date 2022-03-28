package me.danidev.core.providers;

import me.danidev.core.Main;
import me.danidev.core.managers.faction.FactionManager;
import me.danidev.core.managers.faction.FactionMember;
import me.danidev.core.managers.faction.type.PlayerFaction;
import me.danidev.core.managers.games.koth.EventTimer;
import me.danidev.core.managers.games.koth.faction.EventFaction;
import me.danidev.core.managers.games.koth.faction.KothFaction;
import me.danidev.core.managers.user.FactionUser;
import me.danidev.core.utils.CC;
import me.danidev.core.utils.JavaUtils;
import me.danidev.core.utils.file.FileConfig;
import me.danidev.core.utils.tablist.TablistInterfaze;
import net.minecraft.util.com.google.common.collect.HashBasedTable;
import net.minecraft.util.com.google.common.collect.Lists;
import net.minecraft.util.com.google.common.collect.Table;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class TablistProvider implements TablistInterfaze {

    private final FileConfig mainConfig = Main.get().getMainConfig();

    private final Comparator<PlayerFaction> FACTION_COMPARATOR = (Comparator.comparingInt(playerFaction -> playerFaction.getOnlinePlayers().size()));
    private final Comparator<FactionMember> ROLE_COMPARATOR = (Comparator.comparingInt(playerMember -> playerMember.getRole().ordinal()));

    @Override
    public String getHeader(Player player) {
        return null;
    }

    @Override
    public String getFooter(Player player) {
        return null;
    }

    @Override
    public Table<Integer, Integer, String> getEntries(Player player) {
        Table<Integer, Integer, String> entries = HashBasedTable.create();
        FactionManager factionManager = Main.get().getFactionManager();
        PlayerFaction playerFaction = Main.get().getFactionManager().getPlayerFaction(player.getUniqueId());
        FactionUser factionUser = Main.get().getUserManager().getUser(player.getUniqueId());
        String home = (playerFaction == null || playerFaction.getHome() == null)
                ? "None" : playerFaction.getHome().getBlockX() + ", " + playerFaction.getHome().getBlockZ();

        int onlinePlayers = Bukkit.getServer().getOnlinePlayers().size();
        int maxPlayers = Bukkit.getServer().getMaxPlayers();

        EventTimer eventTimer = Main.get().getTimerManager().eventTimer;
        EventFaction eventFaction = eventTimer.getEventFaction();
        KothFaction kothFaction = (KothFaction) eventFaction;

        String koth = (eventFaction == null) ? "&7None" : " &7► &7" + eventFaction.getName();
        String kothcoords = (eventFaction == null) ? "" : (" &7► &7%x% &7, &7%z%").replace("%x%",String.valueOf(kothFaction.getCaptureZone().getCuboid().getX1()))
                .replace("%z%", String.valueOf(kothFaction.getCaptureZone().getCuboid().getZ1()));

        // Left
        entries.put(0, 0, CC.translate("&6Faction Home"));
        entries.put(0, 1, CC.translate("&7" + home));

        entries.put(0, 3, CC.translate("&6Faction Info"));

        if (playerFaction != null) {
            String online = String.valueOf(playerFaction.getOnlineMembers().size());
            String DTR = playerFaction.getDtrColourString()
                    + JavaUtils.format(playerFaction.getDeathsUntilRaidable(false))
                    + playerFaction.getRegenStatus().getSymbol();
            String balance = String.valueOf(playerFaction.getBalance());

            entries.put(0, 4, CC.translate("&7Online&7: &f" + online));
            entries.put(0, 5, CC.translate("&7DTR&7: " + DTR));
            entries.put(0, 6, CC.translate("&7Balance&7: &9$" + balance));
        }
        else {
            entries.put(0, 4, CC.translate("&7You do not"));
            entries.put(0, 5, CC.translate("&7have a faction"));
            entries.put(0, 6, CC.translate("&7/f create <name>"));
        }

        entries.put(0, 8, CC.translate("&6Player Info"));
        entries.put(0, 9, CC.translate("&7Kills&7: &f" + factionUser.getKills()));
        entries.put(0, 10, CC.translate("&7Deaths&7: &f" + factionUser.getDeaths()));

        entries.put(0, 12, CC.translate("&6Location"));
        entries.put(0, 13, CC.translate(factionManager.getFactionAt(player.getLocation()).getDisplayName(player)));
        entries.put(0, 14, CC.translate("&7" + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockZ()
                + " &f[" + getCardinalDirection(player) + "]"));

        // Middle
        entries.put(1, 0, CC.translate(mainConfig.getString("SERVER-NAME")));
        entries.put(1, 2, CC.translate(playerFaction == null ? "" : "&2" + playerFaction.getName()));

        if (playerFaction != null) {
            List<FactionMember> members = playerFaction.getMembers().values().stream()
                    .filter(member -> Bukkit.getPlayer(member.getUniqueId()) != null)
                    .sorted(ROLE_COMPARATOR)
                    .collect(Collectors.toList());

            for (int i = 3; i < 20; i++) {
                int exact = i - 3;

                if (members.size() > exact) {
                    if (i == 18 && members.size() > 18) {
                        entries.put(1, i, CC.translate(" &6►  &band " + (members.size() - 19) + " more..."))
                        ;
                    }
                    else {
                        FactionMember targetMember = members.get(exact);
                        entries.put(1, i, CC.translate(" &6► &a" + targetMember.getRole().getAstrix() + targetMember.getName()));
                    }
                }
            }
        }
        else {
            for (int i = 3; i < 20; i++) {
                entries.put(1, i, CC.translate(""))
                ;
            }
        }

        // Right
        entries.put(2, 0, CC.translate("&6End Portal"));
        entries.put(2, 1,  CC.translate(mainConfig.getString("END-PORTAL")));
        entries.put(2, 2,  CC.translate("&7in each quadrant"));

        entries.put(2, 4,  CC.translate("&6Map Kit"));
        entries.put(2, 5,  CC.translate(mainConfig.getString("MAP-KIT")));

        entries.put(2, 7,  CC.translate("&6Map Border"));
        entries.put(2, 8,  CC.translate(mainConfig.getString("MAP-BORDER")));

        entries.put(2, 10,  CC.translate("&6Online"));
        entries.put(2, 11,  CC.translate("&7" + onlinePlayers + "/" + maxPlayers));

        entries.put(2, 13,  CC.translate("&6KOTH"));
        entries.put(2, 14,  CC.translate(koth));
        entries.put(2, 15,  CC.translate(kothcoords));

        // Far Right
        entries.put(3, 0, CC.translate("&6Team List"));
        List<PlayerFaction> playerTeams = factionManager.getFactions().stream()
                .filter(x -> x instanceof PlayerFaction)
                .map(x -> (PlayerFaction) x)
                .filter(x -> x.getOnlineMembers().size() > 0)
                .distinct()
                .sorted(FACTION_COMPARATOR)
                .collect(Collectors.toList());

        Collections.reverse(playerTeams);

        for (int i = 0; i < 12; i ++) {
            if (i >= playerTeams.size()) break;

            PlayerFaction next = playerTeams.get(i);
            String factionDTR = next.getDtrColourString()
                    + JavaUtils.format(next.getDeathsUntilRaidable(false))
                    + next.getRegenStatus().getSymbol();

            String name = next.getName();

            entries.put(3, i + 1, CC.translate("&7" + (name.length() > 10 ? name.substring(0, 10) : name)
                    + " (" + next.getOnlinePlayers().size() + ") " + "&7┃ " + factionDTR))
            ;
        }
        return entries;
    }



    private static String getCardinalDirection(Player player) {
        double rotation = (player.getLocation().getYaw() - 90) % 360;

        if (rotation < 0) {
            rotation += 360.0;
        }

        if (0 <= rotation && rotation < 22.5) {
            return "W";
        } else if (22.5 <= rotation && rotation < 67.5) {
            return "NW";
        } else if (67.5 <= rotation && rotation < 112.5) {
            return "N";
        } else if (112.5 <= rotation && rotation < 157.5) {
            return "NE";
        } else if (157.5 <= rotation && rotation < 202.5) {
            return "E";
        } else if (202.5 <= rotation && rotation < 247.5) {
            return "SE";
        } else if (247.5 <= rotation && rotation < 292.5) {
            return "S";
        } else if (292.5 <= rotation && rotation < 337.5) {
            return "SW";
        } else if (337.5 <= rotation && rotation < 360.0) {
            return "W";
        } else {
            return null;
        }
    }
}