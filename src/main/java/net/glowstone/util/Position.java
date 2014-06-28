package net.glowstone.util;

import org.bukkit.Location;

/**
 * A static class housing position-related utilities and constants.
 * @author Graham Edgecombe
 */
public final class Position {

    private Position() {}

    /**
     * The number of integer values between each double value. For example, if
     * the coordinate was {@code 1.5}, this would be sent as
     * {@code 1.5 * 32 = 48} within certain packets.
     */
    public static final int GRANULARITY = 32;

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

}
