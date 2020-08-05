package net.glowstone.entity;

import com.google.common.collect.ImmutableBiMap;
import org.bukkit.entity.EntityType;

import static org.bukkit.entity.EntityType.*;

public class EntityNetworkUtil {
    private static final ImmutableBiMap<EntityType, Integer> MOB_IDS
            = ImmutableBiMap.<EntityType, Integer>builder()
            .put(BAT, 3)
            .put(BLAZE, 4)
            .put(CAVE_SPIDER, 6)
            .put(CHICKEN, 7)
            .put(COD, 8)
            .put(COW, 9)
            .put(CREEPER, 10)
            .put(DONKEY, 11)
            .put(DOLPHIN, 12)
            .put(DROWNED, 14)
            .put(ELDER_GUARDIAN, 15)
            .put(ENDER_DRAGON, 17)
            .put(ENDERMAN, 18)
            .put(ENDERMITE, 19)
            .put(EVOKER, 21)
            .put(GHAST, 26)
            .put(GIANT, 27)
            .put(GUARDIAN, 28)
            .put(HORSE, 29)
            .put(HUSK, 30)
            .put(ILLUSIONER, 31)
            .put(LLAMA, 36)
            .put(MAGMA_CUBE, 38)
            .put(MULE, 46)
            .put(MUSHROOM_COW, 47)
            .put(OCELOT, 48)
            .put(PARROT, 50)
            .put(PIG, 51)
            .put(PUFFERFISH, 52)
            .put(ZOMBIFIED_PIGLIN, 53)
            .put(POLAR_BEAR, 54)
            .put(RABBIT, 56)
            .put(SALMON, 57)
            .put(SHEEP, 58)
            .put(SHULKER, 59)
            .put(SILVERFISH, 61)
            .put(SKELETON, 62)
            .put(SKELETON_HORSE, 63)
            .put(SLIME, 64)
            .put(SNOWMAN, 66)
            .put(SPIDER, 69)
            .put(SQUID, 70)
            .put(STRAY, 71)
            .put(TROPICAL_FISH, 72)
            .put(TURTLE, 73)
            .put(VEX, 78)
            .put(VILLAGER, 79)
            .put(IRON_GOLEM, 80)
            .put(VINDICATOR, 81)
            .put(WITCH, 82)
            .put(WITHER, 83)
            .put(WITHER_SKELETON, 84)
            .put(WOLF, 86)
            .put(ZOMBIE, 87)
            .put(ZOMBIE_HORSE, 88)
            .put(ZOMBIE_VILLAGER, 89)
            .put(PHANTOM, 90)
            .build();

    private static final ImmutableBiMap<EntityType, Integer> OBJECT_IDS
            = ImmutableBiMap.<EntityType, Integer>builder()
            .put(BOAT, 1)
            .put(DROPPED_ITEM, 2)
            .put(AREA_EFFECT_CLOUD, 3)
            .put(MINECART, 10)
            .put(PRIMED_TNT, 50)
            .put(ENDER_CRYSTAL, 51)
            .put(ARROW, 60)
            .put(SNOWBALL, 61)
            .put(EGG, 62)
            .put(FIREBALL, 63)
            .put(SMALL_FIREBALL, 64)
            .put(ENDER_PEARL, 65)
            .put(WITHER_SKULL, 66)
            .put(SHULKER_BULLET, 67)
            .put(LLAMA_SPIT, 68)
            .put(FALLING_BLOCK, 70)
            .put(ITEM_FRAME, 71)
            .put(ENDER_SIGNAL, 72)
            .put(SPLASH_POTION, 73)
            .put(THROWN_EXP_BOTTLE, 75)
            .put(FIREWORK, 76)
            .put(LEASH_HITCH, 77)
            .put(ARMOR_STAND, 78)
            .put(EVOKER_FANGS, 79)
            .put(FISHING_HOOK, 90)
            .put(SPECTRAL_ARROW, 91)
            .put(DRAGON_FIREBALL, 93)
            .put(TRIDENT, 94)
            .build();

    /**
     * Gets the network ID of a mob entity.
     *
     * @param type the mob entity type
     * @return the network ID of the mob entity, or -1 if it is not a mob.
     */
    public static int getMobId(EntityType type) {
        return MOB_IDS.getOrDefault(type, -1);
    }

    /**
     * Gets the network ID of an object entity.
     *
     * @param type the object entity type
     * @return the network ID of the object entity, or -1 if it is not an object.
     */
    public static int getObjectId(EntityType type) {
        return OBJECT_IDS.getOrDefault(type, -1);
    }
}
