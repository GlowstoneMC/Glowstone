package net.glowstone.util;

/**
 * Identity methods used to prevent inlining of constant.
 */
public class NoInline {
    public static boolean of(boolean value) {
        return value;
    }

    public static byte of(byte value) {
        return value;
    }

    public static short of(short value) {
        return value;
    }

    public static char of(char value) {
        return value;
    }

    public static int of(int value) {
        return value;
    }

    public static long of(long value) {
        return value;
    }

    public static float of(float value) {
        return value;
    }

    public static double of(double value) {
        return value;
    }

    public static <T> T of(T value) {
        return value;
    }
}
