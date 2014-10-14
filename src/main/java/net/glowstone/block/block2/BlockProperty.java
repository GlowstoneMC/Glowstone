package net.glowstone.block.block2;

import java.util.Collection;

/**
 * A property key that a block can associate a value with.
 */
public interface BlockProperty {

    /**
     * Get the name of this property.
     * @return the name
     */
    String getName();

    /**
     * Get the type of this property.
     * @return the type
     */
    Type getType();

    /**
     * Get the default value of this property.
     * @return the default value
     */
    Object getDefault();

    /**
     * Validate and filter a value for this property.
     * @param value the value to validate
     * @return the filtered value
     * @throws IllegalArgumentException if the value is invalid
     */
    Object validate(Object value);

    /**
     * A property composed of a continuous range of integers.
     */
    interface IntegerProperty extends BlockProperty {

        /**
         * Get the minimum value of this property.
         * @return the minimum
         */
        int getMinimum();

        /**
         * Get the maximum value of this property.
         * @return the maximum
         */
        int getMaximum();

        @Override
        Integer getDefault();

    }

    /**
     * A property composed of a set of possible strings.
     */
    interface StringProperty extends BlockProperty {

        /**
         * Get the possible values this property can take.
         * @return a collection of possible values
         */
        Collection<String> getValues();

        @Override
        String getDefault();

    }

    enum Type {
        BOOLEAN,
        INTEGER,
        STRING
    }
}
