package net.glowstone.command;

import org.bukkit.command.CommandSender;

import net.glowstone.GlowServer;
import org.bukkit.ChatColor;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.HashSet;
import java.util.Set;

/**
 * A built-in command to manipulate the whitelist functionality.
 */
public class WhitelistCommand extends GlowCommand {
    
    public WhitelistCommand(GlowServer server) {
        super(server, "whitelist", "Manipulates the whitelist", "on|off|add <player>|remove <player>");
    }

    @Override
    public boolean run(CommandSender sender, String commandLabel, String[] args) {
        if (args.length < 1 || args.length > 2) {
            sender.sendMessage(ChatColor.GRAY + "Wrong number of arguments. Usage: " + getUsage());
            return false;
        } else {
            String command = args[0].trim();
            if (command.equalsIgnoreCase("on")) {
                if (!checkPermission(sender, "enable")) return false;
                // Enable whitelist
                if (!checkArgs(sender, args, 1)) return false;
                server.setWhitelist(true);
                return tellOps(sender, "Enabling whitelist");
            } else if (command.equalsIgnoreCase("off")) {
                if (!checkPermission(sender, "disable")) return false;
                // Disable whitelist
                if (!checkArgs(sender, args, 1)) return false;
                server.setWhitelist(false);
                return tellOps(sender, "Disabling whitelist");
            } else if (command.equalsIgnoreCase("")) {
                if (!checkPermission(sender, "add")) return false;
                // Add player to list
                if (!checkArgs(sender, args, 2)) return false;
                server.getWhitelist().add(args[1]);
                return tellOps(sender, "Adding " + args[1] + " to whitelist");
            } else if (command.equalsIgnoreCase("remove")) {
                if (!checkPermission(sender, "remove")) return false;
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

    @Override
    public Set<Permission> registerPermissions(String prefix) {
        Set<Permission> perms = new HashSet<Permission>();
        perms.add(new Permission(prefix + ".add", "Allows users to add to the whitelist"));
        perms.add(new Permission(prefix + ".remove", "Allows users to remove from the whitelist"));
        perms.add(new Permission(prefix + ".enable", "Allows users to enable the whitelist"));
        perms.add(new Permission(prefix + ".disable", "Allows users to disable the whitelist"));
        return perms;
    }

    @Override
    public PermissionDefault getPermissionDefault() {
        return PermissionDefault.OP;
    }
}
