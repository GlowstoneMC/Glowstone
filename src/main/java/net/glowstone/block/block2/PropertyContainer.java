package net.glowstone.block.block2;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Container for a map of BlockProperties to their values.
 */
public final class PropertyContainer {

    private final HashMap<BlockProperty<?>, Object> values;

    public PropertyContainer(BlockProperty<?>[] properties) {
        this.values = new HashMap<>();
        for (BlockProperty property : properties) {
            values.put(property, property.getDefault());
        }
    }

    private PropertyContainer(Map<BlockProperty<?>, Object> values) {
        this.values = new HashMap<>(values);
    }

    public Object get(BlockProperty property) {
        return values.get(property);
    }

    public <T> PropertyContainer with(BlockProperty<T> property, T value) {
        PropertyContainer result = new PropertyContainer(values);
        result.values.put(property, property.validate(value));
        return result;
    }

    public Collection<BlockProperty<?>> keySet() {
        return values.keySet();
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
