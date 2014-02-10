package net.glowstone.util.nbt;

/**
 * The {@code TAG_Int} tag.
 * @author Graham Edgecombe
 */
public final class IntTag extends Tag<Integer> {

    /**
     * The value.
     */
    private final int value;

    /**
     * Creates the tag.
     * @param name The name.
     * @param value The value.
     */
    public IntTag(String name, int value) {
        super(TagType.INT, name);
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }

}

