package net.glowstone.util.nbt;

import lombok.Getter;

/**
 * The {@code TAG_Int_Array} tag.
 */
public final class LongArrayTag extends Tag<long[]> {

    /**
     * The value.
     */
    @Getter
    private final long[] value;

    /**
     * Creates the tag.
     *
     * @param value The value.
     */
    public LongArrayTag(long... value) {
        super(TagType.LONG_ARRAY);
        this.value = value;
    }

    @Override
    protected void valueToString(StringBuilder hex) {
        for (long b : value) {
            String hexDigits = Long.toHexString(b);
            hex.append("0000000000000000", hexDigits.length(), 16);
            hex.append(hexDigits).append(" ");
        }
    }
}

