package net.glowstone.entity.meta;

import org.bukkit.entity.*;

import static net.glowstone.entity.meta.MetadataType.*;

/**
 * Index constants for entity metadata.
 */
public enum MetadataIndex {

    STATUS(0, BYTE, Entity.class),
    AIR_TIME(1, SHORT, Entity.class),

    HEALTH(6, FLOAT, LivingEntity.class),
    POTION_COLOR(7, INT, LivingEntity.class),
    POTION_AMBIENT(8, BYTE, LivingEntity.class),
    ARROW_COUNT(9, BYTE, LivingEntity.class),
    NAME_TAG(10, STRING, LivingEntity.class),
    SHOW_NAME_TAG(11, BYTE, LivingEntity.class),

    AGE(12, INT, Ageable.class),

    HORSE_FLAGS(16, INT, Horse.class),
    HORSE_TYPE(19, BYTE, Horse.class),
    HORSE_STYLE(20, INT, Horse.class),
    HORSE_OWNER(21, STRING, Horse.class),
    HORSE_ARMOR(22, INT, Horse.class),

    BAT_HANGING(16, BYTE, Bat.class),

    TAME_FLAGS(16, BYTE, Tameable.class),
    TAME_OWNER(17, STRING, Tameable.class),

    OCELOT_TYPE(18, BYTE, Ocelot.class),

    WOLF_FLAGS(16, BYTE, Wolf.class),
    WOLF_HEALTH(18, FLOAT, Wolf.class),
    WOLF_BEGGING(19, BYTE, Wolf.class),
    WOLF_COLOR(20, BYTE, Wolf.class),

    // todo: more
    ;

    private final int index;
    private final MetadataType type;
    private final Class<?> appliesTo;

    private MetadataIndex(int index, MetadataType type, Class<?> appliesTo) {
        this.index = index;
        this.type = type;
        this.appliesTo = appliesTo;
    }

    public int getIndex() {
        return index;
    }

    public MetadataType getType() {
        return type;
    }

    public Class<?> getAppliesTo() {
        return appliesTo;
    }

    public boolean appliesTo(Class<? extends Entity> clazz) {
        return appliesTo.isAssignableFrom(clazz);
    }
}
