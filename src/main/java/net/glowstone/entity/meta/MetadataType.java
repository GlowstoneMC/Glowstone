package net.glowstone.entity.meta;

import net.glowstone.util.TextMessage;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;
import org.bukkit.util.EulerAngle;

import java.util.UUID;

/**
 * The types of values that entity metadata can contain.
 */
public enum MetadataType {
    BYTE(Byte.class, false),
    INT(Integer.class, false),
    FLOAT(Float.class, false),
    STRING(String.class, false),
    CHAT(TextMessage.class, false),
    ITEM(ItemStack.class, false),
    BOOLEAN(Boolean.class, false),
    VECTOR(EulerAngle.class, false),
    POSITION(BlockVector.class, false),
    OPTPOSITION(BlockVector.class, true),
    DIRECTION(Integer.class, false),
    OPTUUID(UUID.class, true),
    BLOCKID(Integer.class, false);

    private final Class<?> dataType;
    private final boolean optional;

    MetadataType(Class<?> dataType, boolean optional) {
        this.dataType = dataType;
        this.optional = optional;
    }

    public static MetadataType byId(int id) {
        return values()[id];
    }

    public Class<?> getDataType() {
        return dataType;
    }

    public int getId() {
        return ordinal();
    }

    public boolean isOptional() {
        return optional;
    }
}
