package net.glowstone.util.nbt;

/**
 * The {@code TAG_Long} tag.
 * @author Graham Edgecombe
 */
public final class LongTag extends Tag<Long> {

    /**
     * The value.
     */
    private final long value;

    /**
     * Creates the tag.
     * @param name The name.
     * @param value The value.
     */
    public LongTag(String name, long value) {
        super(TagType.LONG, name);
        this.value = value;
    }

    @Override
    public Long getValue() {
        return value;
    }

}

