package net.glowstone.util.nbt;

/**
 * The {@code TAG_Long} tag.
 * @author Graham Edgecombe
 */
public final class LongTag extends Tag {

    /**
     * The value.
     */
    private final long value;

    /**
     * Creates the tag.
     * @param name The name.
     * @param value The value.
     */
    public LongTag(String name, long value) {
        super(name);
        this.value = value;
    }

    @Override
    public Long getValue() {
        return value;
    }

    @Override
    public String toString() {
        String name = getName();
        String append = "";
        if (name != null && !name.equals("")) {
            append = "(\"" + this.getName() + "\")";
        }
        return "TAG_Long" + append + ": " + value;
    }

}

