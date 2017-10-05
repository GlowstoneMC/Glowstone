package net.glowstone.block.function;

/**
 * A runnable function for item behavior.
 */
public interface ItemFunction {

    /**
     * The name of this function's type.
     * @return The functionality name
     */
    String getFunctionality();

    /**
     * If this ItemFunction type cannot run multiple functions.
     * @return If only a single function can be run of this type
     */
    default boolean isSingle() {
        return false;
    }
}
