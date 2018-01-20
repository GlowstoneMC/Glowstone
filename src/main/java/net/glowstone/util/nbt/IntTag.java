package net.glowstone.util.nbt;

import lombok.Getter;

/**
 * The {@code TAG_Int} tag.
 */
public final class IntTag extends Tag<Integer> {

    /**
     * The value.
     */
    @Getter
    private final int value;

    /**
     * Creates the tag.
     *
     * @param value The value.
     */
    public IntTag(int value) {
        super(TagType.INT);
        this.value = value;
    }
}

