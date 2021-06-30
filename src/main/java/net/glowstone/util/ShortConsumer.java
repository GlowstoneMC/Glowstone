package net.glowstone.util;

/**
 * Non-boxing version of {@code {@link java.util.function.Consumer}<Short>}.
 */
@FunctionalInterface
public interface ShortConsumer {
    /**
     * Applies this function.
     *
     * @param arg the argument to accept
     */
    void accept(short arg);
}
