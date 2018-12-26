package net.glowstone.entity.meta;

import static net.glowstone.entity.meta.MetadataType.BLOCKID;
import static net.glowstone.entity.meta.MetadataType.BOOLEAN;
import static net.glowstone.entity.meta.MetadataType.BYTE;
import static net.glowstone.entity.meta.MetadataType.CHAT;
import static net.glowstone.entity.meta.MetadataType.DIRECTION;
import static net.glowstone.entity.meta.MetadataType.FLOAT;
import static net.glowstone.entity.meta.MetadataType.INT;
import static net.glowstone.entity.meta.MetadataType.ITEM;
import static net.glowstone.entity.meta.MetadataType.NBTTAG;
import static net.glowstone.entity.meta.MetadataType.OPTCHAT;
import static net.glowstone.entity.meta.MetadataType.OPTPOSITION;
import static net.glowstone.entity.meta.MetadataType.OPTUUID;
import static net.glowstone.entity.meta.MetadataType.PARTICLE;
import static net.glowstone.entity.meta.MetadataType.POSITION;
import static net.glowstone.entity.meta.MetadataType.STRING;
import static net.glowstone.entity.meta.MetadataType.VECTOR;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.glowstone.entity.passive.GlowParrot;
import net.glowstone.entity.passive.GlowTameable;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Boat;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Fish;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Horse;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.PolarBear;
import org.bukkit.entity.PufferFish;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Spider;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.TippedArrow;
import org.bukkit.entity.Trident;
import org.bukkit.entity.TropicalFish;
import org.bukkit.entity.Turtle;
import org.bukkit.entity.Vex;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Vindicator;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkull;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.entity.minecart.PoweredMinecart;

/**
 * Index constants for entity metadata.
 */
@RequiredArgsConstructor
public enum MetadataIndex {

    //Entity
    STATUS(0, BYTE, Entity.class),
    AIR_TIME(1, INT, Entity.class),
    NAME_TAG(2, OPTCHAT, Entity.class),
    SHOW_NAME_TAG(3, BOOLEAN, Entity.class),
    SILENT(4, BOOLEAN, Entity.class),
    NOGRAVITY(5, BOOLEAN, Entity.class),

    AREAEFFECTCLOUD_RADIUS(6, FLOAT, AreaEffectCloud.class),
    AREAEFFECTCLOUD_COLOR(7, INT, AreaEffectCloud.class),
    AREAEFFECTCLOUD_POINT(8, BOOLEAN, AreaEffectCloud.class),
    AREAEFFECTCLOUD_PARTICLE(9, PARTICLE, AreaEffectCloud.class),

    ARROW_CRITICAL(6, BYTE, Arrow.class),
    TIPPEDARROW_COLOR(7, INT, TippedArrow.class),

    TRIDENT_LOYALTY(7, INT, Trident.class),

    BOAT_HIT_TIME(6, INT, Boat.class),
    BOAT_DIRECTION(7, INT, Boat.class),
    BOAT_DAMAGE_TAKEN(8, FLOAT, Boat.class),
    BOAT_TYPE(9, INT, Boat.class),
    BOAT_RIGHT_PADDLE_TURNING(10, BOOLEAN, Boat.class),
    BOAT_LEFT_PADDLE_TURNING(11, BOOLEAN, Boat.class),
    BOAT_SPLASH_TIMER(12, INT, Boat.class),

    ENDERCRYSTAL_BEAM_TARGET(6, OPTPOSITION, EnderCrystal.class),
    ENDERCRYSTAL_SHOW_BOTTOM(7, BOOLEAN, EnderCrystal.class),

    WITHERSKULL_INVULNERABLE(6, BOOLEAN, WitherSkull.class),

    FIREWORK_INFO(6, ITEM, Firework.class),
    FIREWORK_ENTITY(7, INT, Firework.class),

    ITEM_FRAME_ITEM(6, ITEM, ItemFrame.class),
    ITEM_FRAME_ROTATION(7, INT, ItemFrame.class),
    ITEM_ITEM(6, ITEM, Item.class),

    HAND_USED(6, BYTE, LivingEntity.class),
    HEALTH(7, FLOAT, LivingEntity.class),
    POTION_COLOR(8, INT, LivingEntity.class),
    POTION_AMBIENT(9, BOOLEAN, LivingEntity.class),
    ARROW_COUNT(10, BYTE, LivingEntity.class),

