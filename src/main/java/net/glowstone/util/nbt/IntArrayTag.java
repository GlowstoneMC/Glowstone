package net.glowstone.util.nbt;

import lombok.Getter;

/**
 * The {@code TAG_Int_Array} tag.
 */
public final class IntArrayTag extends Tag<int[]> {

    /**
     * The value.
     */
    @Getter
    private final int[] value;

    /**
     * Creates the tag.
     *
     * @param value The value.
     */
    public IntArrayTag(int... value) {
        super(TagType.INT_ARRAY);
        this.value = value;
    }

    @Override
    protected void valueToString(StringBuilder hex) {
        for (int b : value) {
            String hexDigits = Integer.toHexString(b);
            hex.append("00000000", hexDigits.length(), 8);
            hex.append(hexDigits).append(" ");
        }
    }
}

