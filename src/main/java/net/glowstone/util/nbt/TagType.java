package net.glowstone.util.nbt;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * The types of NBT tags that exist.
 */
public enum TagType {

    END("End", EndTag.class, Void.class),
    BYTE("Byte", ByteTag.class, Byte.class),
    SHORT("Short", ShortTag.class, Short.class),
    INT("Int", IntTag.class, Integer.class),
    LONG("Long", LongTag.class, Long.class),
    FLOAT("Float", FloatTag.class, Float.class),
    DOUBLE("Double", DoubleTag.class, Double.class),
    BYTE_ARRAY("Byte_Array", ByteArrayTag.class, byte[].class),
    STRING("String", StringTag.class, String.class),
    LIST("List", ListTag.class, List.class),
    COMPOUND("Compound", CompoundTag.class, Map.class),
    INT_ARRAY("Int_Array", IntArrayTag.class, int[].class);

    private final String name;
    private final Class<? extends Tag> tagClass;
    private final Class<?> valueClass;

    private <V, T extends Tag<? extends V>> TagType(String name, Class<T> tagClass, Class<V> valueClass) {
        // ? extends V is needed to get Compound to work for some reason
        this.name = name;
        this.tagClass = tagClass;
        this.valueClass = valueClass;
    }

    public byte getId() {
        return (byte) ordinal();
    }

    public String getName() {
        return name;
    }

    public Class<? extends Tag> getTagClass() {
        return tagClass;
    }

    public Class<?> getValueClass() {
        return valueClass;
    }

    public static TagType byId(int id) {
        if (id < 0 || id >= values().length) return null;
        return values()[id];
    }

    static TagType byIdOrError(int id) throws IOException {
        if (id < 0 || id >= values().length) throw new IOException("Invalid tag type: " + id);
        return values()[id];
    }
}
