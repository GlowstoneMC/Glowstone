package net.glowstone.block.block2;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Todo: Javadoc for PropertyContainer.
 */
public final class PropertyContainer {

    private final HashMap<BlockProperty, Object> values;

    public PropertyContainer(BlockProperty[] props) {
        this.values = new HashMap<>();
        for (BlockProperty prop : props) {
            values.put(prop, defaultValue(prop));
        }
    }

    private PropertyContainer(Map<BlockProperty, Object> values) {
        this.values = new HashMap<>(values);
    }

    public Object get(BlockProperty property) {
        return values.get(property);
    }

    public PropertyContainer with(BlockProperty property, Object value) {
        PropertyContainer result = new PropertyContainer(values);
        result.values.put(property, validate(property, value));
        return result;
    }

    public Collection<BlockProperty> keySet() {
        return values.keySet();
    }

    public static Object defaultValue(BlockProperty property) {
        switch (property.getType()) {
            case BOOLEAN:
                return false;
            case INTEGER:
                return ((BlockProperty.IntegerProperty) property).getDefault();
            case STRING:
                return ((BlockProperty.StringProperty) property).getDefault();
            default:
                throw new IllegalArgumentException("Unknown type " + property.getType() + " for " + property);
        }
    }

    public static Object validate(BlockProperty property, Object value) {
        switch (property.getType()) {
            case BOOLEAN:
                if (!(value instanceof Boolean)) {
                    throw new IllegalArgumentException("Expected boolean, got " + value);
                }
                return value;
            case INTEGER: {
                if (!(value instanceof Number)) {
                    throw new IllegalArgumentException("Expected number, got " + value);
                }
                int num = ((Number) value).intValue();
                BlockProperty.IntegerProperty prop = (BlockProperty.IntegerProperty) property;
                if (num < prop.getMinimum() || num > prop.getMaximum()) {
                    throw new IllegalArgumentException("Number " + prop + " outside range [" + prop.getMinimum() + "," + prop.getMaximum() + "]");
                }
                return num;
            }
            case STRING: {
                BlockProperty.StringProperty prop = (BlockProperty.StringProperty) property;
                String str = value.toString().toLowerCase();
                if (!prop.getValues().contains(str)) {
                    throw new IllegalArgumentException("String " + str + " not in set " + prop.getValues());
                }
                return str;
            }
            default:
                throw new IllegalArgumentException("Unknown type " + property.getType() + " for " + property);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PropertyContainer that = (PropertyContainer) o;

        return values.equals(that.values);
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    @Override
    public String toString() {
        String base = values.toString();
        return base.substring(1, base.length() - 1);
    }
}
