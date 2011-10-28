package net.glowstone.command;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import net.glowstone.GlowServer;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.HashSet;
import java.util.Set;

/**
 * A built-in command to change the time on the server
 */
public class TimeCommand extends GlowCommand {
    
    public TimeCommand(GlowServer server) {
        super(server, "time", "Changes the time of day on the server", "<add|set> <amount> [world]");
    }

    @Override
    public boolean run(CommandSender sender, String commandLabel, String[] args) {
        if (!checkArgs(sender, args, 2, 3)) return false;
        World world;
        if (args.length == 3) {
            world = server.getWorld(args[2]);
            if (world == null) {
                sender.sendMessage(ChatColor.GRAY + "World " + args[2] + " does not exist.");
                return false;
            }
        } else if (sender instanceof Player) {
            world = ((Player) sender).getWorld();
        } else {
            world = server.getWorlds().get(0);
        }
        String action = args[0];
        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex) {
            sender.sendMessage(ChatColor.GRAY + args[1] + " is not a number!");
            return false;
        }
        if (action.equals("add")) {
            if (!checkPermission(sender, "add")) return false;
            world.setTime((world.getTime() + amount) % 24000);
        } else if (action.equals("set")) {
            if (!checkPermission(sender, "set")) return false;
            world.setTime(amount);
        } else {
            sender.sendMessage(ChatColor.GRAY + action + " is not a valid action for the time command.");
            return false;
        }
        tellOps(sender, "Changing time of world " + world.getName());
        return true;
    }

    @Override
    public Set<Permission> registerPermissions(String prefix) {
        Set<Permission> perms = new HashSet<Permission>();
        perms.add(new Permission(prefix + ".add", "Allows users to add to the current time"));
        perms.add(new Permission(prefix + ".set", "Allows users to set current time"));
        return perms;
    }

    @Override
    public PermissionDefault getPermissionDefault() {
        return PermissionDefault.OP;
    }
}