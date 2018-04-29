package net.glowstone.util;

/**
 * Utility class to ease conversion between minutes/seconds and ticks.
 */
public final class TickUtil {

    private TickUtil() {
    }

    /**
     * Converts seconds (int) into ticks.
     * @param seconds The seconds to convert.
     * @return The corresponding number of ticks.
     */
    public static int secondsToTicks(int seconds) {
        return 20 * seconds;
    }

    /**
     * Converts seconds (long) into ticks.
     * @param seconds The seconds to convert.
     * @return The corresponding number of ticks.
     */
    public static long secondsToTicks(long seconds) {
        return 20 * seconds;
    }

    /**
     * Converts minutes (int) into ticks.
     * @param minutes The minutes to convert.
     * @return The corresponding number of ticks.
     */
    public static int minutesToTicks(int minutes) {
        return 20 * 60 * minutes;
    }

    /**
     * Converts minutes (double) into ticks.
     * @param minutes The minutes to convert.
     * @return The corresponding number of ticks.
     */
    public static int minutesToTicks(double minutes) {
        return (int) (20 * 60 * minutes);
    }
}
