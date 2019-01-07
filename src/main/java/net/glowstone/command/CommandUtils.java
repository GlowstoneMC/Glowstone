package net.glowstone.command;

import net.glowstone.GlowWorld;
import net.glowstone.ServerProvider;
import net.glowstone.block.state.BlockStateData;
import net.glowstone.block.state.InvalidBlockStateException;
import net.glowstone.block.state.StateSerialization;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;

public class CommandUtils {

    private CommandUtils() {
    }

    private static double getDouble(String d, boolean shift) {
        boolean literal = d.split("\\.").length != 1;
        if (shift && !literal) {
            d += ".5";
        }
        return Double.valueOf(d);
    }

    /**
     * Parses a block state from a string.
     *
     * @param sender the target who should receive an error message if {@code state} is
     *         invalid
     * @param type the block type
     * @param state a string specifying a block state
     * @return the block state for {@code type} and {@code state}, or null if none match
     */
    public static BlockStateData readState(CommandSender sender, Material type, String state) {
        if (NumberUtils.isNumber(state)) {
            return new BlockStateData(Byte.parseByte(state));
        }
        try {
            return StateSerialization.parse(type, state);
        } catch (InvalidBlockStateException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            return null;
        }
    }

    /**
     * Converts an array of entities to a readable string.
     *
     * @param entities one or more entities
     * @return a list of the entities' names, formatted like "Alice, Bob and Creeper"
     * @deprecated Use one of the {@code joinList} overloads in
     * {@link net.glowstone.command.minecraft.GlowVanillaCommand}'s inner class CommandMessages.
     */
    @Deprecated
    public static String prettyPrint(Entity[] entities) {
        String[] names = new String[entities.length];
        for (int i = 0; i < entities.length; i++) {
            names[i] = entities[i].getName();
        }
        return prettyPrint(names);
    }

    /**
     * Converts an array of strings describing list items to a single string listing them.
     *
     * @param strings one or more strings
     * @return a list of the strings, formatted like "a, b and c"
     * @deprecated Use one of the {@code joinList} overloads in
     * {@link net.glowstone.command.minecraft.GlowVanillaCommand}'s inner class CommandMessages.
     */
    @Deprecated
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

    // TODO: Move this into the Server class within Glowkit, and implement it with GlowServer.
    private static GlowWorld getDefaultWorld() {
        return (GlowWorld) ServerProvider.getServer().getWorlds().get(0);
    }

    /**
     * Returns the world that the given command sender is referring to when not specifying one.
     *
     * @param sender a command sender
     * @return the command sender's world if the sender is a block or entity, or the default world
     *         otherwise
     */
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

    /**
     * Gets the location that is "~ ~ ~" for a command sender.
     *
     * @param sender a command sender
     * @return the sender's location if the sender is a block or entity, or the default world's
     *         coordinate origin otherwise.
     */
    public static Location getLocation(CommandSender sender) {
        if (sender instanceof Entity) {
            return ((Entity) sender).getLocation();
        } else if (sender instanceof BlockCommandSender) {
            return ((BlockCommandSender) sender).getBlock().getLocation();
        }
        return new Location(getDefaultWorld(), 0, 0, 0);
    }

    /**
     * Parses coordinates that may be absolute or relative.
     *
     * @param sender the command sender
     * @param x the x coordinate specifier
     * @param y the y coordinate specifier
     * @param z the z coordinate specifier
     * @return the coordinates
     */
    public static Location getLocation(CommandSender sender, String x, String y, String z) {
        Location currentLocation;
        if (x.startsWith("~") || y.startsWith("~") || z
                .startsWith("~")) { // The coordinates are relative
            currentLocation = getLocation(sender);
        } else { // Otherwise, the current location can be set to 0/0/0 (since it's absolute)
            currentLocation = new Location(getWorld(sender), 0, 0, 0);
        }
        return getLocation(currentLocation, x, y, z);
    }

