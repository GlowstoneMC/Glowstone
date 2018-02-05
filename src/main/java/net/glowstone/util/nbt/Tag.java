package net.glowstone.util.nbt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents a single NBT tag.
 */
@RequiredArgsConstructor
public abstract class Tag<T> {

    /**
     * The type of this tag.
     */
    @Getter
    private final TagType type;

    /**
     * Gets the value of this tag.
     *
     * @return The value of this tag.
     */
    public abstract T getValue();

    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder("TAG_");
        builder.append(type.getName()).append(": ");
        valueToString(builder);
        return builder.toString();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Tag tag = (Tag) o;

        return type == tag.type && getValue().equals(tag.getValue());
    }

    @Override
    public final int hashCode() {
        int result = type.hashCode();
        result = 31 * result + getValue().hashCode();
        return result;
    }

    protected void valueToString(StringBuilder builder) {
        builder.append(getValue());
    }
}

