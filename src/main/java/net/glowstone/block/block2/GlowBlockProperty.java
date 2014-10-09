package net.glowstone.block.block2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Todo: Javadoc for GlowBlockProperty.
 */
public class GlowBlockProperty implements BlockProperty {

    private final String name;
    private final Type type;

    private GlowBlockProperty(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return name;
    }

    public static BlockProperty ofBoolean(String name) {
        return new BooleanProp(name);
    }

    public static BlockProperty ofRange(String name, int min, int max) {
        return new IntegerProp(name, min, max);
    }

    public static BlockProperty ofStrings(String name, String... values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("values.length must be > 0");
        }
        return new StringProp(name, values);
    }

    public static BlockProperty ofEnum(String name, Class<? extends Enum> clazz) {
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

    private static class BooleanProp extends GlowBlockProperty {
        private BooleanProp(String name) {
            super(name, Type.BOOLEAN);
        }
    }

    private static class IntegerProp extends GlowBlockProperty implements IntegerProperty {
        private final int min, max;

        private IntegerProp(String name, int min, int max) {
            super(name, Type.INTEGER);
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
        public int getDefault() {
            return min;
        }
    }

    private static class StringProp extends GlowBlockProperty implements StringProperty {
        private final List<String> values = new ArrayList<>();

        private StringProp(String name, String... values) {
            super(name, Type.STRING);
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
    }

}
