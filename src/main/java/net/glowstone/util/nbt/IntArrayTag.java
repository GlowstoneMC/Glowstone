package net.glowstone.util.nbt;

/**
 * The {@code TAG_Int_Array} tag.
 */
public final class IntArrayTag extends Tag {

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
        super(name);
        this.value = value;
    }

    @Override
    public int[] getValue() {
        return value;
    }

    @Override
    public String toString() {
        StringBuilder hex = new StringBuilder();
        for (int b : value) {
            String hexDigits = Integer.toHexString(b).toUpperCase();
            if (hexDigits.length() == 1) {
                hex.append("0");
            }
            hex.append(hexDigits).append(" ");
        }

        String name = getName();
        String append = "";
        if (name != null && !name.equals("")) {
            append = "(\"" + this.getName() + "\")";
        }
        return "TAG_Int_Array" + append + ": " + hex.toString();
    }

}

