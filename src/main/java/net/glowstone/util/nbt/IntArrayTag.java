package net.glowstone.util.nbt;

/**
 * The {@code TAG_Int_Array} tag.
 */
final class IntArrayTag extends Tag<int[]> {

    /**
     * The value.
     */
    private final int[] value;

    /**
     * Creates the tag.
     * @param value The value.
     */
    public IntArrayTag(int[] value) {
        super(TagType.INT_ARRAY);
        this.value = value;
    }

    @Override
    public int[] getValue() {
        return value;
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

