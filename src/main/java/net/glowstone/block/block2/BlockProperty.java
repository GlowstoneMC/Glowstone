package net.glowstone.block.block2;

import java.util.Collection;

/**
 * A property key that a block can associate a value with.
 */
public interface BlockProperty<T> {

    /**
     * Get the name of this property.
     * @return the name
     */
    String getName();

    /**
     * Get the default value of this property.
     * @return the default value
     */
    T getDefault();

    /**
     * Validate and filter a value for this property.
     * @param value the value to validate
     * @return the filtered value
     * @throws IllegalArgumentException if the value is invalid
     */
    T validate(T value);

    /**
     * A property composed of a continuous range of integers.
     */
    interface IntegerProperty extends BlockProperty<Integer> {

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

    }

    /**
     * A property composed of a set of possible strings.
     */
    interface StringProperty extends BlockProperty<String> {

        /**
         * Get the possible values this property can take.
         * @return a collection of possible values
         */
        Collection<String> getValues();

    }

}
