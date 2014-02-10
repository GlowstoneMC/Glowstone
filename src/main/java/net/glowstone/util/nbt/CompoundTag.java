package net.glowstone.util.nbt;

import java.util.Collections;
import java.util.Map;

/**
 * The {@code TAG_Compound} tag.
 * @author Graham Edgecombe
 */
public final class CompoundTag extends Tag<Map<String, Tag>> {

    /**
     * The value.
     */
    private final Map<String, Tag> value;

    /**
     * Creates the tag.
     * @param name The name.
     * @param value The value.
     */
    public CompoundTag(String name, Map<String, Tag> value) {
        super(TagType.COMPOUND, name);
        this.value = Collections.unmodifiableMap(value);
    }

    @Override
    public Map<String, Tag> getValue() {
        return value;
    }

    @Override
    protected void valueToString(StringBuilder bldr) {
        bldr.append(value.size()).append(" entries\r\n{\r\n");
        for (Map.Entry<String, Tag> entry : value.entrySet()) {
            bldr.append("    ").append(entry.getValue().toString().replaceAll("\r\n", "\r\n    ")).append("\r\n");
        }
        bldr.append("}");
    }

    ////////////////////////////////////////////////////////////////////////////
    // Accessor helpers

    public boolean containsKey(String key) {
        return value.containsKey(key);
    }

    public <T extends Tag<?>> boolean is(String key, Class<T> clazz) {
        if (!containsKey(key)) return false;
        final Tag tag = value.get(key);
        return tag != null && clazz == tag.getClass();
    }

    public <V, T extends Tag<V>> V get(String key, Class<T> clazz) {
        return getTag(key, clazz).getValue();
    }

    @SuppressWarnings("unchecked")
    public <T extends Tag<?>> T getTag(String key, Class<T> clazz) {
        if (!is(key, clazz)) {
            throw new IllegalArgumentException("Compound \"" + getName() + "\" does not contain " + clazz.getSimpleName() + " \"" + key + "\"");
        }
        return (T) value.get(key);
    }

}

