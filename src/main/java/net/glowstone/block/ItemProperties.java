package net.glowstone.block;

import org.bukkit.Material;

import java.util.Arrays;

/**
 * An enum containing an entry for every block describing that block's physical properties.
 */
public enum ItemProperties {

    IRON_SPADE(Material.IRON_SPADE, nbtData()),
    IRON_PICKAXE(Material.IRON_PICKAXE, nbtData()),
    IRON_AXE(Material.IRON_AXE, nbtData()),
    FLINT_AND_STEEL(Material.FLINT_AND_STEEL),
    APPLE(Material.APPLE),
    BOW(Material.BOW),
    ARROW(Material.ARROW),
    COAL(Material.COAL),
    DIAMOND(Material.DIAMOND),
    IRON_INGOT(Material.IRON_INGOT),
    GOLD_INGOT(Material.GOLD_INGOT),
    IRON_SWORD(Material.IRON_SWORD, nbtData()),
    WOOD_SWORD(Material.WOOD_SWORD, nbtData()),
    WOOD_SPADE(Material.WOOD_SPADE, nbtData()),
    WOOD_PICKAXE(Material.WOOD_PICKAXE, nbtData()),
    WOOD_AXE(Material.WOOD_AXE, nbtData()),
    STONE_SWORD(Material.STONE_SWORD, nbtData()),
    STONE_SPADE(Material.STONE_SPADE, nbtData()),
    STONE_PICKAXE(Material.STONE_PICKAXE, nbtData()),
    STONE_AXE(Material.STONE_AXE, nbtData()),
    DIAMOND_SWORD(Material.DIAMOND_SWORD, nbtData()),
    DIAMOND_SPADE(Material.DIAMOND_SPADE, nbtData()),
    DIAMOND_PICKAXE(Material.DIAMOND_PICKAXE, nbtData()),
    DIAMOND_AXE(Material.DIAMOND_AXE, nbtData()),
    STICK(Material.STICK),
    BOWL(Material.BOWL),
    MUSHROOM_SOUP(Material.MUSHROOM_SOUP),
    GOLD_SWORD(Material.GOLD_SWORD, nbtData()),
    GOLD_SPADE(Material.GOLD_SPADE, nbtData()),
    GOLD_PICKAXE(Material.GOLD_PICKAXE, nbtData()),
    GOLD_AXE(Material.GOLD_AXE, nbtData()),
    STRING(Material.STRING),
    FEATHER(Material.FEATHER),
    SULPHUR(Material.SULPHUR),
    WOOD_HOE(Material.WOOD_HOE, nbtData()),
    STONE_HOE(Material.STONE_HOE, nbtData()),
    IRON_HOE(Material.IRON_HOE, nbtData()),
    DIAMOND_HOE(Material.DIAMOND_HOE, nbtData()),
    GOLD_HOE(Material.GOLD_HOE, nbtData()),
    SEEDS(Material.SEEDS),
    WHEAT(Material.WHEAT),
    BREAD(Material.BREAD),
    LEATHER_HELMET(Material.LEATHER_HELMET, nbtData()),
    LEATHER_CHESTPLATE(Material.LEATHER_CHESTPLATE, nbtData()),
    LEATHER_LEGGINGS(Material.LEATHER_LEGGINGS, nbtData()),
    LEATHER_BOOTS(Material.LEATHER_BOOTS, nbtData()),
    CHAINMAIL_HELMET(Material.CHAINMAIL_HELMET, nbtData()),
    CHAINMAIL_CHESTPLATE(Material.CHAINMAIL_CHESTPLATE, nbtData()),
    CHAINMAIL_LEGGINGS(Material.CHAINMAIL_LEGGINGS, nbtData()),
    CHAINMAIL_BOOTS(Material.CHAINMAIL_BOOTS, nbtData()),
    IRON_HELMET(Material.IRON_HELMET, nbtData()),
    IRON_CHESTPLATE(Material.IRON_CHESTPLATE, nbtData()),
    IRON_LEGGINGS(Material.IRON_LEGGINGS, nbtData()),
    IRON_BOOTS(Material.IRON_BOOTS, nbtData()),
    DIAMOND_HELMET(Material.DIAMOND_HELMET, nbtData()),
    DIAMOND_CHESTPLATE(Material.DIAMOND_CHESTPLATE, nbtData()),
    DIAMOND_LEGGINGS(Material.DIAMOND_LEGGINGS, nbtData()),
    DIAMOND_BOOTS(Material.DIAMOND_BOOTS, nbtData()),
    GOLD_HELMET(Material.GOLD_HELMET, nbtData()),
    GOLD_CHESTPLATE(Material.GOLD_CHESTPLATE, nbtData()),
    GOLD_LEGGINGS(Material.GOLD_LEGGINGS, nbtData()),
    GOLD_BOOTS(Material.GOLD_BOOTS, nbtData()),
    FLINT(Material.FLINT),
    PORK(Material.PORK),
    GRILLED_PORK(Material.GRILLED_PORK),
    PAINTING(Material.PAINTING),
    GOLDEN_APPLE(Material.GOLDEN_APPLE),
    SIGN(Material.SIGN),
    WOOD_DOOR(Material.WOOD_DOOR),
    BUCKET(Material.BUCKET),
    WATER_BUCKET(Material.WATER_BUCKET),
    LAVA_BUCKET(Material.LAVA_BUCKET),
    MINECART(Material.MINECART),
    SADDLE(Material.SADDLE),
    IRON_DOOR(Material.IRON_DOOR),
    REDSTONE(Material.REDSTONE),
    SNOW_BALL(Material.SNOW_BALL),
    BOAT(Material.BOAT),
    LEATHER(Material.LEATHER),
    MILK_BUCKET(Material.MILK_BUCKET),
    CLAY_BRICK(Material.CLAY_BRICK),
    CLAY_BALL(Material.CLAY_BALL),
    SUGAR_CANE(Material.SUGAR_CANE),
    PAPER(Material.PAINTING),
    BOOK(Material.BOOK),
    SLIME_BALL(Material.SLIME_BALL),
    STORAGE_MINECART(Material.STORAGE_MINECART),
    POWERED_MINECART(Material.POWERED_MINECART),
    EGG(Material.EGG),
    COMPASS(Material.COMPASS),
    FISHING_ROD(Material.FISHING_ROD, nbtData()),
    WATCH(Material.GLOWSTONE_DUST),
    GLOWSTONE_DUST(Material.GLOWSTONE_DUST),
    RAW_FISH(Material.RAW_FISH),
    COOKED_FISH(Material.COOKED_FISH),
    INK_SACK(Material.INK_SACK),
    BONE(Material.BONE),
    SUGAR(Material.SUGAR),
    CAKE(Material.CAKE),
    BED(Material.BED),
    DIODE(Material.DIODE),
    COOKIE(Material.COOKIE),
    MAP(Material.MAP),
    SHEARS(Material.SHEARS, nbtData()),
    MELON(Material.MELON),
    PUMPKIN_SEEDS(Material.PUMPKIN_SEEDS),
    MELON_SEEDS(Material.MELON_SEEDS),
    RAW_BEEF(Material.RAW_BEEF),
    COOKED_BEEF(Material.COOKED_BEEF),
    RAW_CHICKEN(Material.RAW_CHICKEN),
    COOKED_CHICKEN(Material.COOKED_CHICKEN),
    ROTTEN_FLESH(Material.ROTTEN_FLESH),
    ENDER_PEARL(Material.ENDER_PEARL),
    BLAZE_ROD(Material.BLAZE_ROD),
    GHAST_TEAR(Material.GHAST_TEAR),
    GOLD_NUGGET(Material.GOLD_NUGGET),
    NETHER_STALK(Material.NETHER_STALK),
    POTION(Material.POTION),
    GLASS_BOTTLE(Material.GLASS_BOTTLE),
    SPIDER_EYE(Material.SPIDER_EYE),
    FERMENTED_SPIDER_EYE(Material.FERMENTED_SPIDER_EYE),
    BLAZE_POWDER(Material.BLAZE_POWDER),
    MAGMA_CREAM(Material.MAGMA_CREAM),
    BREWING_STAND_ITEM(Material.BREWING_STAND),
    CAULDRON_ITEM(Material.CAULDRON),
    EYE_OF_ENDER(Material.EYE_OF_ENDER),
    GLISTERING_MELON(Material.SPECKLED_MELON),
    DISC_13(Material.GOLD_RECORD),
    DISC_CAT(Material.GREEN_RECORD),
    DISC_BLOCKS(Material.RECORD_3),
    DISC_CHIRP(Material.RECORD_4),
    DISC_FAR(Material.RECORD_5),
    DISC_MALL(Material.RECORD_6),
    DISC_MELLOHI(Material.RECORD_7),
    DISC_STAL(Material.RECORD_8),
    DISC_STRAD(Material.RECORD_9),
    DISC_WARD(Material.RECORD_10),
    DISC_11(Material.RECORD_11);

    // -----------------

    private static ItemProperties[] byId = new ItemProperties[32000];

    static {
        for (ItemProperties prop : values()) {
            if (byId.length > prop.id) {
                byId[prop.id] = prop;
            } else {
                byId = Arrays.copyOf(byId, prop.id + 2);
                byId[prop.id] = prop;
            }
        }
    }

    public static ItemProperties get(Material material) {
        return get(material.getId());
    }

    public static ItemProperties get(int id) {
        if (byId.length > id) {
            return byId[id];
        } else {
            return null;
        }
    }

    // -----------------


    private final int id;
    private boolean nbtData;

    private ItemProperties(Material mat, Property... props) {
        id = mat.getId();
        
        for (Property p : props) {
            p.apply(this);
        }
    }
    
    public boolean hasNbtData() {
        return nbtData;
    }

    public int getId() {
        return id;
    }
    
    // -----------------
    
    private interface Property {
        void apply(ItemProperties prop);
    }
    
    private static Property nbtData() {
        return new Property() { public void apply(ItemProperties p) {
            p.nbtData = true;
        }};
    }
}
