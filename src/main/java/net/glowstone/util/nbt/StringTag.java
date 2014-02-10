package net.glowstone.util.nbt;

/**
 * The {@code TAG_String} tag.
 * @author Graham Edgecombe
 */
public final class StringTag extends Tag<String> {

    /**
     * The value.
     */
    private final String value;

    /**
     * Creates the tag.
     * @param name The name.
     * @param value The value.
     */
    public StringTag(String name, String value) {
        super(TagType.STRING, name);
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

}

