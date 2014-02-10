package net.glowstone.util.nbt;

/**
 * The {@code TAG_Float} tag.
 * @author Graham Edgecombe
 */
public final class FloatTag extends Tag<Float> {

    /**
     * The value.
     */
    private final float value;

    /**
     * Creates the tag.
     * @param name The name.
     * @param value The value.
     */
    public FloatTag(String name, float value) {
        super(TagType.FLOAT, name);
        this.value = value;
    }

    @Override
    public Float getValue() {
        return value;
    }

}

