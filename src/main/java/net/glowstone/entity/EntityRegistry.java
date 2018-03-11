package net.glowstone.entity;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.glowstone.entity.monster.GlowBlaze;
import net.glowstone.entity.monster.GlowCaveSpider;
import net.glowstone.entity.monster.GlowCreeper;
import net.glowstone.entity.monster.GlowElderGuardian;
import net.glowstone.entity.monster.GlowEnderman;
import net.glowstone.entity.monster.GlowEndermite;
import net.glowstone.entity.monster.GlowEvoker;
import net.glowstone.entity.monster.GlowGhast;
import net.glowstone.entity.monster.GlowGiant;
import net.glowstone.entity.monster.GlowGuardian;
import net.glowstone.entity.monster.GlowHusk;
import net.glowstone.entity.monster.GlowIronGolem;
import net.glowstone.entity.monster.GlowMagmaCube;
import net.glowstone.entity.monster.GlowPigZombie;
import net.glowstone.entity.monster.GlowShulker;
import net.glowstone.entity.monster.GlowSilverfish;
import net.glowstone.entity.monster.GlowSkeleton;
import net.glowstone.entity.monster.GlowSlime;
import net.glowstone.entity.monster.GlowSnowman;
import net.glowstone.entity.monster.GlowSpider;
import net.glowstone.entity.monster.GlowStray;
import net.glowstone.entity.monster.GlowVex;
import net.glowstone.entity.monster.GlowVindicator;
import net.glowstone.entity.monster.GlowWitch;
import net.glowstone.entity.monster.GlowWither;
import net.glowstone.entity.monster.GlowWitherSkeleton;
import net.glowstone.entity.monster.GlowZombie;
import net.glowstone.entity.monster.GlowZombieVillager;
import net.glowstone.entity.monster.complex.GlowEnderDragon;
import net.glowstone.entity.objects.GlowArmorStand;
import net.glowstone.entity.objects.GlowBoat;
import net.glowstone.entity.objects.GlowEnderCrystal;
import net.glowstone.entity.objects.GlowEvokerFangs;
import net.glowstone.entity.objects.GlowExperienceOrb;
import net.glowstone.entity.objects.GlowFallingBlock;
import net.glowstone.entity.objects.GlowItem;
import net.glowstone.entity.objects.GlowItemFrame;
import net.glowstone.entity.objects.GlowLeashHitch;
import net.glowstone.entity.objects.GlowMinecart;
import net.glowstone.entity.objects.GlowPainting;
import net.glowstone.entity.passive.GlowAbstractHorse;
import net.glowstone.entity.passive.GlowBat;
import net.glowstone.entity.passive.GlowChestedHorse;
import net.glowstone.entity.passive.GlowChicken;
import net.glowstone.entity.passive.GlowCow;
import net.glowstone.entity.passive.GlowDonkey;
import net.glowstone.entity.passive.GlowFirework;
import net.glowstone.entity.passive.GlowFishingHook;
import net.glowstone.entity.passive.GlowHorse;
import net.glowstone.entity.passive.GlowLlama;
import net.glowstone.entity.passive.GlowMooshroom;
import net.glowstone.entity.passive.GlowMule;
import net.glowstone.entity.passive.GlowOcelot;
import net.glowstone.entity.passive.GlowParrot;
import net.glowstone.entity.passive.GlowPig;
import net.glowstone.entity.passive.GlowPolarBear;
import net.glowstone.entity.passive.GlowRabbit;
import net.glowstone.entity.passive.GlowSheep;
import net.glowstone.entity.passive.GlowSkeletonHorse;
import net.glowstone.entity.passive.GlowSquid;
import net.glowstone.entity.passive.GlowVillager;
import net.glowstone.entity.passive.GlowWolf;
import net.glowstone.entity.passive.GlowZombieHorse;
import net.glowstone.entity.projectile.GlowArrow;
import net.glowstone.entity.projectile.GlowEgg;
import net.glowstone.entity.projectile.GlowEnderPearl;
import net.glowstone.entity.projectile.GlowFireball;
import net.glowstone.entity.projectile.GlowLingeringPotion;
import net.glowstone.entity.projectile.GlowSnowball;
import net.glowstone.entity.projectile.GlowSpectralArrow;
import net.glowstone.entity.projectile.GlowSplashPotion;
import net.glowstone.entity.projectile.GlowThrownExpBottle;
import net.glowstone.entity.projectile.GlowTippedArrow;
import net.glowstone.io.entity.EntityStorage;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Boat;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Donkey;
import org.bukkit.entity.Egg;
import org.bukkit.entity.ElderGuardian;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Firework;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Husk;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LeashHitch;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LingeringPotion;
import org.bukkit.entity.Llama;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Mule;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.PolarBear;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.Spider;
import org.bukkit.entity.SplashPotion;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Stray;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.entity.TippedArrow;
import org.bukkit.entity.Vex;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Vindicator;
import org.bukkit.entity.Weather;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieHorse;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.entity.minecart.SpawnerMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.ItemStack;

