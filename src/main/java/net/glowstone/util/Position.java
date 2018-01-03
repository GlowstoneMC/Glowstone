package net.glowstone.util;

import static org.bukkit.block.BlockFace.EAST;
import static org.bukkit.block.BlockFace.EAST_NORTH_EAST;
import static org.bukkit.block.BlockFace.EAST_SOUTH_EAST;
import static org.bukkit.block.BlockFace.NORTH;
import static org.bukkit.block.BlockFace.NORTH_EAST;
import static org.bukkit.block.BlockFace.NORTH_NORTH_EAST;
import static org.bukkit.block.BlockFace.NORTH_NORTH_WEST;
import static org.bukkit.block.BlockFace.NORTH_WEST;
import static org.bukkit.block.BlockFace.SOUTH;
import static org.bukkit.block.BlockFace.SOUTH_EAST;
import static org.bukkit.block.BlockFace.SOUTH_SOUTH_EAST;
import static org.bukkit.block.BlockFace.SOUTH_SOUTH_WEST;
import static org.bukkit.block.BlockFace.SOUTH_WEST;
import static org.bukkit.block.BlockFace.WEST;
import static org.bukkit.block.BlockFace.WEST_NORTH_WEST;
import static org.bukkit.block.BlockFace.WEST_SOUTH_WEST;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BlockVector;

/**
 * A static class housing position-related utilities and constants.
 *
 * @author Graham Edgecombe
 */
public final class Position {

    /**
     * Common Rotation values used blocks such as Signs, Skulls, and Banners. The order relates to
     * the data/tag that is applied to the block on placing.
     */
    public static final List<BlockFace> ROTATIONS = ImmutableList
            .of(NORTH, NORTH_NORTH_EAST, NORTH_EAST, EAST_NORTH_EAST, EAST, EAST_SOUTH_EAST,
                    SOUTH_EAST, SOUTH_SOUTH_EAST, SOUTH, SOUTH_SOUTH_WEST, SOUTH_WEST,
                    WEST_SOUTH_WEST, WEST, WEST_NORTH_WEST, NORTH_WEST, NORTH_NORTH_WEST);

    private Position() {
    }

    /**
     * Gets an integer approximation of the yaw between 0 and 255.
     *
     * @param loc The location to get the value from.
     * @return An integer approximation of the yaw.
     */
    public static int getIntYaw(Location loc) {
        return (int) (loc.getYaw() % 360 / 360 * 256);
    }

    /**
     * Gets an integer approximation of the pitch between 0 and 255.
     *
     * @param loc The location to get the value from.
     * @return An integer approximation of the yaw.
     */
    public static int getIntPitch(Location loc) {
        return (int) (loc.getPitch() % 360 / 360 * 256);
    }

    /**
     * Gets an integer approximation of the head-yaw rotation between 0 and 255.
     *
     * @param headYaw the head-yaw rotation value.
     * @return An integer approximation of the head-yaw rotation value.
     */
    public static int getIntHeadYaw(float headYaw) {
        return (int) (headYaw % 360 / 360 * 256);
    }

    /**
     * Gets whether there has been a position change between the two Locations.
     *
     * @param first The initial location.
     * @param second The final location.
     * @return A boolean.
     */
    public static boolean hasMoved(Location first, Location second) {
        return first.getX() != second.getX() || first.getY() != second.getY()
                || first.getZ() != second.getZ();
    }

    /**
     * Gets whether there has been a rotation change between the two Locations.
     *
     * @param first The initial location.
     * @param second The final location.
     * @return A boolean.
     */
    public static boolean hasRotated(Location first, Location second) {
        return first.getPitch() != second.getPitch() || first.getYaw() != second.getYaw();
    }

    /**
     * Copy the contents of one Location to another.
     *
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
     * Copy the position contents (x,y,z) of one Location to another.
     *
     * @param source The Location to read the x, y and z values from.
     * @param dest The Location to modify the x, y and z values. May be null.
     * @return The dest parameter, modified if not null.
     */
    public static Location copyPosition(Location source, Location dest) {
        if (dest == null) {
            return null;
        }
        dest.setX(source.getX());
        dest.setY(source.getY());
        dest.setZ(source.getZ());
        return dest;
    }

    /**
     * Get an intercardinal BlockFace from a rotation value, where NORTH is 0.
     *
     * @param rotation byte value rotation to get
     * @return intercardinal BlockFace
     * @throws IndexOutOfBoundsException If the value is less than 0 or greater than 15
     */
    public static BlockFace getDirection(byte rotation) {
        return ROTATIONS.get(rotation);
    }

    /**
     * Gets the byte rotation for an intercardinal BlockFace, where NORTH is 0.
     *
     * @param rotation Rotation to get
     * @return byte data value for the given rotation, or -1 if rotation is SELF or null
     */
    public static byte getDirection(BlockFace rotation) {
        return (byte) ROTATIONS.indexOf(rotation);
    }

    /**
     * Gets the serialized position value for a block vector.
     *
     * @param vector the block vector to serialize
     * @return the serialized position value
     */
    public static long getPosition(BlockVector vector) {
        return (((long) vector.getBlockX() & 0x3FFFFFF) << 38) | (
                ((long) vector.getBlockY() & 0xFFF) << 26) | ((long) vector.getBlockZ()
                & 0x3FFFFFF);
    }

    /**
     * Decodes the block vector from a serialized position value.
     *
     * @param position the position to decode
     * @return the decoded block vector
     */
    public static BlockVector getPosition(long position) {
        return new BlockVector(position >> 38, (position >> 26) & 0xFFF, position << 38 >> 38);
    }
}
