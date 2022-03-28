package me.danidev.core.managers.faction.argument;

import me.danidev.core.utils.BukkitUtils;
import me.danidev.core.utils.JavaUtils;
import me.danidev.core.utils.chat.ClickAction;
import me.danidev.core.utils.chat.Text;
import me.danidev.core.utils.command.CommandArgument;
import com.google.common.collect.ArrayListMultimap;
import me.danidev.core.managers.faction.FactionExecutor;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import com.google.common.collect.ImmutableMultimap;

public class FactionHelpArgument extends CommandArgument {
	
    private final FactionExecutor executor;
    private ImmutableMultimap<Integer, Text> pages;
    
    public FactionHelpArgument(final FactionExecutor executor) {
        super("help", "View help on how to use factions.");
        this.executor = executor;
    }
    
    public String getUsage(final String label) {
        return "/" + label + ' ' + this.getName();
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 2) {
            this.showPage(sender, label, 1);
            return true;
        }

        final Integer page = JavaUtils.tryParseInt(args[1]);

        if (page == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
            return true;
        }
        this.showPage(sender, label, page);
        return true;
    }
    
    private void showPage(final CommandSender sender, final String label, final int pageNumber) {
        if (this.pages == null) {
            final boolean isPlayer = sender instanceof Player;
            int val = 1;
            int count = 0;
            final ArrayListMultimap<Integer, Text> pages = ArrayListMultimap.create();
            for (final CommandArgument argument : this.executor.getArguments()) {
                final String permission;
                if (!argument.equals(this) && ((permission = argument.getPermission()) == null || sender.hasPermission(permission))) {
                    if (!isPlayer) {
                        continue;
                    }
                    pages.get(val).add(new Text(ChatColor.AQUA + " /" + label + ' ' + argument.getName() + ChatColor.GRAY + " - " + ChatColor.WHITE + argument.getDescription()).setColor(ChatColor.WHITE).setClick(ClickAction.SUGGEST_COMMAND, "/" + label + " " + argument.getName()));
                    if (++count % 100 != 0) {
                        continue;
                    }
                    ++val;
                }
            }
            this.pages = ImmutableMultimap.copyOf(pages);
        }
        final int totalPageCount = this.pages.size() / 100 + 1;
        if (pageNumber < 1) {
            sender.sendMessage(ChatColor.RED + "You cannot view a page less than 1.");
            return;
        }
        if (pageNumber > totalPageCount) {
            sender.sendMessage(ChatColor.RED + "There are only " + totalPageCount + " pages.");
            return;
        }
        sender.sendMessage(String.valueOf(ChatColor.GRAY.toString()) + ChatColor.STRIKETHROUGH + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(String.valueOf(ChatColor.DARK_AQUA) + ChatColor.BOLD + " Faction Help ");
        sender.sendMessage(" ");
        for (final Text message : this.pages.get(pageNumber)) {
            message.send(sender);
        }
        sender.sendMessage(" ");
        sender.sendMessage(String.valueOf(ChatColor.GRAY.toString()) + ChatColor.STRIKETHROUGH + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    }
}
