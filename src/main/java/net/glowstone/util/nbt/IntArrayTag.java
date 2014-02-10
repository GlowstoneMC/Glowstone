package net.glowstone.util.nbt;

/**
 * The {@code TAG_Int_Array} tag.
 */
public final class IntArrayTag extends Tag<int[]> {

    /**
     * The value.
     */
    private final int[] value;

    /**
     * Creates the tag.
     * @param name The name.
     * @param value The value.
     */
    public IntArrayTag(String name, int[] value) {
        super(TagType.INT_ARRAY, name);
        this.value = value;
    }

    @Override
    public int[] getValue() {
        return value;
    }

    @Override
    protected void valueToString(StringBuilder hex) {
        for (int b : value) {
            String hexDigits = Integer.toHexString(b).toUpperCase();
            if (hexDigits.length() == 1) {
                hex.append("0");
            }
            hex.append(hexDigits).append(" ");
        }
    }
}

