package net.glowstone.util.nbt;

import lombok.Getter;

/**
 * The {@code TAG_Double} tag.
 */
public final class DoubleTag extends Tag<Double> {

    /**
     * The value.
     */
    @Getter
    private final double value;

    /**
     * Creates the tag.
     *
     * @param value The value.
     */
    public DoubleTag(double value) {
        super(TagType.DOUBLE);
        this.value = value;
    }
}

