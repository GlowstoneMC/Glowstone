package net.glowstone.util.nbt;

/**
 * The {@code TAG_Byte} tag.
 * @author Graham Edgecombe
 */
public final class ByteTag extends Tag<Byte> {

    /**
     * The value.
     */
    private final byte value;

    /**
     * Creates the tag.
     * @param name The name.
     * @param value The value.
     */
    public ByteTag(String name, byte value) {
        super(TagType.BYTE, name);
        this.value = value;
    }

    @Override
    public Byte getValue() {
        return value;
    }

}

