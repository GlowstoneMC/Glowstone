package net.glowstone.util.nbt;

import lombok.Getter;

/**
 * The {@code TAG_Byte_Array} tag.
 */
public final class ByteArrayTag extends Tag<byte[]> {

    private static final int C_ARRAY_START = 0;   // Parsing context
    private static final int C_ARRAY_ELEMENT = 1; // Parsing context

    /**
     * The value.
     */
    @Getter
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
    protected void valueToString(StringBuilder hex) {
        for (byte b : value) {
            String hexDigits = Integer.toHexString(b & 0xff);
            if (hexDigits.length() == 1) {
                hex.append("0");
            }
            hex.append(hexDigits).append(" ");
        }
    }
}

