package net.glowstone.entity;

import static org.bukkit.entity.EntityType.*;

import com.google.common.collect.ImmutableBiMap;
import net.glowstone.entity.monster.*;
import net.glowstone.entity.objects.*;
import net.glowstone.entity.passive.*;
import org.bukkit.entity.EntityType;

public class EntityRegistry {

    public static final ImmutableBiMap<EntityType, Class<? extends GlowEntity>> ENTITIES =
            ImmutableBiMap.<EntityType, Class<? extends GlowEntity>>builder()
            .put(ARMOR_STAND, GlowArmorStand.class)
            //TODO: Arrow
            .put(BAT, GlowBat.class)
            .put(BLAZE, GlowBlaze.class)
            //TODO: Boat
            .put(CAVE_SPIDER, GlowCaveSpider.class)
            .put(CHICKEN, GlowChicken.class)
            .put(COW, GlowCow.class)
            .put(CREEPER, GlowCreeper.class)
            .put(DROPPED_ITEM, GlowItem.class)
            //TODO: Egg
            //TODO: Ender Crystal
            //TODO: Ender Dragon
            //TODO: Ender PEarl
            //TODO: Ender Signal
            .put(ENDERMAN, GlowEnderman.class)
            .put(ENDERMITE, GlowEndermite.class)
            //TODO: Experience orb
            //TODO: Falling block
            //TODO: Fireball
            //TODO: Firework
            //TODO: Fishing hook
            .put(GHAST, GlowGhast.class)
            .put(GIANT, GlowGiant.class)
            .put(GUARDIAN, GlowGuardian.class)
            .put(HORSE, GlowHorse.class)
            .put(IRON_GOLEM, GlowIronGolem.class)
            .put(ITEM_FRAME, GlowItemFrame.class)
            //TODO: Leash hitch
            //TODO: Lightning
            .put(MAGMA_CUBE, GlowMagmaCube.class)
            //TODO: Minecarts
            .put(MUSHROOM_COW, GlowMooshroom.class)
            .put(OCELOT, GlowOcelot.class)
            //TODO: Painting
            .put(PIG, GlowPig.class)
            .put(PIG_ZOMBIE, GlowPigZombie.class)
            .put(PLAYER, GlowPlayer.class)
            .put(PRIMED_TNT, GlowTNTPrimed.class)
            .put(RABBIT, GlowRabbit.class)
            .put(SHEEP, GlowSheep.class)
            .put(SILVERFISH, GlowSilverfish.class)
            .put(SKELETON, GlowSkeleton.class)
            .put(SLIME, GlowSlime.class)
            //TODO: Fireball
            //TODO: Snowball
            .put(SNOWMAN, GlowSnowman.class)
            .put(SPIDER, GlowSpider.class)
            //TODO: Splash potion
            .put(SQUID, GlowSquid.class)
            //TODO: Experience bottle
            .put(VILLAGER, GlowVillager.class)
            .put(WEATHER, GlowWeather.class)
            .put(WITCH, GlowWitch.class)
            //TODO: Wither
            //TODO: Wither Skull
            .put(WOLF, GlowWolf.class)
            .put(ZOMBIE, GlowZombie.class)
            .build();

    public static Class<? extends GlowEntity> getEntity(short id) {
        return ENTITIES.get(EntityType.fromId(id));
    }

}
