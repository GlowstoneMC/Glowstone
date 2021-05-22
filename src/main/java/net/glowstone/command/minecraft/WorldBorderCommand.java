package net.glowstone.command.minecraft;

import net.glowstone.command.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldBorderCommand extends GlowVanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public WorldBorderCommand() {
        super("worldborder");
        setPermission("minecraft.command.worldborder"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args,
                           CommandMessages commandMessages) {
        if (!testPermission(sender, commandMessages.getPermissionMessage())) {
            return true;
        }
        if (args.length == 0) {
            sendUsageMessage(sender, commandMessages);
            return false;
        }
        if (args[0].equalsIgnoreCase("center")) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /worldborder center <x> <z>");
                return false;
            }
            if (sender instanceof Player) {
                for (World world : Bukkit.getWorlds()) {
                    world.getWorldBorder().setCenter(CommandUtils
                        .getLocation(((Player) sender).getLocation(), args[1], "0", args[2]));
                }
                return true;
            } else {
                double x;
                double z;
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
                sender.sendMessage(
                    ChatColor.RED + "Usage: /worldborder set <sizeInBlocks> [timeInSeconds]");
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
                sender.sendMessage(
                    "Set world border size to " + size + " blocks wide over " + time + " seconds.");
            }
        } else if (args[0].equalsIgnoreCase("get")) {
            if (sender instanceof Player) {
                sender.sendMessage(
                    "World border is " + ((Player) sender).getWorld().getWorldBorder().getSize()
                        + " blocks wide.");
            } else {
                sender.sendMessage(
                    "World border is " + Bukkit.getWorlds().get(0).getWorldBorder().getSize()
                        + " blocks wide.");
            }
        } else if (args[0].equalsIgnoreCase("add")) {
            if (args.length < 2) {
                sender.sendMessage(
                    ChatColor.RED + "Usage: /worldborder add <sizeInBlocks> [timeInSeconds]");
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
                world.getWorldBorder().setSize(size + world.getWorldBorder().getSize(), time);
            }
            String action = size >= 0 ? "Increas" : "Decreas";
            if (time == 0) {
                sender.sendMessage(action + "ed world border size by " + size + " blocks wide.");
            } else {
                sender.sendMessage(
                    action + "ing world border size by " + Math.abs(size) + " blocks wide over "
                        + time + " seconds.");
            }
        } else if (args[0].equalsIgnoreCase("damage")) {
            if (args.length < 2) {
                sender
                    .sendMessage(ChatColor.RED + "Usage: /worldborder damage <buffer|amount> ...");
                return false;
            }
            if (args[1].equalsIgnoreCase("buffer")) {
                if (args.length < 3) {
                    sender.sendMessage(
                        ChatColor.RED + "Usage: /worldborder damage buffer <sizeInBlocks>");
                    return false;
                }
                double buffer;
                try {
                    buffer = Double.parseDouble(args[2]);
                } catch (NumberFormatException ex) {
                    sender.sendMessage(
                        ChatColor.RED + "Cannot set damage buffer: invalid number format.");
                    return false;
                }
                if (buffer < 0) {
                    sender.sendMessage(ChatColor.RED
                        + "Cannot set damage buffer: damage buffer must be positive.");
                    return false;
                }
                for (World world : Bukkit.getWorlds()) {
                    world.getWorldBorder().setDamageBuffer(buffer);
                }
                sender.sendMessage("Set border's damage buffer to " + buffer + " blocks.");
                return false;
            } else if (args[1].equalsIgnoreCase("amount")) {
                if (args.length < 3) {
                    sender.sendMessage(
                        ChatColor.RED + "Usage: /worldborder damage amount <damagePerBlock>");
                    return false;
                }
                double damage;
                try {
                    damage = Double.parseDouble(args[2]);
                } catch (NumberFormatException ex) {
                    sender.sendMessage(
                        ChatColor.RED + "Cannot set damage amount: invalid number format.");
                    return false;
                }
                if (damage < 0) {
                    sender.sendMessage(ChatColor.RED
                        + "Cannot set damage amount: damage amount must be positive.");
                    return false;
                }
                for (World world : Bukkit.getWorlds()) {
                    world.getWorldBorder().setDamageAmount(damage);
                }
                sender.sendMessage("Set border's damage amount to " + damage + " damage.");
                return false;
            } else {
                sender
                    .sendMessage(ChatColor.RED + "Usage: /worldborder damage <buffer|amount> ...");
                return false;
            }
        } else if (args[0].equalsIgnoreCase("warning")) {
            if (args.length < 2) {
                sender
                    .sendMessage(ChatColor.RED + "Usage: /worldborder warning <time|distance> ...");
                return false;
            }
            if (args[1].equalsIgnoreCase("time")) {
                if (args.length < 3) {
                    sender.sendMessage(
                        ChatColor.RED + "Usage: /worldborder warning time <timeInSeconds>");
                    return false;
                }
                int time;
                try {
                    time = Integer.parseInt(args[2]);
                } catch (NumberFormatException ex) {
                    sender.sendMessage(
                        ChatColor.RED + "Cannot set warning time: invalid number format.");
                    return false;
                }
                if (time < 0) {
                    sender.sendMessage(
                        ChatColor.RED + "Cannot set warning time: time must be positive.");
                    return false;
                }
                for (World world : Bukkit.getWorlds()) {
                    world.getWorldBorder().setWarningTime(time);
                }
                sender.sendMessage("Set border's warning time to " + time + " seconds.");
                return false;
            } else if (args[1].equalsIgnoreCase("distance")) {
                if (args.length < 3) {
                    sender.sendMessage(
                        ChatColor.RED + "Usage: /worldborder warning distance <sizeInBlocks>");
                    return false;
                }
                int blocks;
                try {
                    blocks = Integer.parseInt(args[2]);
                } catch (NumberFormatException ex) {
                    sender.sendMessage(
                        ChatColor.RED + "Cannot set warning distance: invalid number format.");
                    return false;
                }
                if (blocks < 0) {
                    sender.sendMessage(
                        ChatColor.RED + "Cannot set warning distance: distance must be positive.");
                    return false;
                }
                for (World world : Bukkit.getWorlds()) {
                    world.getWorldBorder().setWarningDistance(blocks);
                }
                sender.sendMessage("Set border's warning distance to " + blocks + " blocks.");
                return false;
            } else {
                sender
                    .sendMessage(ChatColor.RED + "Usage: /worldborder warning <time|distance> ...");
                return false;
            }
        } else {
            sendUsageMessage(sender, commandMessages);
            return false;
        }
        return false;
    }
}
