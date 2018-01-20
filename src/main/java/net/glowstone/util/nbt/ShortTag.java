package net.glowstone.util.nbt;

import lombok.Getter;

/**
 * The {@code TAG_Short} tag.
 */
public final class ShortTag extends Tag<Short> {

    /**
     * The value.
     */
    @Getter
    private final short value;

    /**
     * Creates the tag.
     *
     * @param value The value.
     */
    public ShortTag(short value) {
        super(TagType.SHORT);
        this.value = value;
    }
}