public class EntityRegistry {

    private static final Map<String, CustomEntityDescriptor> CUSTOM_ENTITIES = new HashMap<>();
    private static final Map<Class<? extends Entity>, CustomEntityDescriptor>
            CUSTOM_ENTITIES_BY_CLASS = new HashMap<>();

    private static final ImmutableBiMap<Class<? extends Entity>, Class<? extends GlowEntity>>
            ENTITIES;
    private static ImmutableBiMap.Builder<Class<? extends Entity>, Class<? extends GlowEntity>>
            entitiesBuilder = ImmutableBiMap.builder();

    private static final ImmutableMap
            <Class<? extends Entity>, Function<Location, ? extends GlowEntity>>
            ENTITY_CTORS;
    private static final ImmutableMap.Builder
            <Class<? extends Entity>, Function<Location, ? extends GlowEntity>>
            entityCtorsBuilder = ImmutableMap.builder();

    private static <B extends Entity, G extends GlowEntity> void put(
            Class<B> bukkitClass,
            Class<G> glowstoneClass,
            Function<Location, ? extends G> constructor) {
        entitiesBuilder.put(bukkitClass, glowstoneClass);
        entityCtorsBuilder.put(bukkitClass, constructor);
    }

    static {
        put(AbstractHorse.class, GlowAbstractHorse.class, GlowHorse::new);
        put(AreaEffectCloud.class, GlowAreaEffectCloud.class, GlowAreaEffectCloud::new);
        put(ArmorStand.class, GlowArmorStand.class, GlowArmorStand::new);
        put(Arrow.class, GlowArrow.class, GlowArrow::new);
        put(Bat.class, GlowBat.class, GlowBat::new);
        put(Blaze.class, GlowBlaze.class, GlowBlaze::new);
        put(Boat.class, GlowBoat.class, GlowBoat::new);
        put(CaveSpider.class, GlowCaveSpider.class, GlowCaveSpider::new);
        put(ChestedHorse.class, GlowChestedHorse.class, GlowDonkey::new);
        put(Chicken.class, GlowChicken.class, GlowChicken::new);
        put(Cow.class, GlowCow.class, GlowCow::new);
        put(Creeper.class, GlowCreeper.class, GlowCreeper::new);
        put(Donkey.class, GlowDonkey.class, GlowDonkey::new);
        put(Egg.class, GlowEgg.class, GlowEgg::new);
        put(ElderGuardian.class, GlowElderGuardian.class, GlowElderGuardian::new);
        put(EnderCrystal.class, GlowEnderCrystal.class, GlowEnderCrystal::new);
        put(EnderDragon.class, GlowEnderDragon.class, GlowEnderDragon::new);
        put(EnderPearl.class, GlowEnderPearl.class, GlowEnderPearl::new);
        //TODO: Ender Signal
        put(Enderman.class, GlowEnderman.class, GlowEnderman::new);
        put(Endermite.class, GlowEndermite.class, GlowEndermite::new);
        put(ExperienceOrb.class, GlowExperienceOrb.class, GlowExperienceOrb::new);
        put(Evoker.class, GlowEvoker.class, GlowEvoker::new);
        put(EvokerFangs.class, GlowEvokerFangs.class, GlowEvokerFangs::new);
        put(FallingBlock.class, GlowFallingBlock.class, location -> new GlowFallingBlock(location,
                Material.GRAVEL, (byte) 0));
        put(Fireball.class, GlowFireball.class, GlowFireball::new);
        put(Firework.class, GlowFirework.class, GlowFirework::new);
        put(FishHook.class, GlowFishingHook.class,
            location -> new GlowFishingHook(location, null, null));
        put(Ghast.class, GlowGhast.class, GlowGhast::new);
        put(Giant.class, GlowGiant.class, GlowGiant::new);
        put(Guardian.class, GlowGuardian.class, GlowGuardian::new);
        put(Horse.class, GlowHorse.class, GlowHorse::new);
        put(Husk.class, GlowHusk.class, GlowHusk::new);
        put(IronGolem.class, GlowIronGolem.class, GlowIronGolem::new);
        put(Item.class, GlowItem.class,
            location -> new GlowItem(location, new ItemStack(Material.DIRT)));
        put(ItemFrame.class, GlowItemFrame.class,
            location -> new GlowItemFrame(null, location, BlockFace.SOUTH));
        put(LeashHitch.class, GlowLeashHitch.class, GlowLeashHitch::new);
        put(LightningStrike.class, GlowLightningStrike.class, GlowLightningStrike::new);
        put(LingeringPotion.class, GlowLingeringPotion.class, GlowLingeringPotion::new);
        put(Llama.class, GlowLlama.class, GlowLlama::new);
        put(MagmaCube.class, GlowMagmaCube.class, GlowMagmaCube::new);
        put(RideableMinecart.class, GlowMinecart.Rideable.class,
            location -> (GlowMinecart.Rideable)
                    GlowMinecart.create(location, GlowMinecart.MinecartType.RIDEABLE));
        put(StorageMinecart.class, GlowMinecart.Storage.class,
            location -> (GlowMinecart.Storage)
                    GlowMinecart.create(location, GlowMinecart.MinecartType.CHEST));
        put(PoweredMinecart.class, GlowMinecart.Powered.class,
            location -> (GlowMinecart.Powered)
                    GlowMinecart.create(location, GlowMinecart.MinecartType.FURNACE));
        put(ExplosiveMinecart.class, GlowMinecart.Explosive.class,
            location -> (GlowMinecart.Explosive)
                    GlowMinecart.create(location, GlowMinecart.MinecartType.TNT));
        put(HopperMinecart.class, GlowMinecart.Hopper.class,
            location -> (GlowMinecart.Hopper)
                    GlowMinecart.create(location, GlowMinecart.MinecartType.HOPPER));
        put(SpawnerMinecart.class, GlowMinecart.Spawner.class,
            location -> (GlowMinecart.Spawner)
                    GlowMinecart.create(location, GlowMinecart.MinecartType.SPAWNER));
        put(CommandMinecart.class, GlowMinecart.Command.class,
            location -> (GlowMinecart.Command)
                    GlowMinecart.create(location, GlowMinecart.MinecartType.COMMAND));
        put(Mule.class, GlowMule.class, GlowMule::new);
        put(MushroomCow.class, GlowMooshroom.class, GlowMooshroom::new);
        put(Ocelot.class, GlowOcelot.class, GlowOcelot::new);
        put(Painting.class, GlowPainting.class, GlowPainting::new);
        put(Parrot.class, GlowParrot.class, GlowParrot::new);
        put(Pig.class, GlowPig.class, GlowPig::new);
        put(PigZombie.class, GlowPigZombie.class, GlowPigZombie::new);
        put(Player.class, GlowPlayer.class, location -> {
            throw new UnsupportedOperationException(
                    "Attempt to spawn a Player with no associated login");
        });
        put(PolarBear.class, GlowPolarBear.class, GlowPolarBear::new);
        put(TNTPrimed.class, GlowTntPrimed.class, GlowTntPrimed::new);
        put(Rabbit.class, GlowRabbit.class, GlowRabbit::new);
        put(Sheep.class, GlowSheep.class, GlowSheep::new);
        put(Shulker.class, GlowShulker.class, GlowShulker::new);
        put(Silverfish.class, GlowSilverfish.class, GlowSilverfish::new);
        put(Skeleton.class, GlowSkeleton.class, GlowSkeleton::new);
        put(SkeletonHorse.class, GlowSkeletonHorse.class, GlowSkeletonHorse::new);
        put(Slime.class, GlowSlime.class, GlowSlime::new);
        put(Snowball.class, GlowSnowball.class, GlowSnowball::new);
        put(Snowman.class, GlowSnowman.class, GlowSnowman::new);
        put(SpectralArrow.class, GlowSpectralArrow.class, GlowSpectralArrow::new);
        put(Spider.class, GlowSpider.class, GlowSpider::new);
        put(SplashPotion.class, GlowSplashPotion.class, GlowSplashPotion::new);
        put(Squid.class, GlowSquid.class, GlowSquid::new);
        put(Stray.class, GlowStray.class, GlowStray::new);
        put(ThrownExpBottle.class, GlowThrownExpBottle.class, GlowThrownExpBottle::new);
        put(TippedArrow.class, GlowTippedArrow.class, GlowTippedArrow::new);
        put(Vex.class, GlowVex.class, GlowVex::new);
        put(Villager.class, GlowVillager.class, GlowVillager::new);
        put(Vindicator.class, GlowVindicator.class, GlowVindicator::new);
        put(Weather.class, GlowWeather.class, GlowLightningStrike::new);
        put(Witch.class, GlowWitch.class, GlowWitch::new);
        put(Wither.class, GlowWither.class, GlowWither::new);
        put(WitherSkeleton.class, GlowWitherSkeleton.class, GlowWitherSkeleton::new);
        //TODO: Wither Skull
        put(Wolf.class, GlowWolf.class, GlowWolf::new);
        put(Zombie.class, GlowZombie.class, GlowZombie::new);
        put(ZombieHorse.class, GlowZombieHorse.class, GlowZombieHorse::new);
        put(ZombieVillager.class, GlowZombieVillager.class, GlowZombieVillager::new);
        ENTITIES = entitiesBuilder.build();
        ENTITY_CTORS = entityCtorsBuilder.build();
    }

