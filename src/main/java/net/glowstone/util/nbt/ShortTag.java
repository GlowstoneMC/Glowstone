package net.glowstone.util.nbt;

/**
 * The {@code TAG_Short} tag.
 * @author Graham Edgecombe
 */
public final class ShortTag extends Tag {

    /**
     * The value.
     */
    private final short value;

    /**
     * Creates the tag.
     * @param name The name.
     * @param value The value.
     */
    public ShortTag(String name, short value) {
        super(name);
        this.value = value;
    }

    @Override
    public Short getValue() {
        return value;
    }

    @Override
    public String toString() {
        String name = getName();
        String append = "";
        if (name != null && !name.equals("")) {
            append = "(\"" + this.getName() + "\")";
        }
        return "TAG_Short" + append + ": " + value;
    }

}

