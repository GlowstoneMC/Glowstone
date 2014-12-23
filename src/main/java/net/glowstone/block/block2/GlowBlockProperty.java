package net.glowstone.block.block2;

import com.google.common.base.Optional;
import net.glowstone.block.block2.sponge.BlockProperty;

import java.util.*;

/**
 * Implementation of {@link BlockProperty}.
 */
public abstract class GlowBlockProperty<T extends Comparable<T>> implements BlockProperty<T> {

    private final String name;
    private final List<T> values;

    public GlowBlockProperty(String name, Collection<T> values) {
        this.name = name;
        this.values = new ArrayList<>(values);
        Collections.sort(this.values);
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final Collection<T> getValidValues() {
        return values;
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

    public static <T extends Comparable<T>> T cycle(BlockProperty<T> property, Object value) {
        Iterator<T> iter = property.getValidValues().iterator();
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

    public static <T extends Comparable<T>> T getDefault(BlockProperty<T> property) {
        if (property instanceof GlowBlockProperty) {
            return ((GlowBlockProperty<T>) property).getDefault();
        }
        return property.getValidValues().iterator().next();
    }

    /**
     * Validate and filter a value for this property.
     * @param value the value to validate
     * @return the filtered value
     * @throws IllegalArgumentException if the value is invalid
     */
    public static Comparable<?> validate(BlockProperty<?> property, Comparable<?> value) {
        if (property.getValidValues().contains(value)) {
            return value;
        } else {
            throw new IllegalArgumentException("Invalid value for " + property + ": " + value);
        }
    }

    /**
     * Create a new boolean property with the given name.
     * @param name The name of the property
     * @return A new boolean property
     */
    public static BooleanProperty ofBoolean(String name) {
        return new BooleanProp(name);
    }

    /**
     * Create a new integer property with the given attributes.
     * @param name The name of the property
     * @param min The minimum value, inclusive
     * @param max The maximum value, inclusive
     * @return A new integer property
     */
    public static IntegerProperty ofRange(String name, int min, int max) {
        return new IntegerProp(name, min, max);
    }

    /**
     * Create a new string property from the values of the given enumeration.
     * @param name The name of the property
     * @param clazz The enumeration class to use the values of
     * @return A new string property
     * @throws IllegalArgumentException if the class contains no values
     */
    public static <E extends Enum<E>> EnumProperty<E> ofEnum(String name, Class<E> clazz) {
        E[] values = clazz.getEnumConstants();
        if (values == null) {
            throw new IllegalArgumentException(clazz + " is not an enumeration");
        }
        if (values.length == 0) {
            throw new IllegalArgumentException(clazz + " has no values");
        }
        return new EnumProp<>(name, clazz, values);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Implementation subclasses

    private static class BooleanProp extends GlowBlockProperty<Boolean> implements BooleanProperty {
        private BooleanProp(String name) {
            super(name, Arrays.asList(true, false));
        }

        @Override
        public String getNameForValue(Boolean value) {
            return value.toString();
        }

        @Override
        public Optional<Boolean> getValueForName(String name) {
            if (name.equalsIgnoreCase("true")) {
                return Optional.of(true);
            } else if (name.equalsIgnoreCase("false")) {
                return Optional.of(false);
            } else {
                return Optional.absent();
            }
        }
    }

    private static class IntegerProp extends GlowBlockProperty<Integer> implements IntegerProperty {
        private IntegerProp(String name, int min, int max) {
            super(name, valuesFor(min, max));
        }

        private static Collection<Integer> valuesFor(int min, int max) {
            List<Integer> values = new ArrayList<>(max - min + 1);
            for (int i = min; i <= max; ++i) {
                values.add(i);
            }
            return values;
        }

        @Override
        public String getNameForValue(Integer value) {
            return value.toString();
        }

        @Override
        public Optional<Integer> getValueForName(String name) {
            try {
                return Optional.of(Integer.parseInt(name));
            } catch (NumberFormatException e) {
                return Optional.absent();
            }
        }
    }

    private static class EnumProp<E extends Enum<E>> extends GlowBlockProperty<E> implements EnumProperty<E> {
        private final Class<E> clazz;

        public EnumProp(String name, Class<E> clazz, E[] values) {
            super(name, Arrays.asList(values));
            this.clazz = clazz;
        }

        @Override
        public String getNameForValue(E value) {
            return value.name().toLowerCase();
        }

        @Override
        public Optional<E> getValueForName(String name) {
            try {
                return Optional.of(Enum.valueOf(clazz, name.toUpperCase()));
            } catch (IllegalArgumentException e) {
                return Optional.absent();
            }
        }
    }
}
