package net.glowstone.command;

import org.bukkit.command.CommandSender;

import net.glowstone.GlowServer;

/**
 * A built-in command to stop the Glowstone server.
 * @author Tad
 */
public class StopCommand extends GlowCommand {
    
    public StopCommand(GlowServer server) {
        super(server, "stop", "Stops the server", "");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!checkArgs(sender, args, 0)) {
            return false;
        } else if (!checkOp(sender)) {
            return false;
        } else {
            server.stop();
            return tellOps(sender, "Stopping the server");
        }
    }
    
}
