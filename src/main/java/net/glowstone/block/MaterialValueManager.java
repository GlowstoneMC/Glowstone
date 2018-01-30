package net.glowstone.block;

import org.bukkit.Material;

/**
 * MaterialValueManager provides easily access to {@link Material} related values (e.g. block
 * hardness).
 */
public interface MaterialValueManager {

    /**
     * Returns the {@link ValueCollection} for the given material. If there aren't concrete values
     * for this material, a {@link ValueCollection} with default values will be returned.
     *
     * @param material The material to look for
     * @return a {@link ValueCollection} object with values for the given material or default values
     */
    ValueCollection getValues(Material material);

    interface ValueCollection {

        /**
         * Returns the hardness-component of this value.
         *
         * @return the hardness (or Float.MAX_VALUE for infinity hardness)
         */
        float getHardness();

        /**
         * Returns the blast resistance-component of this value.
         *
         * @return the blast resistance
         */
        float getBlastResistance();

        /**
         * Returns the light opacity-component of this value.
         *
         * @return the light opacity
         */
        int getLightOpacity();

        /**
         * Returns the flame resistance-component of this value.
         *
         * @return the flame resistance
         */
        int getFlameResistance();

        /**
         * Returns the fire resistance-component of this value.
         *
         * @return the fire resistance
         */
        int getFireResistance();

        /**
         * Returns the slipperiness-component of this value.
         *
         * @return the slipperiness
         */
        double getSlipperiness();

        /**
         * Returns the base map color for this value. Map pixels with this as the highest block can
         * be this value plus 0 to 3.
         *
         * @return the base map color for this material
         */
        byte getBaseMapColor();
    }
}
