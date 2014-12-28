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
        return UUID.fromString(str.substring(0, 8) + "-" + str.substring(8, 12) + "-" + str.substring(12, 16) +
                "-" + str.substring(16, 20) + "-" + str.substring(20, 32));
    }

    public static String toFlatString(UUID uuid) {
        return uuid.toString().replace("-", "");
    }

}
