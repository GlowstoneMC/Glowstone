package net.glowstone.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.glowstone.GlowServer;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

/**
 * Common base class for inbuilt Glowstone commands.
 */
public abstract class GlowCommand extends Command {
    
    protected final GlowServer server;
    public static final String PERM_PREFIX = "glowstone.command";
    
    public GlowCommand(GlowServer server, String name, String desc, String usage) {
        super(name, desc, "/" + name + " " + usage, new ArrayList<String>());
        this.server = server;
        setPermission("glowstone.command." + name);
    }
    
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) return false;
        run(sender, commandLabel, args);
        return true;
    }
    
    protected boolean checkArgs(CommandSender sender, String[] args, int expected) {
        if (args.length != expected) {
            sender.sendMessage(ChatColor.GRAY + "Wrong number of arguments. Usage: " + getUsage());
            return false;
        }
        return true;
    }

    protected boolean checkArgs(CommandSender sender, String[] args, int min, int max) {
        if (args.length < min || args.length > max) {
            sender.sendMessage(ChatColor.GRAY + "Wrong number of arguments. Usage: " + getUsage());
            return false;
        }
        return true;
    }
    
    protected boolean checkOp(CommandSender sender) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.GRAY + "You do not have privileges to use this command.");
            return false;
        }
        return true;
    }
    
    protected boolean tellOps(CommandSender sender, String message) {
        server.broadcast("(" + sender.getName() + ": " + message + ")", Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
        return true;
    }

    public boolean checkPermission(CommandSender sender, String permission) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.RED + "I'm sorry Dave but I cannot let you do that.");
            return false;
        }
        return true;
    }

    public abstract boolean run(CommandSender sender, String commandLabel, String[] args);

    public Set<Permission> registerPermissions(String prefix) {
        return new HashSet<Permission>();
    }

    public abstract PermissionDefault getPermissionDefault();
    
}
