package net.glowstone.command;

import net.glowstone.GlowWorld;
import net.glowstone.block.state.BlockStateData;
import net.glowstone.block.state.InvalidBlockStateException;
import net.glowstone.block.state.StateSerialization;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class CommandUtils {

    private CommandUtils() {
    }

    /**
     * Gets the relative location based on the given axis values (x/y/z) based on tilda notation.
     * For instance, using axis values of ~10 ~ ~15 will return the location with the offset of the given rotation values.
     *
     * @param location  the initial location
     * @param xRelative the relative x-axis (if there is no tilda [~], then the literal value is used)
     * @param yRelative the relative y-axis (if there is no tilda [~], then the literal value is used)
     * @param zRelative the relative z-axis (if there is no tilda [~], then the literal value is used)
     * @return the relative location
     */
    public static Location getLocation(Location location, String xRelative, String yRelative, String zRelative) {
        double x, y, z;
        if (xRelative.startsWith("~")) {
            double diff = 0;
            if (xRelative.length() > 1)
                diff = getDouble(xRelative.substring(1), true);
            x = location.getX() + diff;
        } else {
            x = getDouble(xRelative, true);
        }
        if (yRelative.startsWith("~")) {
            double diff = 0;
            if (yRelative.length() > 1)
                diff = getDouble(yRelative.substring(1), false);
            y = location.getY() + diff;
        } else {
            y = getDouble(yRelative, false);
        }
        if (zRelative.startsWith("~")) {
            double diff = 0;
            if (zRelative.length() > 1)
                diff = getDouble(zRelative.substring(1), true);
            z = location.getZ() + diff;
        } else {
            z = getDouble(zRelative, true);
        }
        return new Location(location.getWorld(), x, y, z);
    }

    /**
     * Gets the relative location based on the given rotation values (yaw/relative) based on tilda notation.
     * For instance, using rotations of ~10 ~15 will return the location with the offset of the given rotation values.
     *
     * @param location      the initial location
     * @param yawRelative   the relative yaw (if there is no tilda [~], then the literal value is used)
     * @param pitchRelative the relative pitch (if there is no tilda [~], then the literal value is used)
     * @return the relative location
     */
    public static Location getRotation(Location location, String yawRelative, String pitchRelative) {
        float yaw, pitch;
        if (yawRelative.startsWith("~")) {
            float diff = 0;
            if (yawRelative.length() > 1)
                diff = Float.valueOf(yawRelative.substring(1));
            yaw = location.getYaw() + diff;
        } else {
            yaw = Float.valueOf(yawRelative);
        }
        if (pitchRelative.startsWith("~")) {
            float diff = 0;
            if (pitchRelative.length() > 1)
                diff = Float.valueOf(pitchRelative.substring(1));
            pitch = location.getPitch() + diff;
        } else {
            pitch = Float.valueOf(pitchRelative);
        }
        return new Location(location.getWorld(), location.getX(), location.getY(), location.getZ(), yaw, pitch);
    }

    private static double getDouble(String d, boolean shift) {
        boolean literal = d.split("\\.").length != 1;
        if (shift && !literal)
            d += ".5";
        return Double.valueOf(d);
    }

    public static BlockStateData readState(CommandSender sender, Material type, String state) {
        if (isNumeric(state)) {
            return new BlockStateData(Byte.parseByte(state));
        }
        try {
            return StateSerialization.parse(type, state);
        } catch (InvalidBlockStateException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            return null;
        }
    }

    public static boolean isNumeric(String argument) {
        return NumberUtils.isNumber(argument);
    }

    public static String prettyPrint(Entity[] entities) {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < entities.length; i++) {
            Entity entity = entities[i];
            String name = getName(entity);
            names.add(name);
        }
        return prettyPrint(names.toArray(new String[names.size()]));
    }

    public static String prettyPrint(String[] strings) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            String string = strings[i];
            if (i == strings.length - 1 && strings.length > 1) {
                builder.append(" and ");
            } else if (i > 0) {
                builder.append(", ");
            }
            builder.append(string);
        }
        return builder.toString();
    }

    // TODO: Move this into the Server class within Glowkit, and implement it with the GlowServer class.
    private static GlowWorld getDefaultWorld() {
        return (GlowWorld) Bukkit.getServer().getWorlds().get(0);
    }

    public static GlowWorld getWorld(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            return getDefaultWorld();
        } else if (sender instanceof Entity) {
            return (GlowWorld) ((Entity) sender).getWorld();
        } else if (sender instanceof BlockCommandSender) {
            return (GlowWorld) ((BlockCommandSender) sender).getBlock().getWorld();
        }
        return getDefaultWorld();
    }

    public static Location getLocation(CommandSender sender) {
        if (sender instanceof Entity) {
            return ((Entity) sender).getLocation();
        } else if (sender instanceof BlockCommandSender) {
            return ((BlockCommandSender) sender).getBlock().getLocation();
        }
        return new Location(getDefaultWorld(), 0, 0, 0);
    }

    public static String getName(CommandSender sender) {
        if (sender instanceof Entity) {
            return getName((Entity) sender);
        }
        return sender.getName();
    }

    public static String getName(Entity entity) {
        String name = entity.getName();
        if (name == null || name.isEmpty()) {
            name = entity.getType().getName();
        }
        if (entity.getCustomName() != null && !entity.getCustomName().isEmpty()) {
            name = entity.getCustomName();
        }
        return name;
    }

    public static boolean isPhysical(CommandSender sender) {
        return sender instanceof Entity || sender instanceof BlockCommandSender;
    }
}
