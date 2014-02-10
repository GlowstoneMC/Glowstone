package net.glowstone.util.nbt;

/**
 * The {@code TAG_Short} tag.
 * @author Graham Edgecombe
 */
public final class ShortTag extends Tag<Short> {

    /**
     * The value.
     */
    private final short value;

    /**
     * Creates the tag.
     * @param name The name.
     * @param value The value.
     */
    public ShortTag(String name, short value) {
        super(TagType.SHORT, name);
        this.value = value;
    }

    @Override
    public Short getValue() {
        return value;
    }

}

