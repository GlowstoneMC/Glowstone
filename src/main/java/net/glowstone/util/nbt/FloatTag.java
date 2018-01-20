package net.glowstone.util.nbt;

import lombok.Getter;

/**
 * The {@code TAG_Float} tag.
 */
public final class FloatTag extends Tag<Float> {

    /**
     * The value.
     */
    @Getter
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
}

