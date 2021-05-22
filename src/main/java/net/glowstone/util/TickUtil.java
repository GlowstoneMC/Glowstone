package net.glowstone.util;

/**
 * Utility class to ease conversion from standard time units to in-game ticks.
 */
public final class TickUtil {
    /**
     * The length in ticks of one second.
     */
    public static final short TICKS_PER_SECOND = 20;
    /**
     * The length in ticks of one minute.
     */
    public static final short TICKS_PER_MINUTE = TICKS_PER_SECOND * 60;
    /**
     * The lenght in ticks of one hour.
     */
    public static final int TICKS_PER_HOUR = TICKS_PER_MINUTE * 60;
    /**
     * The length in ticks of one Minecraft day.
     */
    public static final int TICKS_PER_DAY = 24000;
    /**
     * The length in ticks of one Minecraft half day.
     */
    public static final int TICKS_PER_HALF_DAY = TICKS_PER_DAY / 2;
    /**
     * The length in ticks of one Minecraft week.
     */
    public static final int TICKS_PER_WEEK = 7 * TICKS_PER_DAY;

    private TickUtil() {
    }

    /**
     * Converts seconds (int) into ticks.
     *
     * @param seconds The seconds to convert.
     * @return The corresponding number of ticks.
     */
    public static int secondsToTicks(int seconds) {
        return TICKS_PER_SECOND * seconds;
    }

    /**
     * Converts seconds (long) into ticks.
     *
     * @param seconds The seconds to convert.
     * @return The corresponding number of ticks.
     */
    public static long secondsToTicks(long seconds) {
        return TICKS_PER_SECOND * seconds;
    }

    /**
     * Converts minutes (int) into ticks.
     *
     * @param minutes The minutes to convert.
     * @return The corresponding number of ticks.
     */
    public static int minutesToTicks(int minutes) {
        return TICKS_PER_MINUTE * minutes;
    }

    /**
     * Converts minutes (double) into ticks.
     *
     * @param minutes The minutes to convert.
     * @return The corresponding number of ticks.
     */
    public static int minutesToTicks(double minutes) {
        return (int) (TICKS_PER_MINUTE * minutes);
    }
}
