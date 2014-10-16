package net.glowstone.block.block2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of {@link BlockProperty}.
 */
public abstract class GlowBlockProperty<T> implements BlockProperty<T> {

    private final String name;

    public GlowBlockProperty(String name) {
        this.name = name;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final String toString() {
        return name;
    }

    /**
     * Create a new boolean property with the given name.
     * @param name The name of the property
     * @return A new boolean property
     */
    public static BlockProperty<Boolean> ofBoolean(String name) {
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
     * Create a new string property with the given attributes.
     * @param name The name of the property
     * @param values The possible values of the property
     * @return A new string property
     * @throws IllegalArgumentException if no values are provided
     */
    public static StringProperty ofStrings(String name, String... values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("values.length must be > 0");
        }
        return new StringProp(name, values);
    }

    /**
     * Create a new string property from the values of the given enumeration.
     * @param name The name of the property
     * @param clazz The enumeration class to use the values of
     * @return A new string property
     * @throws IllegalArgumentException if the class contains no values
     */
    public static StringProperty ofEnum(String name, Class<? extends Enum> clazz) {
        Enum[] values = clazz.getEnumConstants();
        if (values == null) {
            throw new IllegalArgumentException(clazz + " is not an enumeration");
        }
        if (values.length == 0) {
            throw new IllegalArgumentException(clazz + " has no values");
        }
        String[] names = new String[values.length];
        for (int i = 0; i < values.length; ++i) {
            names[i] = values[i].name().toLowerCase();
        }
        return new StringProp(name, names);
    }

    private static class BooleanProp extends GlowBlockProperty<Boolean> {
        private BooleanProp(String name) {
            super(name);
        }

        @Override
        public Boolean getDefault() {
            return false;
        }

        @Override
        public Boolean validate(Boolean value) {
            return value;
        }
    }

    private static class IntegerProp extends GlowBlockProperty<Integer> implements IntegerProperty {
        private final int min, max;

        private IntegerProp(String name, int min, int max) {
            super(name);
            this.min = min;
            this.max = max;
        }

        @Override
        public int getMinimum() {
            return min;
        }

        @Override
        public int getMaximum() {
            return max;
        }

        @Override
        public Integer getDefault() {
            return min;
        }

        @Override
        public Integer validate(Integer value) {
            if (value < min || value > max) {
                throw new IllegalArgumentException("Number " + value + " outside range [" + min + "," + max + "]");
            }
            return value;
        }
    }

    private static class StringProp extends GlowBlockProperty<String> implements StringProperty {
        private final List<String> values = new ArrayList<>();

        private StringProp(String name, String... values) {
            super(name);
            Collections.addAll(this.values, values);
        }

        @Override
        public Collection<String> getValues() {
            return Collections.unmodifiableCollection(values);
        }

        @Override
        public String getDefault() {
            return values.get(0);
        }

        @Override
        public String validate(String value) {
            String str = value.toLowerCase();
            if (!values.contains(str)) {
                throw new IllegalArgumentException("String " + str + " not in set " + values);
            }
            return str;
        }
    }

}
