package net.glowstone.block.block2;

import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.property.BooleanProperty;
import org.spongepowered.api.data.property.IntProperty;

import java.util.*;

/**
 * Implementation of {@link BlockTrait}.
 */
public abstract class GlowBlockTrait<T extends Comparable<T>> implements BlockTrait<T> {

    private final String name;
    private final List<T> values;

    public GlowBlockTrait(String name, Collection<T> values) {
        this.name = name;
        ArrayList<T> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        this.values = Collections.unmodifiableList(sorted);
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final String toString() {
        return getClass().getSimpleName() + "{name=" + name + ", values=" + values + "}";
    }

    /**
     * Get the default value of this property.
     * @return the default value
     */
    protected T getDefault() {
        return values.get(0);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Static helper methods

    public static <T extends Comparable<T>> T cycle(BlockTrait<T> property, Object value) {
        Iterator<T> iter = property.getPossibleValues().iterator();
        if (!iter.hasNext()) {
            throw new IllegalStateException("no valid values on: " + property);
        }

        T first = iter.next();
        if (first.equals(value)) {
            if (iter.hasNext()) {
                return iter.next();
            } else {
                return first;
            }
        }

        while (iter.hasNext()) {
            if (iter.next().equals(value)) {
                if (iter.hasNext()) {
                    return iter.next();
                } else {
                    // loop to the beginning
                    return first;
                }
            }
        }
        throw new IllegalArgumentException("cannot cycle from invalid value: " + value);
    }

    public static <T extends Comparable<T>> T getDefault(BlockTrait<T> property) {
        if (property instanceof GlowBlockTrait) {
            return ((GlowBlockTrait<T>) property).getDefault();
        }
        return property.getPossibleValues().iterator().next();
    }

    /**
     * Validate and filter a value for this property.
     * @param value the value to validate
     * @return the filtered value
     * @throws IllegalArgumentException if the value is invalid
     */
    public static Comparable<?> validate(BlockTrait<?> property, Comparable<?> value) {
        if (property.getPossibleValues().contains(value)) {
            return value;
        } else {
            throw new IllegalArgumentException("Invalid value for " + property + ": " + value);
        }
    }

    /**
     * Create a new boolean property with the given name.
     * @param value The value of the property
     * @return A new boolean property
     */
    public static BooleanProperty ofBoolean(boolean value) {
        return new BooleanProperty(value);
    }

    /**
     * Create a new integer property with the given attributes.
     * @param value The value
     * @return A new integer property
     */
    public static IntProperty ofRange(int value) {
        return new IntProperty(value);
    }

    /**
     * Reusable checks for the validity of a values array.
     */
    private static <E extends Enum<E>> Class<E> checkClass(E[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("null or empty values provided");
        }
        Class<E> clazz = values[0].getDeclaringClass();
        if (clazz.getEnumConstants() == null) {
            throw new IllegalArgumentException(clazz + " is not an enumeration");
        }
        for (E val : values) {
            if (val.getDeclaringClass() != clazz) {
                throw new IllegalArgumentException("value " + val + " is not member of " + clazz.getName());
            }
        }
        return clazz;
    }
}
