package net.glowstone.command.minecraft;

import net.glowstone.GlowWorld;
import net.glowstone.command.CommandTarget;
import net.glowstone.data.CommandFunction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Entity;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FunctionCommand extends VanillaCommand {

    public FunctionCommand() {
        super("function", "Execute a function", "/function <name> [if <selector>|unless <selector>]", Collections.emptyList());
        setPermission("minecraft.command.function");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        if (args.length == 0 || args.length == 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }
        GlowWorld world = null;
        Location location = null;
        if (sender instanceof ConsoleCommandSender) {
            world = (GlowWorld) Bukkit.getServer().getWorlds().get(0); // get the default world
        } else if (sender instanceof Entity) {
            world = (GlowWorld) ((Entity) sender).getWorld();
            location = ((Entity) sender).getLocation();
        } else if (sender instanceof BlockCommandSender) {
            world = (GlowWorld) ((BlockCommandSender) sender).getBlock().getWorld();
            location = ((BlockCommandSender) sender).getBlock().getLocation();
        }
        if (world == null) {
            return false;
        }
        String functionName = args[0];
        CommandFunction function = null;
        for (CommandFunction f : world.getFunctions()) {
            if (f.getFullName().equals(functionName)) {
                function = f;
                break;
            }
        }
        if (function == null) {
            sender.sendMessage(ChatColor.RED + "Unknown function '" + functionName + "'");
            return false;
        }
        if (args.length > 2 && location != null) {
            String condition = args[1].toLowerCase();
            CommandTarget target = new CommandTarget(sender, args[2]);
            Entity[] matched = target.getMatched(location);
            if (condition.equals("if")) {
                if (matched.length == 0) {
                    sender.sendMessage("Skipped execution of function '" + function.getFullName() + "'");
                    return false;
                }
            } else if (condition.equals("unless")) {
                if (matched.length > 0) {
                    sender.sendMessage("Skipped execution of function '" + function.getFullName() + "'");
                    return false;
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
                return false;
            }
        }
        function.execute(sender);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            GlowWorld world = null;
            if (sender instanceof ConsoleCommandSender) {
                world = (GlowWorld) Bukkit.getServer().getWorlds().get(0); // get the default world
            } else if (sender instanceof Entity) {
                world = (GlowWorld) ((Entity) sender).getWorld();
            } else if (sender instanceof BlockCommandSender) {
                world = (GlowWorld) ((BlockCommandSender) sender).getBlock().getWorld();
            }
            if (world != null) {
                return (List) StringUtil.copyPartialMatches(args[0], world.getFunctionNames(), new ArrayList(world.getFunctionNames().size()));
            }
        }
        return super.tabComplete(sender, alias, args);
    }
}
