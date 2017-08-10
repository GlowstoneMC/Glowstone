package net.glowstone.command.minecraft;

import net.glowstone.command.CommandUtils;
import net.glowstone.util.lang.I;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Player;

public class WorldBorderCommand extends VanillaCommand {
    public WorldBorderCommand() {
        super("worldborder");
        setUsage("/worldborder <set|center|damage|warning|get|add> ...");
        setPermission("minecraft.command.worldborder");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) return true;
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.usage", usageMessage));
            return false;
        }
        if (args[0].equalsIgnoreCase("center")) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.usage", "/worldborder center <x> <z>"));
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
                    sender.sendMessage(ChatColor.RED + I.tr(sender, "command.minecraft.worldborder.invalid.center"));
                    return false;
                }
                for (World world : Bukkit.getWorlds()) {
                    world.getWorldBorder().setCenter(x, z);
                }
                sender.sendMessage(I.tr(sender, "command.minecraft.worldborder.set.center.1", x, z));
                return true;
            }
        } else if (args[0].equalsIgnoreCase("set")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.usage", I.tr(sender, "command.minecraft.worldborder.usage.set")));
                return false;
            }
            double size;
            try {
                size = Double.parseDouble(args[1]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + I.tr(sender, "command.minecraft.worldborder.invalid.size"));
                return false;
            }
            int time = 0;
            if (args.length > 2) {
                try {
                    time = Integer.parseInt(args[2]);
                } catch (NumberFormatException ex) {
                    sender.sendMessage(ChatColor.RED + I.tr(sender, "command.minecraft.worldborder.invalid.size"));
                    return false;
                }
            }
            if (time < 0) {
                sender.sendMessage(ChatColor.RED + I.tr(sender, "command.minecraft.worldborder.positive.size"));
                return false;
            }
            for (World world : Bukkit.getWorlds()) {
                world.getWorldBorder().setSize(size, time);
            }
            if (time == 0) {
                sender.sendMessage(I.tr(sender, "command.minecraft.worldborder.set.size.1", size));
            } else {
                sender.sendMessage(I.tr(sender, "command.minecraft.worldborder.set.size.2", size, time));
            }
        } else if (args[0].equalsIgnoreCase("get")) {
            if (sender instanceof Player) {
                sender.sendMessage(I.tr(sender, "command.minecraft.worldborder.wide", ((Player) sender).getWorld().getWorldBorder().getSize()));
            } else {
                sender.sendMessage(I.tr(sender, "command.minecraft.worldborder.wide", Bukkit.getWorlds().get(0).getWorldBorder().getSize()));
            }
        } else if (args[0].equalsIgnoreCase("add")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.usage", I.tr(sender, "command.minecraft.worldborder.usage.add")));
                return false;
            }
            double size;
            try {
                size = Double.parseDouble(args[1]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + I.tr(sender, "command.minecraft.worldborder.invalid.size"));
                return false;
            }
            int time = 0;
            if (args.length > 2) {
                try {
                    time = Integer.parseInt(args[2]);
                } catch (NumberFormatException ex) {
                    sender.sendMessage(ChatColor.RED + I.tr(sender, "command.minecraft.worldborder.invalid.size"));
                    return false;
                }
            }
            if (time < 0) {
                sender.sendMessage(ChatColor.RED + I.tr(sender, "command.minecraft.worldborder.positive.size"));
                return false;
            }
            for (World world : Bukkit.getWorlds()) {
                world.getWorldBorder().setSize(size + world.getWorldBorder().getSize(), time);
            }
            if (time == 0) {
                sender.sendMessage(I.tr(sender, "command.minecraft.worldborder." + (size >= 0 ? "increased" : "decreased"), size));
            } else {
                sender.sendMessage(I.tr(sender, "command.minecraft.worldborder." + (size >= 0 ? "increasing" : "decreasing"), Math.abs(size), time));
            }
        } else if (args[0].equalsIgnoreCase("damage")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.usage", "/worldborder damage <buffer|amount> ..."));
                return false;
            }
            if (args[1].equalsIgnoreCase("buffer")) {
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.usage", I.tr(sender, "command.minecraft.worldborder.usage.buffer")));
                    return false;
                }
                double buffer;
                try {
                    buffer = Double.parseDouble(args[2]);
                } catch (NumberFormatException ex) {
                    sender.sendMessage(ChatColor.RED + I.tr(sender, "command.minecraft.worldborder.invalid.buffer"));
                    return false;
                }
                if (buffer < 0) {
                    sender.sendMessage(ChatColor.RED + I.tr(sender, "command.minecraft.worldborder.positive.buffer"));
                    return false;
                }
                for (World world : Bukkit.getWorlds()) {
                    world.getWorldBorder().setDamageBuffer(buffer);
                }
                sender.sendMessage(I.tr(sender, "command.minecraft.worldborder.set.buffer", buffer));
                return false;
            } else if (args[1].equalsIgnoreCase("amount")) {
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.usage", I.tr(sender, "command.minecraft.worldborder.usage.amount")));
                    return false;
                }
                double damage;
                try {
                    damage = Double.parseDouble(args[2]);
                } catch (NumberFormatException ex) {
                    sender.sendMessage(ChatColor.RED + I.tr(sender, "command.minecraft.worldborder.invalid.amount"));
                    return false;
                }
                if (damage < 0) {
                    sender.sendMessage(ChatColor.RED + I.tr(sender, "command.minecraft.worldborder.positive.amount"));
                    return false;
                }
                for (World world : Bukkit.getWorlds()) {
                    world.getWorldBorder().setDamageAmount(damage);
                }
                sender.sendMessage(I.tr(sender, "command.minecraft.worldborder.set.amount", damage));
                return false;
            } else {
                sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.usage", "/worldborder damage <buffer|amount> ..."));
                return false;
            }
        } else if (args[0].equalsIgnoreCase("warning")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.usage", "/worldborder warning <time|distance> ..."));
                return false;
            }
            if (args[1].equalsIgnoreCase("time")) {
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.usage", I.tr(sender, "command.minecraft.worldborder.usage.time")));
                    return false;
                }
                int time;
                try {
                    time = Integer.parseInt(args[2]);
                } catch (NumberFormatException ex) {
                    sender.sendMessage(ChatColor.RED + I.tr(sender, "command.minecraft.worldborder.invalid.time"));
                    return false;
                }
                if (time < 0) {
                    sender.sendMessage(ChatColor.RED + I.tr(sender, "command.minecraft.worldborder.positive.time"));
                    return false;
                }
                for (World world : Bukkit.getWorlds()) {
                    world.getWorldBorder().setWarningTime(time);
                }
                sender.sendMessage(I.tr(sender, "command.minecraft.worldborder.set.time", time));
                return false;
            } else if (args[1].equalsIgnoreCase("distance")) {
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.usage", I.tr(sender, "command.minecraft.worldborder.usage.distance")));
                    return false;
                }
                int blocks;
                try {
                    blocks = Integer.parseInt(args[2]);
                } catch (NumberFormatException ex) {
                    sender.sendMessage(ChatColor.RED + I.tr(sender, "command.minecraft.worldborder.invalid.distance"));
                    return false;
                }
                if (blocks < 0) {
                    sender.sendMessage(ChatColor.RED + I.tr(sender, "command.minecraft.worldborder.positive.distance"));
                    return false;
                }
                for (World world : Bukkit.getWorlds()) {
                    world.getWorldBorder().setWarningDistance(blocks);
                }
                sender.sendMessage(I.tr(sender, "command.minecraft.worldborder.set.center.2", blocks));
                return false;
            } else {
                sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.usage", "/worldborder warning <time|distance> ..."));
                return false;
            }
        } else {
            sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.usage", usageMessage));
            return false;
        }
        return false;
    }
}
