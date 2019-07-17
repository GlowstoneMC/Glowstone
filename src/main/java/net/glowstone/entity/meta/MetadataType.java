package net.glowstone.entity.meta;

import com.destroystokyo.paper.ParticleBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.glowstone.util.TextMessage;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;
import org.bukkit.util.EulerAngle;

/**
 * The types of values that entity metadata can contain.
 */
@RequiredArgsConstructor
public enum MetadataType {
    BYTE(Byte.class, false),
    INT(Integer.class, false),
    FLOAT(Float.class, false),
    STRING(String.class, false),
    CHAT(TextMessage.class, false),
    OPTCHAT(TextMessage.class, true),
    ITEM(ItemStack.class, false),
    BOOLEAN(Boolean.class, false),
    VECTOR(EulerAngle.class, false),
    POSITION(BlockVector.class, false),
    OPTPOSITION(BlockVector.class, true),
    DIRECTION(Integer.class, false),
    OPTUUID(UUID.class, true),
    BLOCKID(Integer.class, false),
    NBTTAG(CompoundTag.class, false),
    PARTICLE(ParticleBuilder.class, false);

    @Getter
    private final Class<?> dataType;
    @Getter
    private final boolean optional;

    public static MetadataType byId(int id) {
        return values()[id];
    }
    public static MetadataType byClass(Class<?> clazz) {
        for (MetadataType type : MetadataType.values()) {
            if (type.getDataType() == clazz) {
                return type;
            }
        }
        return null;
    }

    public int getId() {
        return ordinal();
    }
}
