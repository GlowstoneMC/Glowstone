package net.glowstone.io.entity;

import lombok.Getter;
import net.glowstone.i18n.GlowstoneMessages;
import net.glowstone.util.nbt.CompoundTag;

/**
 * Thrown when attempting to load an entity of an unrecognized or unspecified type. Needs a separate
 * exception class because it has different logging messages than other
 * {@link IllegalArgumentException} instances.
 */
public class UnknownEntityTypeException extends IllegalArgumentException {
    public UnknownEntityTypeException(CompoundTag nbt) {
        super(getMessage(nbt));
        this.nbt = nbt;
    }

    @Getter
    private final CompoundTag nbt;

    private static String getMessage(CompoundTag nbt) {
        if (nbt.isString("id")) {
            return GlowstoneMessages.Entity.UNKNOWN_TYPE_WITH_ID.get(nbt.getString("id"));
        } else {
            return GlowstoneMessages.Entity.UNKNOWN_TYPE_NO_ID.get(nbt);
        }
    }

    /**
     * Returns the ID subtag for this entity, if it has one that's a string; otherwise, returns the
     * entire NBT tag as a string.
     *
     * @return the ID, or the entire NBT tag as a string if no ID is defined
     */
    public String getIdOrTag() {
        return nbt.isString("id") ? nbt.getString("id") : nbt.toString();
    }

}
