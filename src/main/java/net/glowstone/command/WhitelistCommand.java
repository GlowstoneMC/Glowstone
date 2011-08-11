package net.glowstone.command;

import org.bukkit.command.CommandSender;

import net.glowstone.GlowServer;
import org.bukkit.ChatColor;

/**
 * A built-in command to manipulate the whitelist functionality.
 */
public class WhitelistCommand extends GlowCommand {
    
    public WhitelistCommand(GlowServer server) {
        super(server, "whitelist", "Manipulates the whitelist", "on|off|add <player>|remove <player>");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length < 1 || args.length > 2) {
            sender.sendMessage(ChatColor.GRAY + "Wrong number of arguments. Usage: " + getUsage());
            return false;
        } else if (!checkOp(sender)) {
            return false;
        } else {
            String command = args[0].trim();
            if (command.equalsIgnoreCase("on")) {
                // Enable whitelist
                if (!checkArgs(sender, args, 1)) return false;
                server.setWhitelist(true);
                return tellOps(sender, "Enabling whitelist");
            } else if (command.equalsIgnoreCase("off")) {
                // Disable whitelist
                if (!checkArgs(sender, args, 1)) return false;
                server.setWhitelist(false);
                return tellOps(sender, "Disabling whitelist");
            } else if (command.equalsIgnoreCase("")) {
                // Add player to list
                if (!checkArgs(sender, args, 2)) return false;
                server.getWhitelist().add(args[1]);
                return tellOps(sender, "Adding " + args[1] + " to whitelist");
            } else if (command.equalsIgnoreCase("remove")) {
                // Remove player from list
                if (!checkArgs(sender, args, 2)) return false;
                server.getWhitelist().add(args[1]);
                return tellOps(sender, "Removing " + args[1] + " from whitelist");
            } else {
                sender.sendMessage(ChatColor.GRAY + "Action must be one of on, off, add, or remove");
                return false;
            }
        }
    }
    
}
