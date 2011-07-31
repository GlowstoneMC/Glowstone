package net.glowstone.command;

import org.bukkit.command.CommandSender;

import net.glowstone.GlowServer;

/**
 * A built-in command to give a player OP status.
 */
public class OpCommand extends GlowCommand {
    
    public OpCommand(GlowServer server) {
        super(server, "op", "Gives a player OP status", "<player>");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!checkArgs(sender, args, 1)) {
            return false;
        } else if (!checkOp(sender)) {
            return false;
        } else {
            server.getOpsList().add(args[0]);
            return tellOps(sender, "Opping " + args[0]);
        }
    }
    
}
