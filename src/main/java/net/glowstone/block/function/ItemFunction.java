package net.glowstone.block.function;

/**
 * A runnable function for item behavior.
 */
public interface ItemFunction {
    /**
     * If this ItemFunction type cannot run multiple functions
     * @return If only a single function can be run of this type
     */
    default boolean isSingle() {
        return false;
    }
}
