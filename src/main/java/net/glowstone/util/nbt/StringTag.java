package net.glowstone.util.nbt;

import net.glowstone.util.mojangson.MojangsonToken;

/**
 * The {@code TAG_String} tag.
 */
public final class StringTag extends Tag<String> {

    /**
     * The value.
     */
    private final String value;

    /**
     * Creates the tag.
     *
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

    @Override
    public String toMojangson() {
        StringBuilder builder = new StringBuilder();
        builder.append(MojangsonToken.STRING_QUOTES).append(value).append(MojangsonToken.STRING_QUOTES);
        return builder.toString();
    }

}

