package net.glowstone.util.nbt;

/**
 * The {@code TAG_String} tag.
 * @author Graham Edgecombe
 */
public final class StringTag extends Tag {

    /**
     * The value.
     */
    private final String value;

    /**
     * Creates the tag.
     * @param name The name.
     * @param value The value.
     */
    public StringTag(String name, String value) {
        super(name);
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        String name = getName();
        String append = "";
        if (name != null && !name.equals("")) {
            append = "(\"" + this.getName() + "\")";
        }
        return "TAG_String" + append + ": " + value;
    }

}

