package net.glowstone.util.nbt;

import java.util.Collections;
import java.util.List;

/**
 * The {@code TAG_List} tag.
 * @author Graham Edgecombe
 */
public final class ListTag<T extends Tag> extends Tag<List<T>> {

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
     * @param name The name.
     * @param type The type of item in the list.
     * @param value The value.
     */
    public ListTag(String name, TagType type, List<T> value) {
        super(TagType.LIST, name);
        this.type = type;
        this.value = Collections.unmodifiableList(value);
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
        bldr.append(value.size()).append(" entries of type ").append(type.getName()).append("\r\n{\r\n");
        for (Tag t : value) {
            bldr.append("    ").append(t.toString().replaceAll("\r\n", "\r\n    ")).append("\r\n");
        }
        bldr.append("}");
    }

}

