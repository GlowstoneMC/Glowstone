package net.glowstone.util.nbt;

/**
 * The {@code TAG_Byte_Array} tag.
 * @author Graham Edgecombe
 */
public final class ByteArrayTag extends Tag<byte[]> {

    /**
     * The value.
     */
    private final byte[] value;

    /**
     * Creates the tag.
     * @param name The name.
     * @param value The value.
     */
    public ByteArrayTag(String name, byte[] value) {
        super(TagType.BYTE_ARRAY, name);
        this.value = value;
    }

    @Override
    public byte[] getValue() {
        return value;
    }

    @Override
    protected void valueToString(StringBuilder hex) {
        for (byte b : value) {
            String hexDigits = Integer.toHexString(b).toUpperCase();
            if (hexDigits.length() == 1) {
                hex.append("0");
            }
            hex.append(hexDigits).append(" ");
        }
    }

}

