package net.glowstone.util.nbt;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import net.glowstone.util.DynamicallyTypedMapWithDoubles;
import org.bukkit.util.EulerAngle;

/**
 * The {@code TAG_Compound} tag.
 */
public final class CompoundTag extends Tag<Map<String, Tag>>
        implements DynamicallyTypedMapWithDoubles<String> {

    /**
     * The value.
     */
    @Getter
    private final Map<String, Tag> value = new LinkedHashMap<>();

    /**
     * Creates a new, empty CompoundTag.
     */
    public CompoundTag() {
        super(TagType.COMPOUND);
    }

    @Override
    protected void valueToString(StringBuilder builder) {
        builder.append(value.size()).append(" entries\n{\n");
        for (Entry<String, Tag> entry : value.entrySet()) {
            builder.append("    ").append(entry.getKey()).append(": ")
                    .append(entry.getValue().toString().replaceAll("\n", "\n    ")).append("\n");
        }
        builder.append("}");
    }

    ////////////////////////////////////////////////////////////////////////////
    // Helper stuff

    public boolean isEmpty() {
        return value.isEmpty();
    }

    /**
     * Check if the compound contains the given key.
     *
     * @param key The key.
     * @return True if the key is in the map.
     */
    public boolean containsKey(String key) {
        return value.containsKey(key);
    }

    public void remove(String key) {
        value.remove(key);
    }

    /**
     * Checks to see if this tag is a strict, deep submap of the given CompoundTag.
     *
     * @param other The CompoundTag that should contain our values.
     */
    public boolean matches(CompoundTag other) {
        for (Entry<String, Tag> entry : value.entrySet()) {
            if (!other.value.containsKey(entry.getKey())) {
                return false;
            }
            Tag value = entry.getValue();
            Tag otherValue = other.value.get(entry.getKey());
            if ((value == null && otherValue != null) || (value != null && otherValue == null)) {
                return false;
            }
            if (value != null) {
                if (value.getClass() != otherValue.getClass()) {
                    return false;
                }
                if (value instanceof CompoundTag) {
                    if (!((CompoundTag) value).matches((CompoundTag) otherValue)) {
                        return false;
                    }
                } else if (value instanceof IntArrayTag) {
                    if (!Arrays.equals(((IntArrayTag) value).getValue(),
                            ((IntArrayTag) otherValue).getValue())) {
                        return false;
                    }
                } else if (value instanceof ByteArrayTag) {
                    if (!Arrays.equals(((ByteArrayTag) value).getValue(),
                            ((ByteArrayTag) otherValue).getValue())) {
                        return false;
                    }
                } else if (!value.equals(otherValue)) {
                    // Note: When Mojang actually starts using lists, revisit this.
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Merges the contents of this compound into the supplied compound.
     *
     * @param other the other compound to merge into.
     * @param overwrite whether keys already set in the other compound should be
     *         overwritten.
     */
    public void mergeInto(CompoundTag other, boolean overwrite) {
        for (String key : value.keySet()) {
            if (!overwrite && other.containsKey(key)) {
                continue;
            }
            other.put(key, value.get(key));
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Simple gets

    public boolean getBool(String key) {
        return containsKey(key) && getByte(key) != 0;
    }

    /**
     * Returns the value of a {@code byte} subtag.
     *
     * @param key the key to look up
     * @return the tag value
     */
    public byte getByte(String key) {
        if (isInt(key)) {
            return (byte) getInt(key);
        }
        return get(key, ByteTag.class);
    }

    /**
     * Returns the value of a {@code short} subtag.
     *
     * @param key the key to look up
     * @return the tag value
     */
    public short getShort(String key) {
        if (isInt(key)) {
            return (short) getInt(key);
        }
        return get(key, ShortTag.class);
    }

    /**
     * Returns the value of an {@code int} subtag.
     *
     * @param key the key to look up
     * @return the tag value
     */
    public int getInt(String key) {
        if (isByte(key)) {
            return (int) getByte(key);
        } else if (isShort(key)) {
            return (int) getShort(key);
        } else if (isLong(key)) {
            return (int) getLong(key);
        }
        return get(key, IntTag.class);
    }

    @Override
    public boolean getBoolean(String key) {
        return getByte(key) != 0;
    }

    /**
     * Returns the value of a {@code long} subtag.
     *
     * @param key the key to look up
     * @return the tag value
     */
    public long getLong(String key) {
        if (isInt(key)) {
            return (long) getInt(key);
        }
        return get(key, LongTag.class);
    }

    /**
     * Returns the value of a {@code float} subtag.
     *
     * @param key the key to look up
     * @return the tag value
     */
    public float getFloat(String key) {
        if (isDouble(key)) {
            return (float) getDouble(key);
        } else if (isInt(key)) {
            return (float) getInt(key);
        }
        return get(key, FloatTag.class);
    }

    /**
     * Returns the value of a {@code double} subtag.
     *
     * @param key the key to look up
     * @return the tag value
     */
    public double getDouble(String key) {
        if (isFloat(key)) {
            return (double) getFloat(key);
        } else if (isInt(key)) {
            return (double) getInt(key);
        }
        return get(key, DoubleTag.class);
    }

    /**
     * Returns the value of a {@code byte[]} subtag.
     *
     * @param key the key to look up
     * @return the tag value
     */
    public byte[] getByteArray(String key) {
        return get(key, ByteArrayTag.class);
    }

    /**
     * Returns the value of a {@link String} subtag.
     *
     * @param key the key to look up
     * @return the tag value
     */
    public String getString(String key) {
        return get(key, StringTag.class);
    }

    /**
     * Returns the value of an {@code int[]} subtag.
     *
     * @param key the key to look up
     * @return the tag value
     */
    public int[] getIntArray(String key) {
        return get(key, IntArrayTag.class);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Fancy gets

    /**
     * Returns the value of a {@link List} subtag.
     *
     * @param key the key to look up
     * @param <V> the list element type
     * @return the tag value
     */
    @SuppressWarnings("unchecked")
    public <V> List<V> getList(String key, TagType type) {
        List<? extends Tag> original = getTagList(key, type);
        List<V> result = new ArrayList<>(original.size());
        result.addAll(
                original.stream().map(item -> (V) item.getValue()).collect(Collectors.toList()));
        return result;
    }

    /**
     * Returns the value of a compound subtag.
     *
     * @param key the key to look up
     * @return the tag value
     */
    public CompoundTag getCompound(String key) {
        return getTag(key, CompoundTag.class);
    }

    /**
     * Returns the value of a list subtag with CompoundTag elements.
     *
     * @param key the key to look up
     * @return the tag value
     */
    @SuppressWarnings("unchecked")
    public List<CompoundTag> getCompoundList(String key) {
        return (List<CompoundTag>) getTagList(key, TagType.COMPOUND);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Simple is

    /**
     * Test whether the subtag with the given key is of {@code byte} type.
     *
     * @param key the key to look up
     * @return true if the subtag exists and is a {@code byte}; false otherwise
     */
    public boolean isByte(String key) {
        return is(key, ByteTag.class);
    }

    /**
     * Test whether the subtag with the given key is of {@code short} type.
     *
     * @param key the key to look up
     * @return true if the subtag exists and is a {@code short}; false otherwise
     */
    public boolean isShort(String key) {
        return is(key, ShortTag.class);
    }

    /**
     * Test whether the subtag with the given key is of {@code int} type.
     *
     * @param key the key to look up
     * @return true if the subtag exists and is an {@code int}; false otherwise
     */
    public boolean isInt(String key) {
        return is(key, IntTag.class);
    }

    /**
     * Test whether the subtag with the given key is of {@code long} type.
     *
     * @param key the key to look up
     * @return true if the subtag exists and is a {@code long}; false otherwise
     */
    public boolean isLong(String key) {
        return is(key, LongTag.class);
    }

    /**
     * Test whether the subtag with the given key is of {@code float} type.
     *
     * @param key the key to look up
     * @return true if the subtag exists and is a {@code float}; false otherwise
     */
    public boolean isFloat(String key) {
        return is(key, FloatTag.class);
    }

    /**
     * Test whether the subtag with the given key is of {@code double} type.
     *
     * @param key the key to look up
     * @return true if the subtag exists and is a {@code double}; false otherwise
     */
    public boolean isDouble(String key) {
        return is(key, DoubleTag.class);
    }

    /**
     * Test whether the subtag with the given key is of {@code byte[]} type.
     *
     * @param key the key to look up
     * @return true if the subtag exists and is a {@code byte[]}; false otherwise
     */
    public boolean isByteArray(String key) {
        return is(key, ByteArrayTag.class);
    }

    /**
     * Test whether the subtag with the given key is of {@link String} type.
     *
     * @param key the key to look up
     * @return true if the subtag exists and is a {@link String}; false otherwise
     */
    public boolean isString(String key) {
        return is(key, StringTag.class);
    }

    /**
     * Test whether the subtag with the given key is of {@code int[]} type.
     *
     * @param key the key to look up
     * @return true if the subtag exists and is an {@code int[]}; false otherwise
     */
    public boolean isIntArray(String key) {
        return is(key, IntArrayTag.class);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Fancy is

    /**
     * Test whether the subtag with the given key is of {@link List} type.
     *
     * @param key the key to look up
     * @param type the {@link TagType} of the list's elements
     * @return true if the subtag exists and is a {@link List}; false otherwise
     */
    public boolean isList(String key, TagType type) {
        if (!is(key, ListTag.class)) {
            return false;
        }
        ListTag tag = getTag(key, ListTag.class);
        return tag.getChildType() == type;
    }

    /**
     * Test whether the subtag with the given key is of {@link CompoundTag} type.
     *
     * @param key the key to look up
     * @return true if the subtag exists and is a {@link CompoundTag}; false otherwise
     */
    public boolean isCompound(String key) {
        return is(key, CompoundTag.class);
    }

    /**
     * Test whether the subtag with the given key is of {@link List} type with elements of type
     * {@link CompoundTag}.
     *
     * @param key the key to look up
     * @return true if the subtag exists and is a {@link List} with elements of type
     *         {@link CompoundTag}; false otherwise
     */
    public boolean isCompoundList(String key) {
        return isList(key, TagType.COMPOUND);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Simple sets

    public void putBool(String key, boolean value) {
        putByte(key, value ? 1 : 0);
    }

    public void putByte(String key, int value) {
        put(key, new ByteTag((byte) value));
    }

    public void putShort(String key, int value) {
        put(key, new ShortTag((short) value));
    }

    public void putInt(String key, int value) {
        put(key, new IntTag(value));
    }

    public void putLong(String key, long value) {
        put(key, new LongTag(value));
    }

    public void putFloat(String key, double value) {
        put(key, new FloatTag((float) value));
    }

    public void putDouble(String key, double value) {
        put(key, new DoubleTag(value));
    }

    public void putByteArray(String key, byte... value) {
        put(key, new ByteArrayTag(value));
    }

    public void putString(String key, String value) {
        put(key, new StringTag(value));
    }

    public void putIntArray(String key, int... value) {
        put(key, new IntArrayTag(value));
    }

    ////////////////////////////////////////////////////////////////////////////
    // Fancy sets

    /**
     * Adds or replaces a list subtag, converting the list entries to tags.
     *
     * @param <V> the list elements' Java type
     * @param key the key to write to
     * @param type the list elements' tag type
     * @param value the list contents, as objects to convert to tags
     * @param tagCreator a function that will convert each V to an element tag
     */
    public <V> void putList(String key, TagType type, List<V> value,
            Function<? super V, ? extends Tag> tagCreator) {
        List<Tag> result = new ArrayList<>(value.size());
        for (V item : value) {
            result.add(tagCreator.apply(item));
        }
        put(key, new ListTag<>(type, result));
    }

    public void putCompound(String key, CompoundTag tag) {
        put(key, tag);
    }

    /**
     * Adds or replaces a list subtag with a list of compound tags.
     *
     * @param key the key to write to
     * @param list the list contents as compound tags
     */
    public void putCompoundList(String key, List<CompoundTag> list) {
        put(key, new ListTag<>(TagType.COMPOUND, list));
    }

    /**
     * Adds or replaces a list subtag with a list of strings.
     *
     * @param key the key to write to
     * @param list the list contents as strings, to convert to string tags
     */
    public void putStringList(String key, List<String> list) {
        putList(key, TagType.STRING, list, StringTag::new);
    }

    /**
     * Adds or replaces a list subtag with a list of floats.
     *
     * @param key the key to write to
     * @param list the list contents as floats, to convert to float tags
     */
    public void putFloatList(String key, List<Float> list) {
        putList(key, TagType.FLOAT, list, FloatTag::new);
    }

    /**
     * Adds or replaces a list subtag with a list of doubles.
     *
     * @param key the key to write to
     * @param list the list contents as doubles, to convert to double tags
     */
    public void putDoubleList(String key, List<Double> list) {
        putList(key, TagType.DOUBLE, list, DoubleTag::new);
    }

    /**
     * Adds or replaces a list subtag with a list of longs.
     *
     * @param key the key to write to
     * @param list the list contents as longs, to convert to long tags
     */
    public void putLongList(String key, List<Long> list) {
        putList(key, TagType.LONG, list, LongTag::new);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Accessor helpers

    private <T extends Tag<?>> boolean is(String key, Class<T> clazz) {
        if (!containsKey(key)) {
            return false;
        }
        Tag tag = value.get(key);
        return tag != null && clazz == tag.getClass();
    }

    void put(String key, Tag tag) {
        checkNotNull(key, "Key cannot be null");
        checkNotNull(tag, "Tag cannot be null");
        value.put(key, tag);
    }

    private <V, T extends Tag<V>> V get(String key, Class<T> clazz) {
        return getTag(key, clazz).getValue();
    }

    @SuppressWarnings("unchecked")
    private <T extends Tag<?>> T getTag(String key, Class<T> clazz) {
        if (!is(key, clazz)) {
            throw new IllegalArgumentException(
                    "Compound does not contain " + clazz.getSimpleName() + " \"" + key + "\"");
        }
        return (T) value.get(key);
    }

    private List<? extends Tag> getTagList(String key, TagType type) {
        ListTag<?> tag = getTag(key, ListTag.class);
        if (tag.getValue().isEmpty()) {
            // empty lists are allowed to be the wrong type
            return Arrays.asList();
        }
        if (tag.getChildType() != type) {
            throw new IllegalArgumentException(
                    "List \"" + key + "\" contains " + tag.getChildType() + ", not " + type);
        }
        return tag.getValue();
    }
}

