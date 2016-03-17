package net.glowstone.util.nbt;

import static net.glowstone.util.mojangson.MojangsonToken.*;

/**
 * The {@code TAG_Byte_Array} tag.
 */
public final class ByteArrayTag extends Tag<byte[]> {

    private static final int C_ARRAY_START = 0;   // Parsing context
    private static final int C_ARRAY_ELEMENT = 1; // Parsing context

    /**
     * The value.
     */
    private final byte[] value;

    /**
     * Creates the tag.
     *
     * @param value The value.
     */
    public ByteArrayTag(byte... value) {
        super(TagType.BYTE_ARRAY);
        this.value = value;
    }

    @Override
    public byte[] getValue() {
        return value;
    }

    @Override
    protected void valueToString(StringBuilder hex) {
        for (byte b : value) {
            String hexDigits = Integer.toHexString(b & 0xff);
            if (hexDigits.length() == 1) {
                hex.append("0");
            }
            hex.append(hexDigits).append(" ");
        }
    }

    @Override
    public String toMojangson() {
        StringBuilder builder = new StringBuilder();
        builder.append(ARRAY_START);
        boolean start = true;

        for (byte value : this.getValue()) {
            ByteTag tag = new ByteTag(value);
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

