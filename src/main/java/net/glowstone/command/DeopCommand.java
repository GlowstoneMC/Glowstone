package net.glowstone.command;

import org.bukkit.command.CommandSender;

import net.glowstone.GlowServer;

/**
 * A built-in command to remove a player's OP status.
 * @author Tad
 */
public class DeopCommand extends GlowCommand {
    
    public DeopCommand(GlowServer server) {
        super(server, "deop", "Removes a player's OP status", "<player>");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!checkArgs(sender, args, 1)) {
            return false;
        } else if (!checkOp(sender)) {
            return false;
        } else {
            server.getOpsList().remove(args[0]);
            return tellOps(sender, "De-opping " + args[0]);
        }
    }
    
}