    /**
     * <p>Gets the relative location based on the given axis values (x/y/z) based on tilde
     * notation.</p>
     *
     * <p>For instance, using axis values of ~10 ~ ~15 will return the location with the offset of
     * the given rotation values.
     *
     * @param location the initial location
     * @param relativeX the relative x-axis (if there is no tilde [~], then the literal
     *         value is used)
     * @param relativeY the relative y-axis (if there is no tilde [~], then the literal
     *         value is used)
     * @param relativeZ the relative z-axis (if there is no tilde [~], then the literal
     *         value is used)
     * @return the relative location
     */
    public static Location getLocation(Location location, String relativeX, String relativeY,
            String relativeZ) {
        double x;
        double y;
        double z;
        if (relativeX.startsWith("~")) {
            double diff = 0;
            if (relativeX.length() > 1) {
                diff = getDouble(relativeX.substring(1), true);
            }
            x = location.getX() + diff;
        } else {
            x = getDouble(relativeX, true);
        }
        if (relativeY.startsWith("~")) {
            double diff = 0;
            if (relativeY.length() > 1) {
                diff = getDouble(relativeY.substring(1), false);
            }
            y = location.getY() + diff;
        } else {
            y = getDouble(relativeY, false);
        }
        if (relativeZ.startsWith("~")) {
            double diff = 0;
            if (relativeZ.length() > 1) {
                diff = getDouble(relativeZ.substring(1), true);
            }
            z = location.getZ() + diff;
        } else {
            z = getDouble(relativeZ, true);
        }
        return new Location(location.getWorld(), x, y, z);
    }

    /**
     * <p>Gets the relative location based on the given rotation values (yaw/relative) based on
     * tilde notation.</p>
     *
     * <p>For instance, using rotations of ~10 ~15 will return the location with the offset of the
     * given rotation values.</p>
     *
     * @param location the initial location
     * @param yawRelative the relative yaw (if there is no tilde [~], then the literal value
     *         is used)
     * @param pitchRelative the relative pitch (if there is no tilde [~], then the literal
     *         value is used)
     * @return the relative location
     */
    public static Location getRotation(Location location, String yawRelative,
            String pitchRelative) {
        float yaw;
        if (yawRelative.startsWith("~")) {
            float diff = 0;
            if (yawRelative.length() > 1) {
                diff = Float.valueOf(yawRelative.substring(1));
            }
            yaw = location.getYaw() + diff;
        } else {
            yaw = Float.valueOf(yawRelative);
        }
        float pitch;
        if (pitchRelative.startsWith("~")) {
            float diff = 0;
            if (pitchRelative.length() > 1) {
                diff = Float.valueOf(pitchRelative.substring(1));
            }
            pitch = location.getPitch() + diff;
        } else {
            pitch = Float.valueOf(pitchRelative);
        }
        return new Location(location.getWorld(), location.getX(), location.getY(), location.getZ(),
                yaw, pitch);
    }

    /**
     * Gets the name of a command sender.
     *
     * @param sender the sender
     * @return the sender's name
     */
    public static String getName(CommandSender sender) {
        if (sender instanceof Entity) {
            return getName((Entity) sender);
        }
        return sender.getName();
    }

    /**
     * Gets the name of an entity.
     *
     * @param entity an entity
     * @return the first of the following that exists and is non-empty: {@code
     *         entity.getCustomName()}, {@code entity.getName()}, {@code
     *         entity.getType().getName()}
     */
    public static String getName(Entity entity) {
        String customName = entity.getCustomName();
        if (customName != null && !customName.isEmpty()) {
            return customName;
        }
        String name = entity.getName();
        if (name == null || name.isEmpty()) {
            name = entity.getType().getName();
        }
        return name;
    }

    public static boolean isPhysical(CommandSender sender) {
        return sender instanceof Entity || sender instanceof BlockCommandSender;
    }

    /**
     * Returns the input unchanged if it already has a namespace prefix; otherwise, adds the
     * {@link org.bukkit.NamespacedKey#MINECRAFT} prefix.
     * @param input a namespaced-key name, or prefix of one, that may or may not be namespaced
     * @return the input, namespaced
     */
    public static String toNamespaced(String input) {
        return input.indexOf(':') >= 0 ? input : NamespacedKey.MINECRAFT + ':' + input;
    }
}
