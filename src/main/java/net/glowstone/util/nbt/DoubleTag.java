package net.glowstone.util.nbt;

import net.glowstone.util.mojangson.MojangsonToken;

/**
 * The {@code TAG_Double} tag.
 */
public final class DoubleTag extends Tag<Double> {

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

    @Override
    public String toMojangson() {
        StringBuilder builder = new StringBuilder();
        builder.append(value).append(MojangsonToken.DOUBLE_SUFFIX);
        return builder.toString();
    }
}

