package net.glowstone.util.nbt;

/**
 * The {@code TAG_Int} tag.
 */
public final class IntTag extends NumericTag<Integer> {

    /**
     * The value.
     */
    private final int value;

    /**
     * Creates the tag.
     *
     * @param value The value.
     */
    public IntTag(int value) {
        super(TagType.INT);
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}

