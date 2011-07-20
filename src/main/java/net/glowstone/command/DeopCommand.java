package net.glowstone.command;

import net.glowstone.GlowServer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

/**
 * A built-in command to remove a player's OP status.
 */
public class DeopCommand extends GlowCommand {

    public DeopCommand(GlowServer server) {
        super(server, "deop", "Removes a player's OP status", "<player>");
    }

    @Override
    public boolean run(CommandSender sender, String commandLabel, String[] args) {
        if (!checkArgs(sender, args, 1)) {
            return false;
        } else {
            server.getOpsList().remove(args[0]);
            Player target = server.getPlayer(args[0]);
            if (target != null) {
                target.sendMessage(ChatColor.GRAY + target.getName() + ": You are no longer op");
            }
            return tellOps(sender, "De-opping " + args[0]);
        }
    }

    @Override
    public PermissionDefault getPermissionDefault() {
        return PermissionDefault.OP;
    }
}
