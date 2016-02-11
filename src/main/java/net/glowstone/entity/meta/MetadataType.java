package net.glowstone.entity.meta;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

/**
 * The types of values that entity metadata can contain.
 */
public enum MetadataType {
    BYTE(Byte.class),
    SHORT(Short.class),
    INT(Integer.class),
    FLOAT(Float.class),
    STRING(String.class),
    ITEM(ItemStack.class),
    VECTOR(Vector.class),
    EULER_ANGLE(EulerAngle.class);

    private final Class<?> dataType;

    MetadataType(Class<?> dataType) {
        this.dataType = dataType;
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
}
