package net.glowstone.command;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import net.glowstone.GlowServer;

/**
 * A built-in command to change the time on the server
 * @author Zhuowei
 */
public class TimeCommand extends GlowCommand {
    
    public TimeCommand(GlowServer server) {
        super(server, "time", "Changes the time of day on the server", "<add|set> <amount> [world]");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!(args.length == 2 || args.length == 3)) {
            sender.sendMessage(ChatColor.GRAY + "Wrong number of arguments. Usage: " + getUsage());
            return false;
        } else if (!checkOp(sender)) {
            return false;
        } else {
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
            }
            catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.GRAY + args[1] + " is not a number!");
                return false;
            }
            if (action.equals("add")){
                world.setTime((world.getTime() + amount) % 24000);
            } else if (action.equals("set")){
                world.setTime(amount);
            } else {
                sender.sendMessage(ChatColor.GRAY + action + " is not a valid action for the time command.");
                return false;
            }
            tellOps(sender, "Changing time of world " + world.getName());
            return true;
        }
    }
    
}