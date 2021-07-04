package net.glowstone.util.nbt;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code TAG_List} tag.
 */
public final class ListTag<T extends Tag> extends Tag<List<T>> {

    /**
     * The type of entries within this list.
     *
     * @return The type of item in this list.
     */
    @Getter
    private final TagType childType;

    /**
     * The value.
     */
    private final List<T> value;

    /**
     * Creates the tag.
     *
     * @param childType The type of item in the list.
     * @param value The value.
     */
    public ListTag(TagType childType, List<T> value) {
        super(TagType.LIST);
        this.childType = childType;
        this.value = new ArrayList<>(value); // modifying list should not modify tag

        // ensure type of objects in list matches tag type
        for (Tag elem : value) {
            if (childType != elem.getType()) {
                throw new IllegalArgumentException(
                    "ListTag(" + childType + ") cannot hold tags of type " + elem.getType());
            }
        }
    }

    @Override
    public List<T> getValue() {
        return value;
    }

    @Override
    protected void valueToString(StringBuilder builder) {
        builder.append(value.size()).append(" entries of type ").append(childType.getName())
            .append("\n{\n");
        for (T elem : value) {
            builder.append("    ").append(elem.toString().replaceAll("\n", "\n    ")).append("\n");
        }
        builder.append("}");
    }
}

