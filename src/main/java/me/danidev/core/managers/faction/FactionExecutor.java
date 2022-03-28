package me.danidev.core.managers.faction;

import me.danidev.core.Main;
import me.danidev.core.managers.faction.argument.*;
import me.danidev.core.utils.command.ArgumentExecutor;
import me.danidev.core.utils.command.CommandArgument;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.danidev.core.managers.faction.argument.staff.FactionClaimForArgument;
import me.danidev.core.managers.faction.argument.staff.FactionClearClaimsArgument;
import me.danidev.core.managers.faction.argument.staff.FactionForceJoinArgument;
import me.danidev.core.managers.faction.argument.staff.FactionForceKickArgument;
import me.danidev.core.managers.faction.argument.staff.FactionForceLeaderArgument;
import me.danidev.core.managers.faction.argument.staff.FactionForcePromoteArgument;
import me.danidev.core.managers.faction.argument.staff.FactionRemoveArgument;
import me.danidev.core.managers.faction.argument.staff.FactionRemovePointsArgument;
import me.danidev.core.managers.faction.argument.staff.FactionSetDeathbanMultiplierArgument;
import me.danidev.core.managers.faction.argument.staff.FactionSetDtrArgument;
import me.danidev.core.managers.faction.argument.staff.FactionSetDtrRegenArgument;
import me.danidev.core.managers.faction.argument.staff.FactionSetPointsArgument;

public class FactionExecutor extends ArgumentExecutor {
	
    private final CommandArgument helpArgument;
    private Main plugin;

    public FactionExecutor(Main plugin) {
        super("faction");
        plugin.getCommand("faction").setExecutor(this);

        this.addArgument(new FactionAcceptArgument(plugin));
        this.addArgument(new FactionAllyArgument(plugin));
        this.addArgument(new FactionChatArgument(plugin));
        this.addArgument(new FactionClaimArgument(plugin));
        this.addArgument(new FactionClaimChunkArgument(plugin));
        this.addArgument(new FactionTopArgument(plugin));
        this.addArgument(new FactionClaimForArgument(plugin));
        this.addArgument(new FactionClaimsArgument(plugin));
        this.addArgument(new FactionClearClaimsArgument(plugin));
        this.addArgument(new FactionCreateArgument(plugin));
        this.addArgument(new FactionCoLeaderArgument(plugin));
        this.addArgument(new FactionAnnouncementArgument(plugin));
        this.addArgument(new FactionDemoteArgument(plugin));
        this.addArgument(new FactionDepositArgument(plugin));
        this.addArgument(new FactionDisbandArgument(plugin));
        this.addArgument(new FactionSetDtrRegenArgument(plugin));
        this.addArgument(new FactionForceJoinArgument(plugin));
        this.addArgument(new FactionForceKickArgument(plugin));
        this.addArgument(new FactionForceLeaderArgument(plugin));
        this.addArgument(new FactionForcePromoteArgument(plugin));
        this.addArgument(helpArgument = new FactionHelpArgument(this));
        this.addArgument(new FactionHomeArgument(this, plugin));
        this.addArgument(new FactionInviteArgument(plugin));
        this.addArgument(new FactionInvitesArgument(plugin));
        this.addArgument(new FactionKickArgument(plugin));
        this.addArgument(new FactionLeaderArgument(plugin));
        this.addArgument(new FactionLeaveArgument(plugin));
        this.addArgument(new FactionListArgument(plugin));
        this.addArgument(new FactionMapArgument(plugin));
        this.addArgument(new FactionMessageArgument(plugin));
        this.addArgument(new FactionOpenArgument(plugin));
        this.addArgument(new FactionRemoveArgument(plugin));
        this.addArgument(new FactionRenameArgument(plugin));
        this.addArgument(new FactionPromoteArgument(plugin));
        this.addArgument(new FactionSetDtrArgument(plugin));
        this.addArgument(new FactionSetDeathbanMultiplierArgument(plugin));
        this.addArgument(new FactionSetHomeArgument(plugin));
        this.addArgument(new FactionShowArgument(plugin));
        this.addArgument(new FactionStuckArgument(plugin));
        this.addArgument(new FactionUnclaimArgument(plugin));
        this.addArgument(new FactionUnallyArgument(plugin));
        this.addArgument(new FactionUnInviteArgument(plugin));
        this.addArgument(new FactionWithdrawArgument(plugin));
        this.addArgument(new FactionSetPointsArgument());
        this.addArgument(new FactionRemovePointsArgument());
        this.addArgument(new FactionFriendlyFireArgument(plugin));
        this.addArgument(new FactionFocusArgument());
        this.addArgument(new FactionRallyArgument(plugin));
        this.addArgument(new FactionUnrallyArgument(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            helpArgument.onCommand(sender, command, label, args);
            return true;
        }

        CommandArgument argument = getArgument(args[0]);
        if (argument != null) {
            String permission = argument.getPermission();
            if (permission == null || sender.hasPermission(permission)) {
                argument.onCommand(sender, command, label, args);
                return true;
            }
        }

        helpArgument.onCommand(sender, command, label, args);
        return true;
    }
}
