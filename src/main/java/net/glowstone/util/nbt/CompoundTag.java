package net.glowstone.util.nbt;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
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

    /**
     * Creates the tag.
     * @param name The name.
     * @param tags A list of child tags.
     */
    public CompoundTag(String name, List<Tag> tags) {
        super(TagType.COMPOUND, name);
        Map<String, Tag> map = new LinkedHashMap<String, Tag>();
        for (Tag tag : tags) {
            if (tag.getName() == null || tag.getName().isEmpty()) {
                throw new IllegalArgumentException("When creating CompoundTag: tag name cannot be null or empty");
            }
            map.put(tag.getName(), tag);
        }
        this.value = Collections.unmodifiableMap(map);
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
    public <T extends Tag> List<T> getList(String key, Class<T> childClass) {
        ListTag tag = getTag(key, ListTag.class);
        if (tag.getChildType().getTagClass() != childClass) {
            throw new IllegalArgumentException("List \"" + key + "\" does not contain type " + childClass.getSimpleName());
        }
        return tag.getValue();
    }

    @SuppressWarnings("unchecked")
    public <T extends Tag<?>> T getTag(String key, Class<T> clazz) {
        if (!is(key, clazz)) {
            throw new IllegalArgumentException("Compound \"" + getName() + "\" does not contain " + clazz.getSimpleName() + " \"" + key + "\"");
        }
        return (T) value.get(key);
    }
}

