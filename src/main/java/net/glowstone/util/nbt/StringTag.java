package net.glowstone.util.nbt;

import lombok.Getter;

/**
 * The {@code TAG_String} tag.
 */
public final class StringTag extends Tag<String> {

    /**
     * The value.
     */
    @Getter
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
}