    PLAYER_EXTRA_HEARTS(11, FLOAT, Player.class),
    PLAYER_SCORE(12, INT, Player.class),
    PLAYER_SKIN_PARTS(13, BYTE, Player.class),
    PLAYER_MAIN_HAND(14, BYTE, Player.class),
    PLAYER_LEFT_SHOULDER(15, NBTTAG, Player.class),
    PLAYER_RIGHT_SHOULDER(16, NBTTAG, Player.class),

    ARMORSTAND_FLAGS(11, BYTE, ArmorStand.class),
    ARMORSTAND_HEAD_POSITION(12, VECTOR, ArmorStand.class),
    ARMORSTAND_BODY_POSITION(13, VECTOR, ArmorStand.class),
    ARMORSTAND_LEFT_ARM_POSITION(14, VECTOR, ArmorStand.class),
    ARMORSTAND_RIGHT_ARM_POSITION(15, VECTOR, ArmorStand.class),
    ARMORSTAND_LEFT_LEG_POSITION(16, VECTOR, ArmorStand.class),
    ARMORSTAND_RIGHT_LEG_POSITION(17, VECTOR, ArmorStand.class),

    //NO_AI(10, BYTE, Insentient.class),
    // TODO - 1.9 "Insentient extends Living". Need more information

    BAT_FLAGS(12, BYTE, Bat.class),

    AGE_ISBABY(12, BOOLEAN, Ageable.class),

    ABSTRACT_HORSE_FLAGS(13, BYTE, AbstractHorse.class),
    ABSTRACT_HORSE_OWNER(14, OPTUUID, AbstractHorse.class),

    HORSE_STYLE(15, INT, Horse.class),
    HORSE_ARMOR(16, INT, Horse.class),

    CHESTED_HORSE_HAS_CHEST(15, BOOLEAN, ChestedHorse.class),

    LLAMA_STRENGTH(16, INT, Llama.class),
    LLAMA_CARPET(17, INT, Llama.class),
    LLAMA_VARIANT(18, INT, Llama.class),

    PIG_SADDLE(13, BOOLEAN, Pig.class),
    PIG_BOOST(14, INT, Pig.class),

    RABBIT_TYPE(13, INT, Rabbit.class),

    SHEEP_DATA(13, BYTE, Sheep.class),

    TAMEABLEAANIMAL_STATUS(13, BYTE, GlowTameable.class),
    TAMEABLEANIMAL_OWNER(14, OPTUUID, GlowTameable.class),

    OCELOT_TYPE(15, INT, Ocelot.class),

    WOLF_HEALTH(15, FLOAT, Wolf.class),
    WOLF_BEGGING(16, BOOLEAN, Wolf.class),
    WOLF_COLOR(17, BYTE, Wolf.class),

    VILLAGER_PROFESSION(13, INT, Villager.class),

    GOLEM_PLAYER_BUILT(12, BYTE, IronGolem.class),

    SNOWMAN_NOHAT(12, BYTE, Snowman.class),

    SHULKER_FACING_DIRECTION(12, DIRECTION, Shulker.class),
    SHULKER_ATTACHMENT_POSITION(13, OPTPOSITION, Shulker.class),
    SHULKER_SHIELD_HEIGHT(14, BYTE, Shulker.class),
    SHULKER_COLOR(15, BYTE, Shulker.class),

    BLAZE_ON_FIRE(12, BYTE, Blaze.class),

    CREEPER_STATE(12, INT, Creeper.class),
    CREEPER_POWERED(13, BOOLEAN, Creeper.class),
    CREEPER_IGNITED(14, BOOLEAN, Creeper.class),

    GUARDIAN_SPIKES(12, BOOLEAN, Guardian.class),
    GUARDIAN_TARGET(13, INT, Guardian.class),

    SKELETON_HANDS_RISEN_UP(12, BOOLEAN, Skeleton.class),

    SPIDER_CLIMBING(12, BYTE, Spider.class),

    WITCH_AGGRESSIVE(13, BOOLEAN, Witch.class),

    WITHER_TARGET_1(12, INT, Wither.class),
    WITHER_TARGET_2(13, INT, Wither.class),
    WITHER_TARGET_3(14, INT, Wither.class),
    WITHER_INVULN_TIME(15, INT, Wither.class),

