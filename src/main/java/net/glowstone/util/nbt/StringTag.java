package net.glowstone.util.nbt;

/**
 * The {@code TAG_String} tag.
 */
final class StringTag extends Tag<String> {

    /**
     * The value.
     */
    private final String value;

    /**
     * Creates the tag.
     * @param value The value.
     */
    public StringTag(String value) {
        super(TagType.STRING);
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

}

