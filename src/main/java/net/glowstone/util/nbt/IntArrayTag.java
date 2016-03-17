package net.glowstone.util.nbt;

import static net.glowstone.util.mojangson.MojangsonToken.*;

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
     *
     * @param value The value.
     */
    public IntArrayTag(int... value) {
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

    @Override
    public String toMojangson() {
        StringBuilder builder = new StringBuilder();
        builder.append(ARRAY_START);
        boolean start = true;

        for (int value : this.getValue()) {
            IntTag tag = new IntTag(value);
            if (start) {
                start = false;
            } else {
                builder.append(ELEMENT_SEPERATOR);
            }
            builder.append(tag.toMojangson());
        }
        builder.append(ARRAY_END);
        return builder.toString();
    }
}

