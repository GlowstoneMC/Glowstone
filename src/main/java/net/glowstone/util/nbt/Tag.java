package net.glowstone.util.nbt;

/**
 * Represents a single NBT tag.
 */
public abstract class Tag<T> {

    /**
     * The type of this tag.
     */
    private final TagType type;

    /**
     * Creates the tag with the specified type.
     * @param type The type.
     */
    protected Tag(TagType type) {
        this.type = type;
    }

    /**
     * Gets the type of this tag.
     * @return The type of this tag.
     */
    public final TagType getType() {
        return type;
    }

    /**
     * Gets the value of this tag.
     * @return The value of this tag.
     */
    public abstract T getValue();

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("TAG_");
        builder.append(type.getName()).append(": ");
        valueToString(builder);
        return builder.toString();
    }

    protected void valueToString(StringBuilder builder) {
        builder.append(getValue());
    }
}

