package net.glowstone.sponge;

import com.google.common.collect.ImmutableMap;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;

public class ConstantEntityTypes {

    private ConstantEntityTypes() { }

    public static void setEntityTypes() {

        ImmutableMap.Builder<String, EntityType> types = ImmutableMap.builder();

        types.put("item", (EntityType) (Object) org.bukkit.entity.EntityType.DROPPED_ITEM);
        types.put("experience_orb", (EntityType) (Object) org.bukkit.entity.EntityType.EXPERIENCE_ORB);
        types.put("leash_hitch", (EntityType) (Object) org.bukkit.entity.EntityType.LEASH_HITCH);
        types.put("painting", (EntityType) (Object) org.bukkit.entity.EntityType.PAINTING);
        types.put("arrow", (EntityType) (Object) org.bukkit.entity.EntityType.ARROW);
        types.put("snowball", (EntityType) (Object) org.bukkit.entity.EntityType.SNOWBALL);
        types.put("fireball", (EntityType) (Object) org.bukkit.entity.EntityType.FIREBALL);
        types.put("small_fireball", (EntityType) (Object) org.bukkit.entity.EntityType.SMALL_FIREBALL);
        types.put("ender_pearl", (EntityType) (Object) org.bukkit.entity.EntityType.ENDER_PEARL);
        types.put("eye_of_ender", (EntityType) (Object) org.bukkit.entity.EntityType.ENDER_SIGNAL);
        types.put("thrown_exp_bottle", (EntityType) (Object) org.bukkit.entity.EntityType.THROWN_EXP_BOTTLE);
        types.put("item_frame", (EntityType) (Object) org.bukkit.entity.EntityType.ITEM_FRAME);
        types.put("wither_skull", (EntityType) (Object) org.bukkit.entity.EntityType.WITHER_SKULL);
        types.put("primed_tnt", (EntityType) (Object) org.bukkit.entity.EntityType.PRIMED_TNT);
        types.put("falling_block", (EntityType) (Object) org.bukkit.entity.EntityType.FALLING_BLOCK);
        types.put("firework", (EntityType) (Object) org.bukkit.entity.EntityType.FIREWORK);
        types.put("commandblock_minecart", (EntityType) (Object) org.bukkit.entity.EntityType.MINECART_COMMAND);
        types.put("armor_stand", (EntityType) (Object) org.bukkit.entity.EntityType.ARMOR_STAND);
        types.put("boat", (EntityType) (Object) org.bukkit.entity.EntityType.BOAT);
        types.put("rideable_minecart", (EntityType) (Object) org.bukkit.entity.EntityType.MINECART);
        types.put("chested_minecart", (EntityType) (Object) org.bukkit.entity.EntityType.MINECART_CHEST);
        types.put("furnace_minecart", (EntityType) (Object) org.bukkit.entity.EntityType.MINECART_FURNACE);
        types.put("tnt_minecart", (EntityType) (Object) org.bukkit.entity.EntityType.MINECART_TNT);
        types.put("hopper_minecart", (EntityType) (Object) org.bukkit.entity.EntityType.MINECART_HOPPER);
        types.put("mob_spawner_minecart", (EntityType) (Object) org.bukkit.entity.EntityType.MINECART_MOB_SPAWNER);
        types.put("creeper", (EntityType) (Object) org.bukkit.entity.EntityType.CREEPER);
        types.put("skeleton", (EntityType) (Object) org.bukkit.entity.EntityType.SKELETON);
        types.put("spider", (EntityType) (Object) org.bukkit.entity.EntityType.SPIDER);
        types.put("giant", (EntityType) (Object) org.bukkit.entity.EntityType.GIANT);
        types.put("zombie", (EntityType) (Object) org.bukkit.entity.EntityType.ZOMBIE);
        types.put("slime", (EntityType) (Object) org.bukkit.entity.EntityType.SLIME);
        types.put("ghast", (EntityType) (Object) org.bukkit.entity.EntityType.GHAST);
        types.put("pig_zombie", (EntityType) (Object) org.bukkit.entity.EntityType.PIG_ZOMBIE);
        types.put("enderman", (EntityType) (Object) org.bukkit.entity.EntityType.ENDERMAN);
        types.put("cave_spider", (EntityType) (Object) org.bukkit.entity.EntityType.CAVE_SPIDER);
        types.put("silverfish", (EntityType) (Object) org.bukkit.entity.EntityType.SILVERFISH);
        types.put("blaze", (EntityType) (Object) org.bukkit.entity.EntityType.BLAZE);
        types.put("magma_cube", (EntityType) (Object) org.bukkit.entity.EntityType.MAGMA_CUBE);
        types.put("ender_dragon", (EntityType) (Object) org.bukkit.entity.EntityType.ENDER_DRAGON);
        types.put("wither", (EntityType) (Object) org.bukkit.entity.EntityType.WITHER);
        types.put("bat", (EntityType) (Object) org.bukkit.entity.EntityType.BAT);
        types.put("witch", (EntityType) (Object) org.bukkit.entity.EntityType.WITCH);
        types.put("pig", (EntityType) (Object) org.bukkit.entity.EntityType.PIG);
        types.put("sheep", (EntityType) (Object) org.bukkit.entity.EntityType.SHEEP);
        types.put("cow", (EntityType) (Object) org.bukkit.entity.EntityType.COW);
        types.put("chicken", (EntityType) (Object) org.bukkit.entity.EntityType.CHICKEN);
        types.put("squid", (EntityType) (Object) org.bukkit.entity.EntityType.SQUID);
        types.put("wolf", (EntityType) (Object) org.bukkit.entity.EntityType.WOLF);
        types.put("mushroom_cow", (EntityType) (Object) org.bukkit.entity.EntityType.MUSHROOM_COW);
        types.put("snowman", (EntityType) (Object) org.bukkit.entity.EntityType.SNOWMAN);
        types.put("ocelot", (EntityType) (Object) org.bukkit.entity.EntityType.OCELOT);
        types.put("iron_golem", (EntityType) (Object) org.bukkit.entity.EntityType.IRON_GOLEM);
        types.put("horse", (EntityType) (Object) org.bukkit.entity.EntityType.HORSE);
        types.put("villager", (EntityType) (Object) org.bukkit.entity.EntityType.VILLAGER);
        types.put("ender_crystal", (EntityType) (Object) org.bukkit.entity.EntityType.ENDER_CRYSTAL);
        types.put("splash_potion", (EntityType) (Object) org.bukkit.entity.EntityType.SPLASH_POTION);
        types.put("egg", (EntityType) (Object) org.bukkit.entity.EntityType.EGG);
        types.put("fishing_hook", (EntityType) (Object) org.bukkit.entity.EntityType.FISHING_HOOK);
        types.put("lightning", (EntityType) (Object) org.bukkit.entity.EntityType.LIGHTNING);
        types.put("weather", (EntityType) (Object) org.bukkit.entity.EntityType.WEATHER);
        types.put("player", (EntityType) (Object) org.bukkit.entity.EntityType.PLAYER);
        types.put("complex_part", (EntityType) (Object) org.bukkit.entity.EntityType.COMPLEX_PART);
        types.put("guardian", (EntityType) (Object) org.bukkit.entity.EntityType.GUARDIAN);
        types.put("rabbit", (EntityType) (Object) org.bukkit.entity.EntityType.RABBIT);
        types.put("endermite", (EntityType) (Object) org.bukkit.entity.EntityType.ENDERMITE);
        types.put("unknown", (EntityType) (Object) org.bukkit.entity.EntityType.UNKNOWN);
        types.put("human", (EntityType) (Object) org.bukkit.entity.EntityType.PLAYER); // idk ?
        RegistryHelper.mapFields(EntityTypes.class, types.build());

    }
}
