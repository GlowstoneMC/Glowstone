package net.glowstone.util;

/**
 * Utility class to ease conversion from standard time units to in-game ticks.
 */
public final class TickUtil {

    private static final int TICKS_PER_SECOND = 20;

    private TickUtil() {
    }

    /**
     * Converts seconds (int) into ticks.
     * @param seconds The seconds to convert.
     * @return The corresponding number of ticks.
     */
    public static int secondsToTicks(int seconds) {
        return TICKS_PER_SECOND * seconds;
    }

    /**
     * Converts seconds (long) into ticks.
     * @param seconds The seconds to convert.
     * @return The corresponding number of ticks.
     */
    public static long secondsToTicks(long seconds) {
        return TICKS_PER_SECOND * seconds;
    }

    /**
     * Converts minutes (int) into ticks.
     * @param minutes The minutes to convert.
     * @return The corresponding number of ticks.
     */
    public static int minutesToTicks(int minutes) {
        return TICKS_PER_SECOND * 60 * minutes;
    }

    /**
     * Converts minutes (double) into ticks.
     * @param minutes The minutes to convert.
     * @return The corresponding number of ticks.
     */
    public static int minutesToTicks(double minutes) {
        return (int) (TICKS_PER_SECOND * 60 * minutes);
    }
}
