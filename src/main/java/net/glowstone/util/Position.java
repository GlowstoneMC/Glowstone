package net.glowstone.util;

import com.google.common.collect.ImmutableList;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import java.util.List;

import static org.bukkit.block.BlockFace.*;

/**
 * A static class housing position-related utilities and constants.
 * @author Graham Edgecombe
 */
public final class Position {

    private Position() {
    }

    /**
     * The number of integer values between each double value. For example, if
     * the coordinate was {@code 1.5}, this would be sent as
     * {@code 1.5 * 32 = 48} within certain packets.
     */
    public static final int GRANULARITY = 32;

    /**
     * Common Rotation values used blocks such as Signs, Skulls, and Banners.
     * The order relates to the data/tag that is applied to the block on placing.
     */
    public static final List<BlockFace> ROTATIONS = ImmutableList.of(NORTH, NORTH_NORTH_EAST, NORTH_EAST,
            EAST_NORTH_EAST, EAST, EAST_SOUTH_EAST, SOUTH_EAST, SOUTH_SOUTH_EAST, SOUTH, SOUTH_SOUTH_WEST,
            SOUTH_WEST, WEST_SOUTH_WEST, WEST, WEST_NORTH_WEST, NORTH_WEST, NORTH_NORTH_WEST);

    /**
     * Gets the X coordinate multiplied the granularity and rounded to an
     * integer.
     * @return An integer approximation of the X coordinate.
     */
    public static int getIntX(Location loc) {
        return (int) (loc.getX() * GRANULARITY);
    }

    /**
     * Gets the Y coordinate multiplied the granularity and rounded to an
     * integer.
     * @return An integer approximation of the Y coordinate.
     */
    public static int getIntY(Location loc) {
        return (int) (loc.getY() * GRANULARITY);
    }

    /**
     * Gets the Z coordinate multiplied the granularity and rounded to an
     * integer.
     * @return An integer approximation of the Z coordinate.
     */
    public static int getIntZ(Location loc) {
        return (int) (loc.getZ() * GRANULARITY);
    }

    /**
     * Gets an integer approximation of the yaw between 0 and 255.
     * @return An integer approximation of the yaw.
     */
    public static int getIntYaw(Location loc) {
        return (int) (((loc.getYaw() % 360) / 360) * 256);
    }

    /**
     * Gets an integer approximation of the pitch between 0 and 255.
     * @return An integer approximation of the yaw.
     */
    public static int getIntPitch(Location loc) {
        return (int) (((loc.getPitch() % 360) / 360) * 256);
    }

    /**
     * Gets whether there has been a position change between the two Locations.
     * @return A boolean.
     */
    public static boolean hasMoved(Location first, Location second) {
        return first.getX() != second.getX() || first.getY() != second.getY() || first.getZ() != second.getZ();
    }

    /**
     * Gets whether there has been a rotation change between the two Locations.
     * @return A boolean.
     */
    public static boolean hasRotated(Location first, Location second) {
        return first.getPitch() != second.getPitch() || first.getYaw() != second.getYaw();
    }

    /**
     * Copy the contents of one Location to another.
     * @param source The Location to read from.
     * @param dest The Location to modify. May be null.
     * @return The dest parameter, modified if not null.
     */
    public static Location copyLocation(Location source, Location dest) {
        if (dest == null) {
            return null;
        }
        dest.setWorld(source.getWorld());
        dest.setX(source.getX());
        dest.setY(source.getY());
        dest.setZ(source.getZ());
        dest.setPitch(source.getPitch());
        dest.setYaw(source.getYaw());
        return dest;
    }

    /**
     * Get an intercardinal BlockFace from a rotation value, where NORTH is 0.
     * @param rotation byte value rotation to get
     * @return intercardinal BlockFace
     * @throws IndexOutOfBoundsException If 0 > value > 15
     */
    public static BlockFace getDirection(byte rotation) {
        return ROTATIONS.get(rotation);
    }

    /**
     * Gets the byte rotation for an intercardinal BlockFace, where NORTH is 0.
     * @param rotation Rotation to get
     * @return byte data value for the given rotation, or -1 if rotation is SELF or null
     */
    public static byte getDirection(BlockFace rotation) {
        return (byte) ROTATIONS.indexOf(rotation);
    }

}
