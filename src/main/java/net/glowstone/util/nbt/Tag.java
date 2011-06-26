package net.glowstone.util.nbt;

/**
 * Represents a single NBT tag.
 * @author Graham Edgecombe
 */
public abstract class Tag {

    /**
     * The name of this tag.
     */
    private final String name;

    /**
     * Creates the tag with no name.
     */
    public Tag() {
        this("");
    }

    /**
     * Creates the tag with the specified name.
     * @param name The name.
     */
    public Tag(String name) {
        this.name = name;
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
    public abstract Object getValue();

}

