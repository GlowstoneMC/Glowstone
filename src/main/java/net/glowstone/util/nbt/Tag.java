package net.glowstone.util.nbt;

/**
 * Represents a single NBT tag.
 * @author Graham Edgecombe
 */
public abstract class Tag<T> {

    /**
     * The type of this tag.
     */
    private final TagType type;

    /**
     * The name of this tag.
     */
    private final String name;

    /**
     * Creates the tag with the specified type and name.
     * @param type The type.
     * @param name The name.
     */
    protected Tag(TagType type, String name) {
        this.type = type;
        this.name = name;
    }

    /**
     * Gets the type of this tag.
     * @return The type of this tag.
     */
    public final TagType getType() {
        return type;
    }

    /**
     * Gets the name of this tag.
     * @return The name of this tag.
     */
    public final String getName() {
        return name;
    }

    /**
     * Gets the value of this tag.
     * @return The value of this tag.
     */
    public abstract T getValue();

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("TAG_");
        builder.append(type.getName());
        String name = getName();
        if (name != null && !name.equals("")) {
            builder.append("(\"").append(name).append("\")");
        }
        builder.append(": ");
        valueToString(builder);
        return builder.toString();
    }

    protected void valueToString(StringBuilder builder) {
        builder.append(getValue());
    }
}