    ZOMBIE_IS_CHILD(12, BOOLEAN, Zombie.class),
    ZOMBIE_PROFESSION(13, INT, Zombie.class), // Unused as of 1.11
    ZOMBIE_HANDS_RISED_UP(14, BOOLEAN, Zombie.class),

    ZOMBIE_VILLAGER_IS_CONVERTING(15, BOOLEAN, ZombieVillager.class),
    ZOMBIE_VILLAGER_PROFESSION(16, INT, ZombieVillager.class),

    ENDERMAN_BLOCK(12, BLOCKID, Enderman.class),
    ENDERMAN_SCREAMING(13, BOOLEAN, Enderman.class),

    ENDERDRAGON_PHASE(12, INT, EnderDragon.class),

    GHAST_ATTACKING(12, BOOLEAN, Ghast.class),

    SLIME_SIZE(12, INT, Slime.class),

    POLARBEAR_STANDING(13, BOOLEAN, PolarBear.class),

    MINECART_SHAKE_POWER(6, INT, Minecart.class),
    MINECART_SHAKE_DIRECTION(7, INT, Minecart.class),
    MINECART_DAMAGE_TAKEN(8, FLOAT, Minecart.class),
    MINECART_BLOCK(9, INT, Minecart.class),
    MINECART_BLOCK_OFFSET(10, INT, Minecart.class),
    MINECART_BLOCK_SHOWN(11, BYTE, Minecart.class),

    EVOKER_SPELL(12, BYTE, Evoker.class),

    VEX_STATE(12, BYTE, Vex.class),

    VINDICATOR_STATE(12, BYTE, Vindicator.class),

    PHANTOM_SIZE(12, INT, Phantom.class),

    FISH_FROM_BUCKET(12, BOOLEAN, Fish.class),

    PUFFER_FISH_STATE(13, INT, PufferFish.class),

    TROPICAL_FISH_VARIANT(13, INT, TropicalFish.class),

    TURTLE_HOME_POSITION(13, POSITION, Turtle.class),
    TURTLE_HAS_EGG(14, BOOLEAN, Turtle.class),
    TURTLE_LAYING_EGG(15, BOOLEAN, Turtle.class),
    TURTLE_TRAVEL_POS(16, POSITION, Turtle.class),
    TURTLE_GOING_HOME(17, BOOLEAN, Turtle.class),
    TURTLE_TRAVELLING(18, BOOLEAN, Turtle.class),

    DROWNED_HAS_TARGET(15, BOOLEAN, Drowned.class),

    PARROT_VARIANT(15, INT, GlowParrot.class),

    MINECARTCOMMANDBLOCK_COMMAND(12, STRING, CommandMinecart.class),
    MINECARTCOMMANDBLOCK_LAST_OUTPUT(13, CHAT, CommandMinecart.class),

    FURNACE_MINECART_POWERED(12, BOOLEAN, PoweredMinecart.class),
    TNT_PRIMED(6, INT, TNTPrimed.class),

    /**
     * Hooked entity id + 1, or 0 if there is no hooked entity.
     */
    FISHING_HOOK_HOOKED_ENTITY(6, INT, FishHook.class);

    @Getter
    private final int index;
    @Getter
    private final MetadataType type;
    @Getter
    private final Class<? extends Entity> appliesTo;

    /**
     * Returns the first {@link MetadataIndex} with a given index and {@link MetadataType}.
     *
     * @param index the index to look up
     * @param type  the type to look up
     * @return a {@link MetadataIndex} with that index and type, or null if none match
     */
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

    public boolean appliesTo(Class<? extends Entity> clazz) {
        return appliesTo.isAssignableFrom(clazz);
    }

    public interface StatusFlags {

        int ON_FIRE = 0x01;
        int SNEAKING = 0x02;
        int SPRINTING = 0x08;
        int ARM_UP = 0x10; // eating, drinking, blocking
        int INVISIBLE = 0x20;
        int GLOWING = 0x40;
        int GLIDING = 0x80;
    }

    public interface ArmorStandFlags {

        int IS_SMALL = 0x01;
        int HAS_GRAVITY = 0x02;
        int HAS_ARMS = 0x04;
        int NO_BASE_PLATE = 0x08;
        int IS_MARKER = 0x10;
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

    public interface BatFlags {

        int IS_HANGING = 0x01;
    }
}
