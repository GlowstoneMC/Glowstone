package net.glowstone.block;

import org.bukkit.Material;

/**
 * MaterialValueManager provides easily access to {@link Material} related values (e.g. block hardness).
 */
public interface MaterialValueManager {
    /**
     * Gets the {@link Value} for the given material.
     * If there aren't concrete values for this material, a {@link Value} with default values will be returned.
     * @param material The material to look for
     * @return A {@link Value} object with values for the given material or default values
     */
    Value getValue(Material material);

    public interface Value {
        /**
         * Returns the hardness-component of this value.
         * @return returns the hardness (or Float.MAX_VALUE for infinity hardness)
         */
        float getHardness();

        float getBlastResistance();
    }
}
