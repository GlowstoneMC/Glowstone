package net.glowstone.constants;

import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;

/**
 * Mappings for resource pack status codes.
 */
public final class ResourcePackStatus {

    private static final Status[] statuses = new Status[Status.values().length];

    static {
        set(0, Status.SUCCESSFULLY_LOADED);
        set(1, Status.DECLINED);
        set(2, Status.FAILED_DOWNLOAD);
        set(3, Status.ACCEPTED);
    }

    private ResourcePackStatus() {
    }

    /**
     * Get the status for a specified id.
     *
     * @param code the code.
     * @return the Status, or null
     */
    public static Status getStatus(int code) {
        return statuses[code];
    }

    private static void set(int code, Status status) {
        statuses[code] = status;
    }
}
