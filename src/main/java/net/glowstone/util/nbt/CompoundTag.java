package net.glowstone.util.nbt;

import java.util.Collections;
import java.util.Map;

/**
 * The {@code TAG_Compound} tag.
 * @author Graham Edgecombe
 */
public final class CompoundTag extends Tag {

    /**
     * The value.
     */
    private final Map<String, Tag> value;

    /**
     * Creates the tag.
     * @param name The name.
     * @param value The value.
     */
    public CompoundTag(String name, Map<String, Tag> value) {
        super(name);
        this.value = Collections.unmodifiableMap(value);
    }

    @Override
    public Map<String, Tag> getValue() {
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
        bldr.append("TAG_Compound").append(append).append(": ").append(value.size()).append(" entries\r\n{\r\n");
        for (Map.Entry<String, Tag> entry : value.entrySet()) {
            bldr.append("   ").append(entry.getValue().toString().replaceAll("\r\n", "\r\n   ")).append("\r\n");
        }
        bldr.append("}");
        return bldr.toString();
    }

}

