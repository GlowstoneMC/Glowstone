package net.glowstone.util.nbt;

/**
 * The {@code TAG_Short} tag.
 */
public final class ShortTag extends Tag<Short> {

    /**
     * The value.
     */
    private final short value;

    /**
     * Creates the tag.
     *
     * @param value The value.
     */
    public ShortTag(short value) {
        super(TagType.SHORT);
        this.value = value;
    }

    @Override
    public Short getValue() {
        return value;
    }
}

