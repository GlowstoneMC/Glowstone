package net.glowstone.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandUtils;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class WorldBorderCommand extends BukkitCommand {
    public WorldBorderCommand() {
        super("worldborder");
        setUsage("/worldborder <set|center|damage|warning|get|add> ...");
        setPermission("glowstone.command.worldborder");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) return true;
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }
        if (args[0].equalsIgnoreCase("center")) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /worldborder center <x> <z>");
                return false;
            }
            if (sender instanceof Player) {
                for (World world : Bukkit.getWorlds()) {
                    world.getWorldBorder().setCenter(CommandUtils.getLocation(((Player) sender).getLocation(), args[1], "0", args[2]));
                }
                return true;
            } else {
                double x, z;
                try {
                    x = Double.parseDouble(args[1]);
                    z = Double.parseDouble(args[2]);
                } catch (NumberFormatException ex) {
                    sender.sendMessage(ChatColor.RED + "Cannot set center: invalid number format.");
                    return false;
                }
                for (World world : Bukkit.getWorlds()) {
                    world.getWorldBorder().setCenter(x, z);
                }
                sender.sendMessage("Set world border center to (x=" + x + ", z=" + z + ").");
                return true;
            }
        } else if (args[0].equalsIgnoreCase("set")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /worldborder set <sizeInBlocks> [timeInSeconds]");
                return false;
            }
            double size;
            try {
                size = Double.parseDouble(args[1]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + "Cannot set size: invalid number format.");
                return false;
            }
            int time = 0;
            if (args.length > 2) {
                try {
                    time = Integer.parseInt(args[2]);
                } catch (NumberFormatException ex) {
                    sender.sendMessage(ChatColor.RED + "Cannot set size: invalid number format.");
                    return false;
                }
            }
            if (time < 0) {
                sender.sendMessage(ChatColor.RED + "Cannot set size: time must be positive.");
                return false;
            }
            for (World world : Bukkit.getWorlds()) {
                world.getWorldBorder().setSize(size, time);
            }
            if (time == 0) {
                sender.sendMessage("Set world border size to " + size + " blocks wide.");
            } else {
                sender.sendMessage("Set world border size to " + size + " blocks wide over " + time + " seconds.");
            }
        } else if (args[0].equalsIgnoreCase("get")) {
            if (sender instanceof Player) {
                sender.sendMessage("World border is " + ((Player) sender).getWorld().getWorldBorder().getSize() + " blocks wide.");
            } else {
                sender.sendMessage("World border is " + Bukkit.getWorlds().get(0).getWorldBorder().getSize() + " blocks wide.");
            }
        } else if (args[0].equalsIgnoreCase("add")) {
            // todo: "add" subcommand
            sender.sendMessage(ChatColor.RED + "This subcommand is not currently supported.");
            return false;
        } else if (args[0].equalsIgnoreCase("damage")) {
            // todo: "damage <buffer|amount>" subcommand
            sender.sendMessage(ChatColor.RED + "This subcommand is not currently supported.");
            return false;
        } else if (args[0].equalsIgnoreCase("warning")) {
            // todo: "warning <time|distance>" subcommand
            sender.sendMessage(ChatColor.RED + "This subcommand is not currently supported.");
            return false;
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }
        return false;
    }
}
