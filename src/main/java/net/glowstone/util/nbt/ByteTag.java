package net.glowstone.util.nbt;

/**
 * The {@code TAG_Byte} tag.
 */
public class ByteTag extends Tag<Byte> {

    /**
     * The value.
     */
    private final byte value;

    /**
     * Creates the tag.
     *
     * @param value The value.
     */
    public ByteTag(byte value) {
        super(TagType.BYTE);
        this.value = value;
    }

    @Override
    public Byte getValue() {
        return value;
    }
}

