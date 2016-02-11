package net.glowstone.entity.meta;

import net.glowstone.entity.passive.GlowTameable;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.PoweredMinecart;

import static net.glowstone.entity.meta.MetadataType.*;

/**
 * Index constants for entity metadata.
 */
public enum MetadataIndex {

    //Entity
    STATUS(0, BYTE, Entity.class),
    AIR_TIME(1, INT, Entity.class),
    NAME_TAG(2, STRING, Entity.class),
    SHOW_NAME_TAG(3, BYTE, Entity.class),
    SILENT(4, BYTE, Entity.class),

    //TODO 1.9 - Support this
    //AREAEFFECTCLOUD_RADIUS(5, FLOAT, Entity.class),
    //AREAEFFECTCLOUD_COLOR(6, INT, Entity.class),
    //AREAEFFECTCLOUD_UNKNOWN(7, BOOLEAN, Entity.class),
    //AREAEFFECTCLOUD_PARTICLEID(8, INT, Entity.class),

    ARROW_CRITICAL(5, BYTE, Arrow.class),

    TIPPEDARROW_COLOR(6, INT, Arrow.class), //TODO Proper arrow class

    BOAT_HIT_TIME(5, INT, Boat.class),
    BOAT_DIRECTION(6, INT, Boat.class),
    BOAT_DAMAGE_TAKEN(7, FLOAT, Boat.class),
    BOAT_TYPE(8, INT, Boat.class),

    ENDERCRYSTAL_BEAM_TARGET(5, OPTPOSITION, EnderCrystal.class),
    ENDERCRYSTAL_SHOW_BOTTOM(6, BOOLEAN, EnderCrystal.class),

    WITHERSKULL_INVULNERABLE(5, BOOLEAN, WitherSkull.class),

    FIREWORK_INFO(5, ITEM, Firework.class),

    ITEM_FRAME_ITEM(5, ITEM, ItemFrame.class),
    ITEM_FRAME_ROTATION(6, INT, ItemFrame.class),

    ITEM_ITEM(5, ITEM, Item.class),

    HAND_USED(5, BYTE, LivingEntity.class), //TODO : 1.9 - To confirm
    HEALTH(6, FLOAT, LivingEntity.class),
    POTION_COLOR(7, INT, LivingEntity.class),
    POTION_AMBIENT(8, BOOLEAN, LivingEntity.class),
    ARROW_COUNT(9, BYTE, LivingEntity.class),

    ARMORSTAND_FLAGS(10, BYTE, ArmorStand.class),
    ARMORSTAND_HEAD_POSITION(11, VECTOR, ArmorStand.class),
    ARMORSTAND_BODY_POSITION(12, VECTOR, ArmorStand.class),
    ARMORSTAND_LEFT_ARM_POSITION(13, VECTOR, ArmorStand.class),
    ARMORSTAND_RIGHT_ARM_POSITION(14, VECTOR, ArmorStand.class),
    ARMORSTAND_LEFT_LEG_POSITION(15, VECTOR, ArmorStand.class),
    ARMORSTAND_RIGHT_LEG_POSITION(16, VECTOR, ArmorStand.class),

    //NO_AI(10, BYTE, Insentient.class), TODO - 1.9 "Insentient extends Living". Need more information

    BAT_HANGING(11, BYTE, Bat.class),

    AGE_ISBABY(11, BOOLEAN, Ageable.class),

    HORSE_FLAGS(12, BYTE, Horse.class),
    HORSE_TYPE(13, INT, Horse.class),
    HORSE_STYLE(14, INT, Horse.class),
    HORSE_OWNER(15, OPTUUID, Horse.class),
    HORSE_ARMOR(16, INT, Horse.class),

    PIG_SADDLE(12, BOOLEAN, Pig.class),

    RABBIT_TYPE(12, INT, Rabbit.class),

    SHEEP_DATA(12, BYTE, Sheep.class),

    TAMEABLEAANIMAL_STATUS(12, BYTE, GlowTameable.class), //TODO 1.9 - We need a "TameableAnimal extends Animal"
    TAMEABLEANIMAL_OWNER(13, OPTUUID, GlowTameable.class),

    OCELOT_TYPE(14, INT, Ocelot.class),

    WOLF_HEALTH(14, FLOAT, Wolf.class),
    WOLF_BEGGING(15, BOOLEAN, Wolf.class),
    WOLF_COLOR(20, BYTE, Wolf.class),

