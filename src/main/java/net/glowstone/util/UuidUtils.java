package net.glowstone.util;

import java.util.UUID;

/**
 * Utility methods for dealing with UUIDs.
 */
public final class UuidUtils {

    private UuidUtils() {
    }

    public static UUID fromFlatString(String str) {
        // xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
        return new UUID(Long.parseUnsignedLong(str.substring(0, 16), 16), Long.parseUnsignedLong(str.substring(16), 16));
    }

    public static String toFlatString(UUID uuid) {
        return uuid.toString().replace("-", "");
    }
}
