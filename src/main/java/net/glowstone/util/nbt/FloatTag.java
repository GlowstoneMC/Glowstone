package net.glowstone.util.nbt;

/**
 * The {@code TAG_Float} tag.
 */
public final class FloatTag extends Tag<Float> {

    /**
     * The value.
     */
    private final float value;

    /**
     * Creates the tag.
     *
     * @param value The value.
     */
    public FloatTag(float value) {
        super(TagType.FLOAT);
        this.value = value;
    }

    @Override
    public Float getValue() {
        return value;
    }
}

