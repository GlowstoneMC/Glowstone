package net.glowstone.entity.meta;

import org.bukkit.entity.*;
import org.bukkit.entity.minecart.PoweredMinecart;

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

    // allowed to override NAME_TAG from LivingEntity
    PLAYER_SKIN_FLAGS(10, BYTE, HumanEntity.class),

    AGE(12, INT, Ageable.class),

    HORSE_FLAGS(16, INT, Horse.class),
    HORSE_TYPE(19, BYTE, Horse.class),
    HORSE_STYLE(20, INT, Horse.class),
    HORSE_OWNER(21, STRING, Horse.class),
    HORSE_ARMOR(22, INT, Horse.class),

    BAT_HANGING(16, BYTE, Bat.class),

    OCELOT_FLAGS(16, BYTE, Ocelot.class),
    OCELOT_OWNER(17, STRING, Ocelot.class),
    OCELOT_TYPE(18, BYTE, Ocelot.class),

    WOLF_FLAGS(16, BYTE, Wolf.class),
    WOLF_OWNER(17, STRING, Wolf.class),
    WOLF_HEALTH(18, FLOAT, Wolf.class),
    WOLF_BEGGING(19, BYTE, Wolf.class),
    WOLF_COLOR(20, BYTE, Wolf.class),

    PIG_SADDLE(16, BYTE, Pig.class),

    SHEEP_DATA(16, BYTE, Sheep.class),

    VILLAGER_TYPE(16, INT, Villager.class),

    ENDERMAN_BLOCK(16, SHORT, Enderman.class),
    ENDERMAN_BLOCK_DATA(17, BYTE, Enderman.class),
    ENDERMAN_ALERTED(18, BYTE, Enderman.class),

    ZOMBIE_IS_CHILD(12, BYTE, Zombie.class),
    ZOMBIE_IS_VILLAGER(13, BYTE, Zombie.class),
    ZOMBIE_IS_CONVERTING(14, BYTE, Zombie.class),

    BLAZE_ON_FIRE(16, BYTE, Blaze.class),

    SPIDER_CLIMBING(16, BYTE, Spider.class),

    CREEPER_STATE(16, BYTE, Creeper.class),
    CREEPER_POWERED(17, BYTE, Creeper.class),

    GHAST_ATTACKING(16, BYTE, Ghast.class),

    SLIME_SIZE(16, BYTE, Slime.class),

    SKELETON_TYPE(13, BYTE, Skeleton.class),

    WITCH_AGGRESSIVE(21, BYTE, Witch.class),

    GOLEM_PLAYER_BUILT(16, BYTE, IronGolem.class),

    WITHER_TARGET_1(17, INT, Wither.class),
    WITHER_TARGET_2(18, INT, Wither.class),
    WITHER_TARGET_3(19, INT, Wither.class),
    WITHER_INVULN_TIME(20, INT, Wither.class),

    BOAT_HIT_TIME(17, INT, Boat.class),
    BOAT_DIRECTION(18, INT, Boat.class),
    BOAT_DAMAGE_TAKEN(19, FLOAT, Boat.class),

    MINECART_SHAKE_POWER(17, INT, Minecart.class),
    MINECART_SHAKE_DIRECTION(18, INT, Minecart.class),
    MINECART_DAMAGE_TAKEN(19, INT, Minecart.class),
    MINECART_BLOCK(20, INT, Minecart.class),
    MINECART_BLOCK_OFFSET(21, INT, Minecart.class),
    MINECART_BLOCK_SHOWN(22, BYTE, Minecart.class),

    FURNACE_MINECART_POWERED(16, BYTE, PoweredMinecart.class),

    ITEM_ITEM(10, ITEM, Item.class),

    ARROW_CRITICAL(16, BYTE, Arrow.class),

    FIREWORK_INFO(8, ITEM, Firework.class),

    ITEM_FRAME_ITEM(2, ITEM, ItemFrame.class),
    ITEM_FRAME_ROTATION(3, BYTE, ItemFrame.class),

    ENDER_CRYSTAL_HEALTH(8, INT, EnderCrystal.class),
    ;

    private final int index;
    private final MetadataType type;
    private final Class<? extends Entity> appliesTo;

    private MetadataIndex(int index, MetadataType type, Class<? extends Entity> appliesTo) {
        this.index = index;
        this.type = type;
        this.appliesTo = appliesTo;
    }

    //TODO Ugly hack so unit tests can read metadata
    public static MetadataIndex getIndex(int index, MetadataType type) {
        MetadataIndex output = null;
        for (MetadataIndex entry : values()) {
            if (entry.getIndex() == index && entry.getType().equals(type)) {
                output = entry;
                break;
            }
        }
        return output;
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

    public static interface StatusFlags {
        final int ON_FIRE = 0x01;
        final int SNEAKING = 0x02;
        final int SPRINTING = 0x08;
        final int ARM_UP = 0x10; // eating, drinking, blocking
        final int INVISIBLE = 0x20;
    }

    public static interface HorseFlags {
        final int IS_TAME = 0x02;
        final int HAS_SADDLE = 0x04;
        final int HAS_CHEST = 0x08;
        final int IS_BRED = 0x10;
        final int IS_EATING = 0x20;
        final int IS_REARING = 0x40;
        final int MOUTH_OPEN = 0x80;
    }

    public static interface TameableFlags {
        final int IS_SITTING = 0x01;
        final int WOLF_IS_ANGRY = 0x02;
        final int IS_TAME = 0x04;
    }
}