    VILLAGER_TYPE(12, INT, Villager.class), //TODO 1.9 - Currently Unknown on wiki.vg

    GOLEM_PLAYER_BUILT(11, BYTE, IronGolem.class),

    //SHULKER_FACING_DIRECTION(11, DIRECTION, Golem.class), //TODO 1.9 - New mob?
    //SHULKER_ATTACHMENT_POSITION(12, OPTPOSITION, Golem.class), //TODO 1.9 - New mob?
    //SHULKER_SHIELD_HEIGHT(13, BYTE, Golem.class), //TODO 1.9 - New mob?

    BLAZE_ON_FIRE(11, BYTE, Blaze.class),

    CREEPER_STATE(11, INT, Creeper.class),
    CREEPER_POWERED(12, BOOLEAN, Creeper.class),
    CREEPER_IGNITED(13, BOOLEAN, Creeper.class),

    GUARDIAN_FLAGS(11, BYTE, Guardian.class),
    GUARDIAN_TARGET(12, INT, Guardian.class),

    SKELETON_TYPE(11, INT, Skeleton.class),
    SKELETON_UNKNOWN(12, BOOLEAN, Skeleton.class), //TODO 1.9 - Something hand related according to wiki.vg

    SPIDER_CLIMBING(11, BYTE, Spider.class),

    WITCH_AGGRESSIVE(11, BOOLEAN, Witch.class),

    WITHER_TARGET_1(11, INT, Wither.class),
    WITHER_TARGET_2(12, INT, Wither.class),
    WITHER_TARGET_3(13, INT, Wither.class),
    WITHER_INVULN_TIME(14, INT, Wither.class),

    ZOMBIE_IS_CHILD(11, BOOLEAN, Zombie.class),
    ZOMBIE_IS_VILLAGER(12, INT, Zombie.class),
    ZOMBIE_IS_CONVERTING(13, BOOLEAN, Zombie.class),
    ZOMBIE_HANDS_RISED_UP(14, BOOLEAN, Zombie.class),

    ENDERMAN_BLOCK(11, BLOCKID, Enderman.class),
    ENDERMAN_ALERTED(12, BOOLEAN, Enderman.class),

    ENDERDRAGON_PHASE(11, INT, EnderDragon.class),

    GHAST_ATTACKING(11, BOOLEAN, Ghast.class),

    SLIME_SIZE(11, INT, Slime.class),

    MINECART_SHAKE_POWER(5, INT, Minecart.class),
    MINECART_SHAKE_DIRECTION(6, INT, Minecart.class),
    MINECART_DAMAGE_TAKEN(7, FLOAT, Minecart.class),
    MINECART_BLOCK(8, INT, Minecart.class),
    MINECART_BLOCK_OFFSET(9, INT, Minecart.class),
    MINECART_BLOCK_SHOWN(10, BYTE, Minecart.class),

    //TODO - 1.9 When Those minecarts are implemented, uncomment this
    //MINECARTCOMMANDBLOCK_COMMAND(11, STRING, Minecart.class), //TODO 1.9 - Command block minecraft addition
    //MINECARTCOMMANDBLOCK_LAST_OUTPUT(12, CHAT, Minecart.class), //TODO 1.9 - Command block minecraft addition

    FURNACE_MINECART_POWERED(11, BOOLEAN, PoweredMinecart.class),

    TNT_PRIMED(5, INT, TNTPrimed.class);


    private final int index;
    private final MetadataType type;
    private final Class<? extends Entity> appliesTo;

    private MetadataIndex(int index, MetadataType type, Class<? extends Entity> appliesTo) {
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

    public interface StatusFlags {
        int ON_FIRE = 0x01;
        int SNEAKING = 0x02;
        int SPRINTING = 0x08;
        int ARM_UP = 0x10; // eating, drinking, blocking
        int INVISIBLE = 0x20;
    }

    public interface HorseFlags {
        int IS_TAME = 0x02;
        int HAS_SADDLE = 0x04;
        int HAS_CHEST = 0x08;
        int IS_BRED = 0x10;
        int IS_EATING = 0x20;
        int IS_REARING = 0x40;
        int MOUTH_OPEN = 0x80;
    }

    public interface TameableFlags {
        int IS_SITTING = 0x01;
        int WOLF_IS_ANGRY = 0x02;
        int IS_TAME = 0x04;
    }
}
