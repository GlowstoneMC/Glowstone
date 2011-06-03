package net.glowstone.command;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.glowstone.GlowServer;
import org.bukkit.entity.Player;

/**
 * Common base class for inbuilt Glowstone commands.
 * @author Tad
 */
public abstract class GlowCommand extends Command {
    
    protected final GlowServer server;
    
    public GlowCommand(GlowServer server, String name, String desc, String usage) {
        super(name, desc, "/" + name + " " + usage, new ArrayList<String>());
        this.server = server;
    }
    
    protected boolean checkArgs(CommandSender sender, String[] args, int expected) {
        if (args.length != expected) {
            sender.sendMessage(ChatColor.GRAY + "Wrong number of args: expected " + expected + ", got " + args.length);
            return false;
        }
        return true;
    }
    
    protected boolean checkOp(CommandSender sender) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.GRAY + "You do not have priveleges to use this command.");
            return false;
        }
        return true;
    }
    
    protected boolean tellOps(CommandSender sender, String message) {
        if (sender instanceof Player) {
            message = "(" + ((Player) sender).getName() + ": " + message + ")";
        } else {
            message = "(" + sender.getClass().getName() + ": " + message + ")";
        }
        
        for (Player p : server.getOnlinePlayers()) {
            if (p.isOp()) {
                p.sendMessage(ChatColor.GRAY + message);
            }
        }
        
        server.getLogger().info(message);
        
        return true;
    }
    
}