    /**
     * Creates an entity of the given type at the given location.
     *
     * @param type     the entity type
     * @param location the location to spawn the entity
     * @return an entity that has not yet been sent to clients
     */
    public static GlowEntity constructEntity(Class<? extends Entity> type, Location location) {
        if (ENTITY_CTORS.containsKey(type)) {
            return ENTITY_CTORS.get(type).apply(location);
        } else if (CUSTOM_ENTITIES_BY_CLASS.containsKey(type)) {
            return CUSTOM_ENTITIES_BY_CLASS.get(type)
                    .getStorage().createEntity(location, new CompoundTag());
        } else {
            Bukkit.getServer().getLogger().severe(
                    "Attempt to spawn unregistered entity type " + type);
            return null;
        }
    }

    public static Class<? extends GlowEntity> getEntity(EntityType type) {
        return ENTITIES.get(type.getEntityClass());
    }

    public static Class<? extends GlowEntity> getEntity(Class<? extends Entity> clazz) {
        return ENTITIES.get(clazz);
    }

    /**
     * Registers a custom entity type.
     *
     * @param descriptor the entity type to register; all fields except
     *                   {@link CustomEntityDescriptor#getStorage()} must be non-null
     */
    public static void registerCustomEntity(
            CustomEntityDescriptor<? extends GlowEntity> descriptor) {
        if (descriptor == null || descriptor.getEntityClass() == null || descriptor.getId() == null
                || descriptor.getPlugin() == null) {
            return;
        }
        if (descriptor.getPlugin().isEnabled()) {
            descriptor.getPlugin().getServer().getLogger().warning(
                    "Cannot register custom entity '" + descriptor.getId() + "' for plugin '"
                            + descriptor.getPlugin() + "', worlds are already loaded.");
            return;
        }
        if (CUSTOM_ENTITIES.containsKey(descriptor.getId().toLowerCase())) {
            return;
        }
        CUSTOM_ENTITIES.put(descriptor.getId(), descriptor);
        CUSTOM_ENTITIES_BY_CLASS.put(descriptor.getEntityClass(), descriptor);
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
        List<CustomEntityDescriptor> entities = new ArrayList<>(CUSTOM_ENTITIES.values());
        return Collections.unmodifiableList(entities);
    }
}
