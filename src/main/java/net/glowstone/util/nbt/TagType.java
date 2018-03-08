package net.glowstone.util.nbt;

import java.io.IOException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The types of NBT tags that exist.
 */
@Getter
@RequiredArgsConstructor
public enum TagType {

    END("End"),
    BYTE("Byte"),
    SHORT("Short"),
    INT("Int"),
    LONG("Long"),
    FLOAT("Float"),
    DOUBLE("Double"),
    BYTE_ARRAY("Byte_Array"),
    STRING("String"),
    LIST("List"),
    COMPOUND("Compound"),
    INT_ARRAY("Int_Array");

    private final String name;

    /**
     * Returns the tag type with a given ID.
     *
     * @param id the ID to look up
     * @return the tag type with ID {@code id}, or null if none exists
     */
    public static TagType byId(int id) {
        if (id < 0 || id >= values().length) {
            return null;
        }
        return values()[id];
    }

    /**
     * Returns the tag type with a given ID.
     *
     * @param id the ID to look up
     * @return the tag type with ID {@code id}
     * @throws IOException if {@code id} doesn't match any tag type
     */
    static TagType byIdOrError(int id) throws IOException {
        if (id < 0 || id >= values().length) {
            throw new IOException("Invalid tag type: " + id);
        }
        return values()[id];
    }

    public byte getId() {
        return (byte) ordinal();
    }

}
