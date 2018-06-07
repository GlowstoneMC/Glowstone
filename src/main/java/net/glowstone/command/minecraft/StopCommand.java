package net.glowstone.command.minecraft;

import java.util.Collections;
import net.glowstone.GlowServer;
import net.glowstone.ServerProvider;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;

/**
 * In vanilla Minecraft, this command takes no arguments; but as an extension, Glowstone treats any
 * arguments as a custom kick message.
 */
public class StopCommand extends VanillaCommand {

    public StopCommand() {
        super("stop", "Gracefully stops the server.", "/stop [message]", Collections.emptyList());
        setPermission("minecraft.command.stop");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return true;
        }
        Server server = ServerProvider.getServer();
        if (args.length > 0 && server instanceof GlowServer) {
            ((GlowServer) server).shutdown(String.join(" ", args));
        } else {
            server.shutdown();
        }
        return true;
    }
}
