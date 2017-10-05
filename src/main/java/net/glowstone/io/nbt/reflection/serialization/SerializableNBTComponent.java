package net.glowstone.io.nbt.reflection.serialization;

import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.Tag;
import net.glowstone.util.nbt.TagType;

/**
 * Represents a serializable NBT component (generally an NBT Compound tag) that can be read and written.
 *
 * @param <T> the type of value that should be passed when serializing and de-serializing an object from an NBT tag.
 */
public abstract class SerializableNBTComponent<T> {

    public SerializableNBTComponent() {
    }

    /**
     * Reads an object value from an NBT tag.
     *
     * @param tag the source NBT tag
     * @return the object value
     */
    public abstract T read(Tag tag);

    /**
     * Save the data from the object to an NBT compound tag.
     *
     * @param tagName    the name of the tag the object should be saved into.
     * @param objectType the type of object
     * @param object     the object to save the information from
     * @param tag        the target NBT compound tag
     */
    public abstract void write(String tagName, TagType objectType, T object, CompoundTag tag);
}
