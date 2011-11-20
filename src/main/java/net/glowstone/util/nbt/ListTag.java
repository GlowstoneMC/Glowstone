package net.glowstone.util.nbt;

import java.util.Collections;
import java.util.List;

/**
 * The {@code TAG_List} tag.
 * @author Graham Edgecombe
 */
public final class ListTag<T extends Tag> extends Tag {

    /**
     * The type of entries within this list.
     */
    private final Class<T> type;

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
    public ListTag(String name, Class<T> type, List<T> value) {
        super(name);
        this.type = type;
        this.value = Collections.unmodifiableList(value);
    }

    /**
     * Gets the type of item in this list.
     * @return The type of item in this list.
     */
    public Class<T> getType() {
        return type;
    }

    @Override
    public List<T> getValue() {
        return value;
    }

    @Override
    public String toString() {
        String name = getName();
        String append = "";
        if (name != null && !name.equals("")) {
            append = "(\"" + this.getName() + "\")";
        }

        StringBuilder bldr = new StringBuilder();
        bldr.append("TAG_List").append(append).append(": ").append(value.size()).append(" entries of type ").append(NBTUtils.getTypeName(type)).append("\r\n{\r\n");
        for (Tag t : value) {
            bldr.append("   ").append(t.toString().replaceAll("\r\n", "\r\n   ")).append("\r\n");
        }
        bldr.append("}");
        return bldr.toString();
    }

}

