package net.glowstone.command;

import net.glowstone.GlowServer;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

/**
 * A built-in command to stop the Glowstone server.
 */
public class StopCommand extends GlowCommand {

    public StopCommand(GlowServer server) {
        super(server, "stop", "Stops the server", "");
    }

    @Override
    public boolean run(CommandSender sender, String commandLabel, String[] args) {
        if (!checkArgs(sender, args, 0)) {
            return false;
        } else {
            server.shutdown();
            return tellOps(sender, "Stopping the server");
        }
    }

    @Override
    public PermissionDefault getPermissionDefault() {
        return PermissionDefault.OP;
    }
}
