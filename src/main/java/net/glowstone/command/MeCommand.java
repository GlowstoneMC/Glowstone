package net.glowstone.command;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.glowstone.GlowServer;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

/**
 * A built-in command to display the message in form of * USER message
 */
public class MeCommand extends GlowCommand {
    
    public MeCommand(GlowServer server) {
        super(server, "me", "Displays message in form of * USER message", "<message>");
    }

    @Override
    public boolean run(CommandSender sender, String commandLabel, String[] args) {
        if (args.length < 1) {
            return false;
        }
        
        String message = "";
        for (int i = 0; i < args.length; ++i) {
            message += " " + args[i];
        }
        String senderName = sender instanceof Player ? ((Player) sender).getDisplayName() : "Console";
        server.broadcastMessage(" * " + senderName + message);
        return true;
    }
    
    @Override
    public PermissionDefault getPermissionDefault() {
        return PermissionDefault.TRUE;
    }
}
