package net.glowstone.util.nbt;

/**
 * The {@code TAG_Double} tag.
 */
public final class DoubleTag extends NumericTag<Double> {

    /**
     * The value.
     */
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

    @Override
    public Double getValue() {
        return value;
    }
}

