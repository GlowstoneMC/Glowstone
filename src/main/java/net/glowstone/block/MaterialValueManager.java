package net.glowstone.block;

import org.bukkit.Material;

/**
 * BlockValueManager provides easily access to Block/Material related values (e.g. block hardness).
 */
public interface MaterialValueManager {
    /**
     * Gets the {@link Value} for the given material.
     * If there aren't concrete values for this material, a {@link Value} with default values will be returned.
     * @param material The material to look for
     * @return A {@link Value} object with values for the given material or default values
     */
    Value getValue(Material material);

    @lombok.Value
    public static class Value {
        private final float hardness;
        private final float blastResistance;
    }
}
