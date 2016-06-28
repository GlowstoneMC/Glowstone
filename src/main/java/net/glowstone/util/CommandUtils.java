package net.glowstone.util;

import org.bukkit.Location;

public class CommandUtils {

    private CommandUtils() {
    }

    /**
     * Gets the relative location based on the given axis values (x/y/z) based on tilda notation.
     * For instance, using axis values of ~10 ~ ~15 will return the location with the offset of the given rotation values.
     *
     * @param location the initial location
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
                diff = getDouble(xRelative.substring(1));
            x = location.getX() + diff;
        } else {
            x = getDouble(xRelative);
        }
        if (yRelative.startsWith("~")) {
            double diff = 0;
            if (yRelative.length() > 1)
                diff = getDouble(yRelative.substring(1));
            y = location.getY() + diff;
        } else {
            y = getDouble(yRelative);
        }
        if (zRelative.startsWith("~")) {
            double diff = 0;
            if (zRelative.length() > 1)
                diff = getDouble(zRelative.substring(1));
            z = location.getZ() + diff;
        } else {
            z = getDouble(zRelative);
        }
        return new Location(location.getWorld(), x, y, z);
    }

    /**
     * Gets the relative location based on the given rotation values (yaw/relative) based on tilda notation.
     * For instance, using rotations of ~10 ~15 will return the location with the offset of the given rotation values.
     *
     * @param location the initial location
     * @param yawRelative the relative yaw (if there is no tilda [~], then the literal value is used)
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

    private static double getDouble(String d) {
        boolean literal = d.split("\\.").length != 1;
        if (!literal)
            d += ".5";
        return Double.valueOf(d);
    }
}
