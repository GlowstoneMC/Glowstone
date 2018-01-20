package net.glowstone.util.nbt;

import lombok.Getter;

/**
 * The {@code TAG_Long} tag.
 */
public final class LongTag extends Tag<Long> {

    /**
     * The value.
     */
    @Getter
    private final long value;

    /**
     * Creates the tag.
     *
     * @param value The value.
     */
    public LongTag(long value) {
        super(TagType.LONG);
        this.value = value;
    }
}

