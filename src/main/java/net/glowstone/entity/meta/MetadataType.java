package net.glowstone.entity.meta;

import net.glowstone.util.Position;
import net.glowstone.util.TextMessage;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

/**
 * The types of values that entity metadata can contain.
 */
public enum MetadataType {
    BYTE(Byte.class),
    INT(Integer.class),
    FLOAT(Float.class),
    STRING(String.class),
    CHAT(TextMessage.class),
    ITEM(ItemStack.class),
    BOOLEAN(Boolean.class),
    VECTOR(EulerAngle.class),
    POSITION(Position.class),
    OPTPOSITION(Position.class),
    DIRECTION(Integer.class),
    OPTUUID(java.util.UUID.class),
    BLOCKID(Integer.class);

    private final Class<?> dataType;

    private MetadataType(Class<?> dataType) {
        this.dataType = dataType;
    }

    public Class<?> getDataType() {
        return dataType;
    }

    public int getId() {
        return ordinal();
    }

    public static MetadataType byId(int id) {
        return values()[id];
    }
}
