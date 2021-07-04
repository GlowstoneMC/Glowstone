package net.glowstone.util;

import com.eatthepath.uuid.FastUUID;

import java.util.UUID;

/**
 * Utility methods for dealing with UUIDs.
 */
public final class UuidUtils {

    private UuidUtils() {
    }

    /**
     * Parses a UUID from a hexadecimal string with hyphens.
     *
     * @param str a hexadecimal string
     * @return {@code str} as a UUID
     */
    public static UUID fromString(String str) {
        return FastUUID.parseUUID(str);
    }

    /**
     * Parses a UUID from a hexadecimal string without hyphens.
     *
     * @param str a 32-digit hexadecimal string
     * @return {@code str} as a UUID
     */
    public static UUID fromFlatString(String str) {
        return fromString(str.substring(0, 8)
                + "-" + str.substring(8, 12)
                + "-" + str.substring(12, 16)
                + "-" + str.substring(16, 20)
                + "-" + str.substring(20, 32));
    }

    /**
     * Converts a UUID to a hexadecimal string with hyphens.
     *
     * <p>This method uses the FastUUID library for faster serialization in Java 8.
     *
     * @param uuid a UUID
     * @return {@code uuid} as a hexadecimal string
     * @see FastUUID#toString(UUID)
     */
    public static String toString(UUID uuid) {
        return FastUUID.toString(uuid);
    }

    /**
     * Converts a UUID to a hexadecimal string without hyphens.
     *
     * <p>This method uses the FastUUID library for faster serialization in Java 8.
     *
     * @param uuid a UUID
     * @return {@code uuid} as a hexadecimal string without hyphens
     * @see FastUUID#toString(UUID)
     */
    public static String toFlatString(UUID uuid) {
        return toString(uuid).replace("-", "");
    }
}
