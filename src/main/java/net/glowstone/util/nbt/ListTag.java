package net.glowstone.util.nbt;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code TAG_List} tag.
 */
final class ListTag<T extends Tag> extends Tag<List<T>> {

    /**
     * The type of entries within this list.
     */
    private final TagType type;

    /**
     * The value.
     */
    private final List<T> value;

    /**
     * Creates the tag.
     * @param type The type of item in the list.
     * @param value The value.
     */
    public ListTag(TagType type, List<T> value) {
        super(TagType.LIST);
        this.type = type;
        this.value = new ArrayList<>(value); // modifying list should not modify tag

        // ensure type of objects in list matches tag type
        for (Tag elem : value) {
            if (type != elem.getType()) {
                throw new IllegalArgumentException("ListTag(" + type + ") cannot hold tags of type " + elem.getType());
            }
        }
    }

    /**
     * Gets the type of item in this list.
     * @return The type of item in this list.
     */
    public TagType getChildType() {
        return type;
    }

    @Override
    public List<T> getValue() {
        return value;
    }

    @Override
    protected void valueToString(StringBuilder bldr) {
        bldr.append(value.size()).append(" entries of type ").append(type.getName()).append("\n{\n");
        for (T elem : value) {
            bldr.append("    ").append(elem.toString().replaceAll("\n", "\n    ")).append("\n");
        }
        bldr.append("}");
    }

}

