package net.glowstone.util.nbt;

/**
 * The {@code TAG_Double} tag.
 * @author Graham Edgecombe
 */
public final class DoubleTag extends Tag<Double> {

    /**
     * The value.
     */
    private final double value;

    /**
     * Creates the tag.
     * @param name The name.
     * @param value The value.
     */
    public DoubleTag(String name, double value) {
        super(TagType.DOUBLE, name);
        this.value = value;
    }

    @Override
    public Double getValue() {
        return value;
    }

}

