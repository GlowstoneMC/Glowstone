package net.glowstone.constants;

import org.apache.commons.lang.Validate;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;

/**
 * Mappings for resource pack status codes.
 */
public final class ResourcePackStatus {

    private ResourcePackStatus() {
    }

    private static final int[] codes = new int[Status.values().length];
    private static final Status[] statuses = new Status[Status.values().length];

    /**
     * Get the status ID for a specified Status.
     * @param status the Status.
     * @return the status code
     */
    public static int getId(Status status) {
        Validate.notNull(status, "Status cannot be null");
        return codes[status.ordinal()];
    }

    /**
     * Get the status for a specified id.
     * @param code the code.
     * @return the Status, or null
     */
    public static Status getStatus(int code) {
        return statuses[code];
    }

    private static void set(int code, Status status) {
        codes[status.ordinal()] = code;
        statuses[code] = status;
    }

    static {
        set(0, Status.SUCCESS);
        set(1, Status.DECLINED);
        set(2, Status.FAILED_DOWNLOAD);
        set(3, Status.ACCEPTED);
    }
}
