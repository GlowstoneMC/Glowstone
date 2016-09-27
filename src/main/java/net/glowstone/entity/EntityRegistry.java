package net.glowstone.entity;

import com.google.common.collect.ImmutableBiMap;
import net.glowstone.entity.monster.*;
import net.glowstone.entity.objects.*;
import net.glowstone.entity.passive.*;
import org.bukkit.entity.*;

public class EntityRegistry {

    private static final ImmutableBiMap<Class<? extends Entity>, Class<? extends GlowEntity>> ENTITIES =
            ImmutableBiMap.<Class<? extends Entity>, Class<? extends GlowEntity>>builder()
                    .put(ArmorStand.class, GlowArmorStand.class)
                    //TODO: Arrow
                    .put(Bat.class, GlowBat.class)
                    .put(Blaze.class, GlowBlaze.class)
                    //TODO: Boat
                    .put(CaveSpider.class, GlowCaveSpider.class)
                    .put(Chicken.class, GlowChicken.class)
                    .put(Cow.class, GlowCow.class)
                    .put(Creeper.class, GlowCreeper.class)
                    .put(Item.class, GlowItem.class)
                    //TODO: Egg
                    //TODO: Ender Crystal
                    //TODO: Ender Dragon
                    //TODO: Ender PEarl
                    //TODO: Ender Signal
                    .put(Enderman.class, GlowEnderman.class)
                    .put(Endermite.class, GlowEndermite.class)
                    //TODO: Experience orb
                    .put(FallingBlock.class, GlowFallingBlock.class)
                    //TODO: Fireball
                    //TODO: Firework
                    //TODO: Fishing hook
                    .put(Ghast.class, GlowGhast.class)
                    .put(Giant.class, GlowGiant.class)
                    .put(Guardian.class, GlowGuardian.class)
                    .put(Horse.class, GlowHorse.class)
                    .put(IronGolem.class, GlowIronGolem.class)
                    .put(ItemFrame.class, GlowItemFrame.class)
                    //TODO: Leash hitch
                    //TODO: Lightning
                    .put(MagmaCube.class, GlowMagmaCube.class)
                    .put(GlowMinecart.MinecartType.RIDEABLE.getEntityClass(), GlowMinecart.MinecartType.RIDEABLE.getMinecartClass())
                    .put(GlowMinecart.MinecartType.CHEST.getEntityClass(), GlowMinecart.MinecartType.CHEST.getMinecartClass())
                    .put(GlowMinecart.MinecartType.FURNACE.getEntityClass(), GlowMinecart.MinecartType.FURNACE.getMinecartClass())
                    .put(GlowMinecart.MinecartType.TNT.getEntityClass(), GlowMinecart.MinecartType.TNT.getMinecartClass())
                    .put(GlowMinecart.MinecartType.HOPPER.getEntityClass(), GlowMinecart.MinecartType.HOPPER.getMinecartClass())
                    .put(GlowMinecart.MinecartType.SPAWNER.getEntityClass(), GlowMinecart.MinecartType.SPAWNER.getMinecartClass())
                    //TODO: Command Block minecart
                    .put(MushroomCow.class, GlowMooshroom.class)
                    .put(Ocelot.class, GlowOcelot.class)
                    //TODO: Painting
                    .put(Pig.class, GlowPig.class)
                    .put(PigZombie.class, GlowPigZombie.class)
                    .put(Player.class, GlowPlayer.class)
                    .put(PolarBear.class, GlowPolarBear.class)
                    .put(TNTPrimed.class, GlowTNTPrimed.class)
                    .put(Rabbit.class, GlowRabbit.class)
                    .put(Sheep.class, GlowSheep.class)
                    .put(Silverfish.class, GlowSilverfish.class)
                    .put(Skeleton.class, GlowSkeleton.class)
                    .put(Slime.class, GlowSlime.class)
                    //TODO: Fireball
                    //TODO: Snowball
                    .put(Snowman.class, GlowSnowman.class)
                    .put(Spider.class, GlowSpider.class)
                    //TODO: Splash potion
                    .put(Squid.class, GlowSquid.class)
                    //TODO: Experience bottle
                    .put(Villager.class, GlowVillager.class)
                    .put(Weather.class, GlowWeather.class)
                    .put(Witch.class, GlowWitch.class)
                    .put(Wither.class, GlowWither.class)
                    //TODO: Wither Skull
                    .put(Wolf.class, GlowWolf.class)
                    .put(Zombie.class, GlowZombie.class)
                    .put(Shulker.class, GlowShulker.class)
                    .build();

    public static Class<? extends GlowEntity> getEntity(EntityType type) {
        return ENTITIES.get(type.getEntityClass());
    }

    public static Class<? extends GlowEntity> getEntity(Class<? extends Entity> clazz) {
        return ENTITIES.get(clazz);
    }
}
