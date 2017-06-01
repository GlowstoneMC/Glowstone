package net.glowstone.entity;

import com.google.common.collect.ImmutableBiMap;
import net.glowstone.entity.monster.*;
import net.glowstone.entity.monster.complex.GlowEnderDragon;
import net.glowstone.entity.objects.*;
import net.glowstone.entity.passive.*;
import net.glowstone.io.entity.EntityStorage;
import org.bukkit.entity.*;

import java.util.*;

public class EntityRegistry {

    private static final Map<String, CustomEntityDescriptor> CUSTOM_ENTITIES = new HashMap<>();

    private static final ImmutableBiMap<Class<? extends Entity>, Class<? extends GlowEntity>> ENTITIES =
            ImmutableBiMap.<Class<? extends Entity>, Class<? extends GlowEntity>>builder()
                    .put(AbstractHorse.class, GlowAbstractHorse.class)
                    .put(ArmorStand.class, GlowArmorStand.class)
                    //TODO: Arrow
                    .put(Bat.class, GlowBat.class)
                    .put(Blaze.class, GlowBlaze.class)
                    //TODO: Boat
                    .put(CaveSpider.class, GlowCaveSpider.class)
                    .put(ChestedHorse.class, GlowChestedHorse.class)
                    .put(Chicken.class, GlowChicken.class)
                    .put(Cow.class, GlowCow.class)
                    .put(Creeper.class, GlowCreeper.class)
                    .put(Donkey.class, GlowDonkey.class)
                    //TODO: Egg
                    .put(ElderGuardian.class, GlowElderGuardian.class)
                    //TODO: Ender Crystal
                    .put(EnderDragon.class, GlowEnderDragon.class)
                    //TODO: Ender Pearl
                    //TODO: Ender Signal
                    .put(Enderman.class, GlowEnderman.class)
                    .put(Endermite.class, GlowEndermite.class)
                    //TODO: Experience orb
                    .put(Evoker.class, GlowEvoker.class)
                    .put(EvokerFangs.class, GlowEvokerFangs.class)
                    .put(FallingBlock.class, GlowFallingBlock.class)
                    //TODO: Fireball
                    //TODO: Firework
                    //TODO: Fishing hook
                    .put(Ghast.class, GlowGhast.class)
                    .put(Giant.class, GlowGiant.class)
                    .put(Guardian.class, GlowGuardian.class)
                    .put(Horse.class, GlowHorse.class)
                    .put(Husk.class, GlowHusk.class)
                    .put(IronGolem.class, GlowIronGolem.class)
                    .put(Item.class, GlowItem.class)
                    .put(ItemFrame.class, GlowItemFrame.class)
                    //TODO: Leash hitch
                    //TODO: Lightning
                    .put(Llama.class, GlowLlama.class)
                    .put(MagmaCube.class, GlowMagmaCube.class)
                    .put(GlowMinecart.MinecartType.RIDEABLE.getEntityClass(), GlowMinecart.MinecartType.RIDEABLE.getMinecartClass())
                    .put(GlowMinecart.MinecartType.CHEST.getEntityClass(), GlowMinecart.MinecartType.CHEST.getMinecartClass())
                    .put(GlowMinecart.MinecartType.FURNACE.getEntityClass(), GlowMinecart.MinecartType.FURNACE.getMinecartClass())
                    .put(GlowMinecart.MinecartType.TNT.getEntityClass(), GlowMinecart.MinecartType.TNT.getMinecartClass())
                    .put(GlowMinecart.MinecartType.HOPPER.getEntityClass(), GlowMinecart.MinecartType.HOPPER.getMinecartClass())
                    .put(GlowMinecart.MinecartType.SPAWNER.getEntityClass(), GlowMinecart.MinecartType.SPAWNER.getMinecartClass())
                    //TODO: Command Block minecart
                    .put(Mule.class, GlowMule.class)
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
                    .put(Shulker.class, GlowShulker.class)
                    .put(Silverfish.class, GlowSilverfish.class)
                    .put(Skeleton.class, GlowSkeleton.class)
                    .put(SkeletonHorse.class, GlowSkeletonHorse.class)
                    .put(Slime.class, GlowSlime.class)
                    //TODO: Fireball
                    //TODO: Snowball
                    .put(Snowman.class, GlowSnowman.class)
                    .put(Spider.class, GlowSpider.class)
                    //TODO: Splash potion
                    .put(Squid.class, GlowSquid.class)
                    .put(Stray.class, GlowStray.class)
                    //TODO: Experience bottle
                    .put(Vex.class, GlowVex.class)
                    .put(Villager.class, GlowVillager.class)
                    .put(Vindicator.class, GlowVindicator.class)
                    .put(Weather.class, GlowWeather.class)
                    .put(Witch.class, GlowWitch.class)
                    .put(Wither.class, GlowWither.class)
                    .put(WitherSkeleton.class, GlowWitherSkeleton.class)
                    //TODO: Wither Skull
                    .put(Wolf.class, GlowWolf.class)
                    .put(Zombie.class, GlowZombie.class)
                    .put(ZombieHorse.class, GlowZombieHorse.class)
                    .put(ZombieVillager.class, GlowZombieVillager.class)
                    .build();

    public static Class<? extends GlowEntity> getEntity(EntityType type) {
        return ENTITIES.get(type.getEntityClass());
    }

    public static Class<? extends GlowEntity> getEntity(Class<? extends Entity> clazz) {
        return ENTITIES.get(clazz);
    }

    public static void registerCustomEntity(CustomEntityDescriptor<? extends GlowEntity> descriptor) {
        if (descriptor == null || descriptor.getEntityClass() == null || descriptor.getId() == null || descriptor.getPlugin() == null)
            return;
        if (descriptor.getPlugin().isEnabled()) {
            descriptor.getPlugin().getServer().getLogger().warning("Cannot register custom entity '" + descriptor.getId() + "' for plugin '" + descriptor.getPlugin() + "', worlds are already loaded.");
            return;
        }
        if (CUSTOM_ENTITIES.containsKey(descriptor.getId().toLowerCase())) return;
        CUSTOM_ENTITIES.put(descriptor.getId(), descriptor);
        if (descriptor.getStorage() != null) {
            EntityStorage.bind(descriptor.getStorage());
        }
    }

    public static CustomEntityDescriptor getCustomEntityDescriptor(String id) {
        return CUSTOM_ENTITIES.get(id.toLowerCase());
    }

    public static boolean isCustomEntityRegistered(String id) {
        return CUSTOM_ENTITIES.containsKey(id.toLowerCase());
    }

    public static List<CustomEntityDescriptor> getRegisteredCustomEntities() {
        List<CustomEntityDescriptor> entities = new ArrayList<>();
        CUSTOM_ENTITIES.values().forEach(entities::add);
        return Collections.unmodifiableList(entities);
    }
}
