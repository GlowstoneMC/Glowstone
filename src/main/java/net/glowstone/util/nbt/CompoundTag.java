package net.glowstone.util.nbt;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.Getter;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.DynamicallyTypedMapWithDoubles;
import net.glowstone.util.FloatConsumer;
import net.glowstone.util.ShortConsumer;
import org.bukkit.inventory.ItemStack;

/**
 * The {@code TAG_Compound} tag.
 */
public class CompoundTag extends Tag<Map<String, Tag>>
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

    public boolean getBoolDefaultFalse(String key) {
        return isByte(key) && getByte(key) != 0;
    }

    public boolean getBoolDefaultTrue(String key) {
        return !isByte(key) || getByte(key) != 0;
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
     * @param type the list element tag type
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
     * Returns the value of a compound subtag, if it exists. Multiple strings can be passed in to
     * retrieve a sub-subtag (e.g. {@code tryGetCompound("foo", "bar")} returns a compound subtag
     * called "bar" of a compound subtag called "foo", or null if either of those tags doesn't exist
     * or isn't compound.
     *
     * @param keys the key to look up, or multiple keys forming a subtag path
     * @return the tag value
     */
    @Nullable
    public CompoundTag tryGetCompound(String... keys) {
        CompoundTag tag = this;
        for (String key : keys) {
            if (tag.isCompound(key)) {
                tag = tag.getCompound(key);
            } else {
                return null;
            }
        }
        return tag;
    }

    /**
     * Applies the given function to a compound subtag if it is present. Multiple strings can be
     * passed in to operate on a sub-subtag, as with {@link #tryGetCompound(String...)}.
     *
     * @param consumer the function to apply
     * @param keys the key to look up, or multiple keys forming a subtag path
     * @return true if the tag exists and was passed to the consumer; false otherwise
     */
    public boolean consumeCompound(Consumer<? super CompoundTag> consumer, String... keys) {
        CompoundTag tag = tryGetCompound(keys);
        if (tag != null) {
            consumer.accept(tag);
            return true;
        } else {
            return false;
        }
    }

    private <V, T extends Tag<V>> boolean consumeObject(Consumer<? super V> consumer,
            Class<T> clazz, String[] keys) {
        String lastKey = keys[keys.length - 1];
        String[] interveningKeys = Arrays.copyOf(keys, keys.length - 1);
        boolean[] consumed = {false};
        return consumeCompound(tag -> {
            if (tag.is(lastKey, clazz)) {
                consumer.accept(tag.get(lastKey, clazz));
                consumed[0] = true;
            }
        }, (String[]) interveningKeys) && consumed[0];
    }

    /**
     * Applies the given function to a float subtag if it is present. Multiple strings can be
     * passed in to operate on a sub-subtag, as with {@link #tryGetCompound(String...)}, except that
     * the last one must be a float rather than compound subtag.
     *
     * @param consumer the function to apply
     * @param keys the key to look up, or multiple keys forming a subtag path
     * @return true if the tag exists and was passed to the consumer; false otherwise
     */
    public boolean consumeFloat(FloatConsumer consumer, String... keys) {
        // Avoid boxing by not delegating to consumeObject
        String lastKey = keys[keys.length - 1];
        String[] interveningKeys = Arrays.copyOf(keys, keys.length - 1);
        boolean[] consumed = {false};
        return consumeCompound(tag -> {
            if (tag.isFloat(lastKey)) {
                consumer.accept(tag.getFloat(lastKey));
                consumed[0] = true;
            }
        }, (String[]) interveningKeys) && consumed[0];
    }

    /**
     * Applies the given function to a double subtag if it is present. Multiple strings can be
     * passed in to operate on a sub-subtag, as with {@link #tryGetCompound(String...)}, except that
     * the last one must be a double rather than compound subtag.
     *
     * @param consumer the function to apply
     * @param keys the key to look up, or multiple keys forming a subtag path
     * @return true if the tag exists and was passed to the consumer; false otherwise
     */
    public boolean consumeDouble(DoubleConsumer consumer, String... keys) {
        // Avoid boxing by not delegating to consumeObject
        String lastKey = keys[keys.length - 1];
        String[] interveningKeys = Arrays.copyOf(keys, keys.length - 1);
        boolean[] consumed = {false};
        return consumeCompound(tag -> {
            if (tag.isDouble(lastKey)) {
                consumer.accept(tag.getDouble(lastKey));
                consumed[0] = true;
            }
        }, (String[]) interveningKeys) && consumed[0];
    }

    /**
     * Applies the given function to an integer subtag if it is present. Multiple strings can be
     * passed in to operate on a sub-subtag, as with {@link #tryGetCompound(String...)}, except that
     * the last one must be an integer rather than compound subtag.
     *
     * @param consumer the function to apply
     * @param keys the key to look up, or multiple keys forming a subtag path
     * @return true if the tag exists and was passed to the consumer; false otherwise
     */
    public boolean consumeInt(IntConsumer consumer, String... keys) {
        // Avoid boxing by not delegating to consumeObject
        String lastKey = keys[keys.length - 1];
        String[] interveningKeys = Arrays.copyOf(keys, keys.length - 1);
        boolean[] consumed = {false};
        return consumeCompound(tag -> {
            if (tag.isInt(lastKey)) {
                consumer.accept(tag.getInt(lastKey));
                consumed[0] = true;
            }
        }, (String[]) interveningKeys) && consumed[0];
    }

    /**
     * Applies the given function to a byte array subtag if it is present. Multiple strings can
     * be passed in to operate on a sub-subtag, as with {@link #tryGetCompound(String...)}, except
     * that the last one must be a byte array rather than compound subtag.
     *
     * @param consumer the function to apply
     * @param keys the key to look up, or multiple keys forming a subtag path
     * @return true if the tag exists and was passed to the consumer; false otherwise
     */
    public boolean consumeByteArray(Consumer<? super byte[]> consumer, String... keys) {
        return consumeObject(consumer, ByteArrayTag.class, keys);
    }

    /**
     * Applies the given function to an integer array subtag if it is present. Multiple strings can
     * be passed in to operate on a sub-subtag, as with {@link #tryGetCompound(String...)}, except
     * that the last one must be an integer array rather than compound subtag.
     *
     * @param consumer the function to apply
     * @param keys the key to look up, or multiple keys forming a subtag path
     * @return true if the tag exists and was passed to the consumer; false otherwise
     */
    public boolean consumeIntArray(Consumer<? super int[]> consumer, String... keys) {
        return consumeObject(consumer, IntArrayTag.class, keys);
    }

    /**
     * Applies the given function to a long subtag if it is present. Multiple strings can be
     * passed in to operate on a sub-subtag, as with {@link #tryGetCompound(String...)}, except that
     * the last one must be a long rather than compound subtag.
     *
     * @param consumer the function to apply
     * @param keys the key to look up, or multiple keys forming a subtag path
     * @return true if the tag exists and was passed to the consumer; false otherwise
     */
    public boolean consumeLong(LongConsumer consumer, String... keys) {
        // Avoid boxing by not delegating to consumeObject
        String lastKey = keys[keys.length - 1];
        String[] interveningKeys = Arrays.copyOf(keys, keys.length - 1);
        boolean[] consumed = {false};
        return consumeCompound(tag -> {
            if (tag.isInt(lastKey)) {
                consumer.accept(tag.getInt(lastKey));
                consumed[0] = true;
            }
        }, (String[]) interveningKeys) && consumed[0];
    }


    /**
     * Applies the given function to an integer subtag if it is present. Multiple strings can be
     * passed in to operate on a sub-subtag, as with {@link #tryGetCompound(String...)}, except that
     * the last one must be an integer rather than compound subtag.
     *
     * @param consumer the function to apply
     * @param keys the key to look up, or multiple keys forming a subtag path
     * @return true if the tag exists and was passed to the consumer; false otherwise
     */
    public boolean consumeShort(ShortConsumer consumer, String... keys) {
        // Avoid boxing by not delegating to consumeObject
        String lastKey = keys[keys.length - 1];
        String[] interveningKeys = Arrays.copyOf(keys, keys.length - 1);
        boolean[] consumed = {false};
        return consumeCompound(tag -> {
            if (tag.isShort(lastKey)) {
                consumer.accept(tag.getShort(lastKey));
                consumed[0] = true;
            }
        }, (String[]) interveningKeys) && consumed[0];
    }


    /**
     * Applies the given function to a compound subtag if it is present, first converting it to an
     * item using {@link NbtSerialization#readItem(CompoundTag)}. Multiple strings can be
     * passed in to operate on a sub-subtag, as with {@link #tryGetCompound(String...)}.
     *
     * @param consumer the function to apply
     * @param keys the key to look up, or multiple keys forming a subtag path
     * @return true if the tag exists and was passed to the consumer; false otherwise
     */
    public boolean consumeItem(Consumer<? super ItemStack> consumer, String... keys) {
        return consumeCompound(tag -> consumer.accept(NbtSerialization.readItem(tag)), keys);
    }

    /**
     * Applies the given function to a byte subtag if it is present, converting it to boolean first.
     * Multiple strings can be passed in to operate on a sub-subtag, as with
     * {@link #tryGetCompound(String...)}, except that the last one must be a byte rather than
     * compound subtag.
     *
     * @param consumer the function to apply
     * @param keys the key to look up, or multiple keys forming a subtag path
     * @return true if the tag exists and was passed to the consumer; false otherwise
     */
    public boolean consumeBoolean(Consumer<? super Boolean> consumer, String... keys) {
        // For a boolean, boxing carries no penalty, per
        // https://stackoverflow.com/questions/27698911/why-there-is-no-booleanconsumer-in-java-8
        return consumeObject(byteVal -> consumer.accept(byteVal != 0), ByteTag.class, keys);
    }

    /**
     * Applies the given function to a byte subtag if it is present.
     * Multiple strings can be passed in to operate on a sub-subtag, as with
     * {@link #tryGetCompound(String...)}, except that the last one must be a byte rather than
     * compound subtag.
     *
     * @param consumer the function to apply
     * @param keys the key to look up, or multiple keys forming a subtag path
     * @return true if the tag exists and was passed to the consumer; false otherwise
     */
    public boolean consumeByte(Consumer<? super Byte> consumer, String... keys) {
        // For a byte, boxing carries no penalty, per
        // https://stackoverflow.com/questions/27698911/why-there-is-no-booleanconsumer-in-java-8
        return consumeObject(consumer, ByteTag.class, keys);
    }

    /**
     * Applies the given function to a byte subtag if it is present, converting it to boolean and
     * negating it first. Multiple strings can be passed in to operate on a sub-subtag, as with
     * {@link #tryGetCompound(String...)}, except that the last one must be a byte rather than
     * compound subtag.
     *
     * @param consumer the function to apply
     * @param keys the key to look up, or multiple keys forming a subtag path
     * @return true if the tag exists and was passed to the consumer; false otherwise
     */
    public boolean consumeBooleanNegated(Consumer<? super Boolean> consumer, String... keys) {
        // For a boolean, boxing carries no penalty, per
        // https://stackoverflow.com/questions/27698911/why-there-is-no-booleanconsumer-in-java-8
        return consumeObject(byteVal -> consumer.accept(byteVal == 0), ByteTag.class, keys);
    }

    /**
     * Applies the given function to a list subtag if it is present, converting it to a list of
     * values first.
     * Multiple strings can be passed in to operate on a sub-subtag, as with
     * {@link #tryGetCompound(String...)}, except that the last one must be a byte rather than
     * compound subtag.
     *
     * @param <T> the type to convert the list entries to
     * @param consumer the function to apply
     * @param type the type that the list entries must be
     * @param keys the key to look up, or multiple keys forming a subtag path
     * @return true if the tag exists and was passed to the consumer; false otherwise
     */
    public <T> boolean consumeList(Consumer<? super List<T>> consumer, TagType type,
            String... keys) {
        // Can't use consumeObject because of the list-element type check
        String lastKey = keys[keys.length - 1];
        String[] interveningKeys = Arrays.copyOf(keys, keys.length - 1);
        boolean[] consumed = {false};
        return consumeCompound(tag -> {
            if (tag.isList(lastKey, type)) {
                consumer.accept(tag.getList(lastKey, type));
                consumed[0] = true;
            }
        }, (String[]) interveningKeys) && consumed[0];
    }

    /**
     * Applies the given function to a list subtag if it is present and its contents are compound
     * tags. Processes the list as a single object; to process each tag separately, instead use
     * {@link #iterateCompoundList(Consumer, String...)}.
     * Multiple strings can be passed in to operate on a sub-subtag, as with
     * {@link #tryGetCompound(String...)}, except that the last one must be a byte rather than
     * compound subtag.
     *
     * @param consumer the function to apply
     * @param keys the key to look up, or multiple keys forming a subtag path
     * @return true if the tag exists and was passed to the consumer; false otherwise
     */
    public boolean consumeCompoundList(Consumer<? super List<CompoundTag>> consumer,
            String... keys) {
        return consumeList(consumer, TagType.COMPOUND, keys);
    }

    /**
     * Applies the given function to each compound tag in a compound-list subtag, if that tag
     * exists.
     * Multiple strings can be passed in to operate on a sub-subtag, as with
     * {@link #tryGetCompound(String...)}, except that the last one must be a byte rather than
     * compound subtag.
     *
     * @param consumer the function to apply
     * @param keys the key to look up, or multiple keys forming a subtag path
     * @return true if the tag exists and was passed to the consumer; false otherwise
     */
    public boolean iterateCompoundList(Consumer<? super CompoundTag> consumer, String... keys) {
        return consumeCompoundList(compoundTags -> compoundTags.forEach(consumer), keys);
    }

    /**
     * Applies the given function to a list subtag if it is present and its contents are string
     * tags.
     * Multiple strings can be passed in to operate on a sub-subtag, as with
     * {@link #tryGetCompound(String...)}, except that the last one must be a byte rather than
     * compound subtag.
     *
     * @param consumer the function to apply
     * @param keys the key to look up, or multiple keys forming a subtag path
     * @return true if the tag exists and was passed to the consumer; false otherwise
     */
    public boolean consumeStringList(Consumer<? super List<String>> consumer, String... keys) {
        return consumeList(consumer, TagType.STRING, keys);
    }

    /**
     * Applies the given function to a string subtag if it is present. Multiple strings can be
     * passed in to operate on a sub-subtag, as with {@link #tryGetCompound(String...)}, except that
     * the last one must be a string rather than compound subtag.
     *
     * @param consumer the function to apply
     * @param keys the key to look up, or multiple keys forming a subtag path
     * @return true if the tag exists and was passed to the consumer; false otherwise
     */
    public boolean consumeString(Consumer<? super String> consumer, String... keys) {
        return consumeObject(consumer, StringTag.class, keys);
    }

    /**
     * Applies the given function to a UUID if it is present in the given pair of long subtags.
     * Unlike the other consume* functions, this one is not variadic and does not recurse into
     * sub-subtags.
     *
     * @param consumer the function to apply
     * @param keyMost the key to look up the high word of the UUID
     * @param keyLeast the key to look up the low word of the UUID
     * @return true if the tags exist and were passed to the consumer; false otherwise
     */
    public boolean consumeUuid(Consumer<? super UUID> consumer, String keyMost, String keyLeast) {
        if (isLong(keyMost) && isLong(keyLeast)) {
            consumer.accept(new UUID(getLong(keyMost), getLong(keyLeast)));
            return true;
        }
        return false;
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

