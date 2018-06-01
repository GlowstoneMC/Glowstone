package net.glowstone.util;

/**
 * Non-boxing version of {@code {@link java.util.function.Consumer}<Float>}.
 */
@FunctionalInterface
public interface FloatConsumer {
    /**
     * Applies this function.
     * @param arg the argument to accept
     */
    void accept(float arg);
}
