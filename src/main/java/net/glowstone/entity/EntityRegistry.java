package net.glowstone.entity;

import com.google.common.collect.ImmutableBiMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.bukkit.entity.*;

public class EntityRegistry {

    private static final Map<String, CustomEntityDescriptor> CUSTOM_ENTITIES = new HashMap<>();

    private static final ImmutableBiMap<Class<? extends Entity>, Class<? extends GlowEntity>> ENTITIES =
        ImmutableBiMap.<Class<? extends Entity>, Class<? extends GlowEntity>>builder()
            .put(AbstractHorse.class, GlowAbstractHorse.class)
            .put(AreaEffectCloud.class, GlowAreaEffectCloud.class)
            .put(ArmorStand.class, GlowArmorStand.class)
            .put(Arrow.class, GlowArrow.class)
            .put(Bat.class, GlowBat.class)
            .put(Blaze.class, GlowBlaze.class)
            .put(Boat.class, GlowBoat.class)
            .put(CaveSpider.class, GlowCaveSpider.class)
            .put(ChestedHorse.class, GlowChestedHorse.class)
            .put(Chicken.class, GlowChicken.class)
            .put(Cow.class, GlowCow.class)
            .put(Creeper.class, GlowCreeper.class)
            .put(Donkey.class, GlowDonkey.class)
            .put(Egg.class, GlowEgg.class)
            .put(ElderGuardian.class, GlowElderGuardian.class)
            .put(EnderCrystal.class, GlowEnderCrystal.class)
            .put(EnderDragon.class, GlowEnderDragon.class)
            .put(EnderPearl.class, GlowEnderPearl.class)
            //TODO: Ender Signal
            .put(Enderman.class, GlowEnderman.class)
            .put(Endermite.class, GlowEndermite.class)
            .put(ExperienceOrb.class, GlowExperienceOrb.class)
            .put(Evoker.class, GlowEvoker.class)
            .put(EvokerFangs.class, GlowEvokerFangs.class)
            .put(FallingBlock.class, GlowFallingBlock.class)
            .put(Fireball.class, GlowFireball.class)
            .put(Firework.class, GlowFirework.class)
            //TODO: Fishing hook
            .put(Ghast.class, GlowGhast.class)
            .put(Giant.class, GlowGiant.class)
            .put(Guardian.class, GlowGuardian.class)
            .put(Horse.class, GlowHorse.class)
            .put(Husk.class, GlowHusk.class)
            .put(IronGolem.class, GlowIronGolem.class)
            .put(Item.class, GlowItem.class)
            .put(ItemFrame.class, GlowItemFrame.class)
            .put(LeashHitch.class, GlowLeashHitch.class)
            .put(LightningStrike.class, GlowLightningStrike.class)
            .put(LingeringPotion.class, GlowLingeringPotion.class)
            .put(Llama.class, GlowLlama.class)
            .put(MagmaCube.class, GlowMagmaCube.class)
            .put(GlowMinecart.MinecartType.RIDEABLE.getEntityClass(),
                GlowMinecart.MinecartType.RIDEABLE.getMinecartClass())
            .put(GlowMinecart.MinecartType.CHEST.getEntityClass(),
                GlowMinecart.MinecartType.CHEST.getMinecartClass())
            .put(GlowMinecart.MinecartType.FURNACE.getEntityClass(),
                GlowMinecart.MinecartType.FURNACE.getMinecartClass())
            .put(GlowMinecart.MinecartType.TNT.getEntityClass(),
                GlowMinecart.MinecartType.TNT.getMinecartClass())
            .put(GlowMinecart.MinecartType.HOPPER.getEntityClass(),
                GlowMinecart.MinecartType.HOPPER.getMinecartClass())
            .put(GlowMinecart.MinecartType.SPAWNER.getEntityClass(),
                GlowMinecart.MinecartType.SPAWNER.getMinecartClass())
            //TODO: Command Block minecart
            .put(Mule.class, GlowMule.class)
            .put(MushroomCow.class, GlowMooshroom.class)
            .put(Ocelot.class, GlowOcelot.class)
            .put(Painting.class, GlowPainting.class)
            .put(Parrot.class, GlowParrot.class)
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
            .put(Snowball.class, GlowSnowball.class)
            .put(Snowman.class, GlowSnowman.class)
            .put(SpectralArrow.class, GlowSpectralArrow.class)
            .put(Spider.class, GlowSpider.class)
            .put(SplashPotion.class, GlowSplashPotion.class)
            .put(Squid.class, GlowSquid.class)
            .put(Stray.class, GlowStray.class)
            .put(ThrownExpBottle.class, GlowThrownExpBottle.class)
            .put(TippedArrow.class, GlowTippedArrow.class)
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
