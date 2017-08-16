package net.glowstone.block;

import net.glowstone.block.blocktype.*;
import net.glowstone.block.itemtype.*;
import net.glowstone.entity.objects.GlowMinecart;
import net.glowstone.inventory.ToolType;
import org.bukkit.Material;
import org.bukkit.Sound;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.TreeSpecies;

/**
 * The lookup table for block and item types.
 */
public final class ItemTable {

    private static final ItemTable INSTANCE = new ItemTable();

    static {
        INSTANCE.registerBuiltins();
    }

    private final Map<Integer, ItemType> idToType = new HashMap<>(512);
    private int nextBlockId, nextItemId;

    ////////////////////////////////////////////////////////////////////////////
    // Data

    private ItemTable() {
    }

    public static ItemTable instance() {
        return INSTANCE;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Registration

    private void registerBuiltins() {
        reg(Material.FLOWER_POT, new BlockFlowerPot());
        reg(Material.JUKEBOX, new BlockJukebox());
        reg(Material.NOTE_BLOCK, new BlockNote());
        reg(Material.MOB_SPAWNER, new BlockMobSpawner());
        reg(Material.MONSTER_EGGS, new BlockMonsterEgg());
        reg(Material.DRAGON_EGG, new BlockFalling(Material.DRAGON_EGG));
        reg(Material.SIGN_POST, new BlockSign(), Sound.BLOCK_WOOD_BREAK);
        reg(Material.WALL_SIGN, new BlockSign(), Sound.BLOCK_WOOD_BREAK);
        reg(Material.WORKBENCH, new BlockWorkbench(), Sound.BLOCK_WOOD_BREAK);
        reg(Material.ENDER_CHEST, new BlockEnderChest());
        reg(Material.CHEST, new BlockChest(), Sound.BLOCK_WOOD_BREAK);
        reg(Material.TRAPPED_CHEST, new BlockChest(true), Sound.BLOCK_WOOD_BREAK);
        reg(Material.DISPENSER, new BlockDispenser());
        reg(Material.DROPPER, new BlockDropper());
        reg(Material.BOOKSHELF, new BlockDirectDrops(Material.BOOK, 3), Sound.BLOCK_WOOD_BREAK);
        reg(Material.CLAY, new BlockDirectDrops(Material.CLAY_BALL, 4), Sound.BLOCK_GRAVEL_BREAK);
        reg(Material.HARD_CLAY, new BlockDirectDrops(ToolType.PICKAXE));
        reg(Material.STAINED_CLAY, new BlockDirectDrops(ToolType.PICKAXE));
        reg(Material.WOODEN_DOOR, new BlockDoor(Material.WOOD_DOOR), Sound.BLOCK_WOOD_BREAK);
        reg(Material.IRON_DOOR_BLOCK, new BlockDoor(Material.IRON_DOOR));
        reg(Material.SPRUCE_DOOR, new BlockDoor(Material.SPRUCE_DOOR_ITEM));
        reg(Material.BIRCH_DOOR, new BlockDoor(Material.BIRCH_DOOR_ITEM));
        reg(Material.JUNGLE_DOOR, new BlockDoor(Material.JUNGLE_DOOR_ITEM));
        reg(Material.ACACIA_DOOR, new BlockDoor(Material.ACACIA_DOOR_ITEM));
        reg(Material.DARK_OAK_DOOR, new BlockDoor(Material.DARK_OAK_DOOR_ITEM));
        reg(Material.DOUBLE_STEP, new BlockDoubleSlab());
        reg(Material.DOUBLE_STONE_SLAB2, new BlockDoubleSlab());
        reg(Material.WOOD_DOUBLE_STEP, new BlockDoubleSlab(), Sound.BLOCK_WOOD_BREAK);
        reg(Material.PURPUR_DOUBLE_SLAB, new BlockDoubleSlab());
        reg(Material.SOIL, new BlockSoil(), Sound.BLOCK_GRAVEL_BREAK);
        reg(Material.GLASS, new BlockDropless());
        reg(Material.THIN_GLASS, new BlockDropless());
        reg(Material.STAINED_GLASS, new BlockDropless());
        reg(Material.STAINED_GLASS_PANE, new BlockDropless());
        reg(Material.GLOWSTONE, new BlockRandomDrops(Material.GLOWSTONE_DUST, 2, 4));
        reg(Material.MYCEL, new BlockMycel(), Sound.BLOCK_GRAVEL_BREAK);
        reg(Material.GRASS, new BlockGrass(), Sound.BLOCK_GRASS_BREAK);
        reg(Material.DIRT, new BlockDirt(), Sound.BLOCK_GRAVEL_BREAK);
        reg(Material.GRAVEL, new BlockGravel(), Sound.BLOCK_GRAVEL_BREAK);
        reg(Material.SAND, new BlockFalling(Material.SAND), Sound.BLOCK_SAND_BREAK);
        reg(Material.ANVIL, new BlockAnvil());
        reg(Material.ICE, new BlockIce());
        reg(Material.PACKED_ICE, new BlockDropless());
        reg(Material.SNOW, new BlockSnow(), Sound.BLOCK_SNOW_BREAK);
        reg(Material.SNOW_BLOCK, new BlockSnowBlock(), Sound.BLOCK_SNOW_BREAK);
        reg(Material.PRISMARINE, new BlockDirectDrops(ToolType.PICKAXE));
        reg(Material.RED_SANDSTONE, new BlockDirectDrops(ToolType.PICKAXE));
        reg(Material.SANDSTONE, new BlockDirectDrops(ToolType.PICKAXE));
        reg(Material.NETHER_BRICK, new BlockDirectDrops(ToolType.PICKAXE));
        reg(Material.NETHER_FENCE, new BlockFence(Material.NETHER_FENCE, ToolType.PICKAXE));
        reg(Material.FENCE, new BlockFence(Material.FENCE));
        reg(Material.NETHERRACK, new BlockDirectDrops(ToolType.PICKAXE));
        reg(Material.IRON_FENCE, new BlockDirectDrops(ToolType.PICKAXE));
        reg(Material.BRICK, new BlockDirectDrops(ToolType.PICKAXE));
        reg(Material.SMOOTH_BRICK, new BlockDirectDrops(ToolType.PICKAXE));
        reg(Material.ENDER_STONE, new BlockDirectDrops(ToolType.PICKAXE));
        reg(Material.COBBLESTONE, new BlockDirectDrops(ToolType.PICKAXE));
        reg(Material.COBBLE_WALL, new BlockDirectDrops(ToolType.PICKAXE));
        reg(Material.MOSSY_COBBLESTONE, new BlockDirectDrops(ToolType.PICKAXE));
        reg(Material.STONE, new BlockStone());
        reg(Material.OBSIDIAN, new BlockDirectDrops(ToolType.DIAMOND_PICKAXE));
        reg(Material.COAL_ORE, new BlockOre(Material.COAL, ToolType.PICKAXE));
        reg(Material.COAL_BLOCK, new BlockDirectDrops(ToolType.PICKAXE));
        reg(Material.IRON_ORE, new BlockDirectDrops(ToolType.STONE_PICKAXE));
        reg(Material.IRON_BLOCK, new BlockDirectDrops(ToolType.STONE_PICKAXE));
        reg(Material.GOLD_ORE, new BlockDirectDrops(ToolType.IRON_PICKAXE));
        reg(Material.GOLD_BLOCK, new BlockDirectDrops(ToolType.IRON_PICKAXE));
        reg(Material.DIAMOND_ORE, new BlockOre(Material.DIAMOND, ToolType.IRON_PICKAXE));
        reg(Material.DIAMOND_BLOCK, new BlockDirectDrops(ToolType.IRON_PICKAXE));
        reg(Material.EMERALD_ORE, new BlockOre(Material.EMERALD, ToolType.IRON_PICKAXE));
        reg(Material.EMERALD_BLOCK, new BlockDirectDrops(ToolType.PICKAXE));
        reg(Material.LAPIS_ORE, new BlockOre(Material.INK_SACK,  ToolType.STONE_PICKAXE, 4, 4, 8));
        reg(Material.LAPIS_BLOCK, new BlockDirectDrops(ToolType.STONE_PICKAXE));
        reg(Material.QUARTZ_ORE, new BlockOre(Material.QUARTZ, ToolType.PICKAXE));
        reg(Material.REDSTONE_ORE, new BlockRedstoneOre());
        reg(Material.GLOWING_REDSTONE_ORE, new BlockLitRedstoneOre());
        reg(Material.REDSTONE_BLOCK, new BlockDirectDrops(ToolType.PICKAXE));
        reg(Material.CARROT, new BlockCarrot(), Sound.BLOCK_GRASS_BREAK);
        reg(Material.COCOA, new BlockCocoa(), Sound.BLOCK_WOOD_BREAK);
        reg(Material.DEAD_BUSH, new BlockDeadBush(), Sound.BLOCK_GRASS_BREAK);
        reg(Material.LONG_GRASS, new BlockTallGrass(), Sound.BLOCK_GRASS_BREAK);
        reg(Material.HUGE_MUSHROOM_1, new BlockHugeMushroom(true), Sound.BLOCK_WOOD_BREAK);
        reg(Material.HUGE_MUSHROOM_2, new BlockHugeMushroom(false), Sound.BLOCK_WOOD_BREAK);
        reg(Material.LEAVES, new BlockLeaves(), Sound.BLOCK_GRASS_BREAK);
        reg(Material.LEAVES_2, new BlockLeaves(), Sound.BLOCK_GRASS_BREAK);
        reg(Material.MELON_BLOCK, new BlockMelon(), Sound.BLOCK_WOOD_BREAK);
        reg(Material.MELON_STEM, new BlockStem(Material.MELON_STEM), Sound.BLOCK_GRASS_BREAK);
        reg(Material.NETHER_WARTS, new BlockNetherWart(), Sound.BLOCK_GRASS_BREAK);
        reg(Material.POTATO, new BlockPotato(), Sound.BLOCK_GRASS_BREAK);
        reg(Material.PUMPKIN_STEM, new BlockStem(Material.PUMPKIN_STEM), Sound.BLOCK_GRASS_BREAK);
        reg(Material.CROPS, new BlockCrops(), Sound.BLOCK_GRASS_BREAK);
        reg(Material.CAKE_BLOCK, new BlockDropless(), Sound.BLOCK_CLOTH_BREAK);
        reg(Material.WEB, new BlockWeb());
        reg(Material.FIRE, new BlockFire());
        reg(Material.ENDER_PORTAL_FRAME, new BlockEnderPortalFrame());
        reg(Material.FENCE_GATE, new BlockFenceGate());
        reg(Material.ACACIA_FENCE_GATE, new BlockFenceGate());
        reg(Material.BIRCH_FENCE_GATE, new BlockFenceGate());
        reg(Material.DARK_OAK_FENCE_GATE, new BlockFenceGate());
        reg(Material.JUNGLE_FENCE_GATE, new BlockFenceGate());
        reg(Material.SPRUCE_FENCE_GATE, new BlockFenceGate());
        reg(Material.TRAP_DOOR, new BlockWoodenTrapDoor(), Sound.BLOCK_WOOD_BREAK);
        reg(Material.IRON_TRAPDOOR, new BlockIronTrapDoor());
        reg(Material.FURNACE, new BlockFurnace());
        reg(Material.BURNING_FURNACE, new BlockFurnace());
        reg(Material.LEVER, new BlockLever());
        reg(Material.HOPPER, new BlockHopper());
        reg(Material.PISTON_BASE, new BlockPiston(false));
        reg(Material.PISTON_STICKY_BASE, new BlockPiston(true));
        reg(Material.ACACIA_STAIRS, new BlockStairs());
        reg(Material.BIRCH_WOOD_STAIRS, new BlockStairs(), Sound.BLOCK_WOOD_BREAK);
        reg(Material.BRICK_STAIRS, new BlockStairs());
        reg(Material.COBBLESTONE_STAIRS, new BlockStairs());
        reg(Material.DARK_OAK_STAIRS, new BlockStairs());
        reg(Material.JUNGLE_WOOD_STAIRS, new BlockStairs(), Sound.BLOCK_WOOD_BREAK);
        reg(Material.NETHER_BRICK_STAIRS, new BlockStairs());
        reg(Material.QUARTZ_STAIRS, new BlockStairs());
        reg(Material.SANDSTONE_STAIRS, new BlockStairs());
        reg(Material.RED_SANDSTONE_STAIRS, new BlockStairs());
        reg(Material.SPRUCE_WOOD_STAIRS, new BlockStairs(), Sound.BLOCK_WOOD_BREAK);
        reg(Material.SMOOTH_STAIRS, new BlockStairs());
        reg(Material.WOOD_STAIRS, new BlockStairs(), Sound.BLOCK_WOOD_BREAK);
        reg(Material.PURPUR_STAIRS, new BlockStairs());
        reg(Material.STEP, new BlockSlab());
        reg(Material.WOOD_STEP, new BlockSlab(), Sound.BLOCK_WOOD_BREAK);
        reg(Material.STONE_SLAB2, new BlockSlab());
        reg(Material.PURPUR_SLAB, new BlockSlab());
        reg(Material.HAY_BLOCK, new BlockHay());
        reg(Material.QUARTZ_BLOCK, new BlockQuartz());
        reg(Material.LOG, new BlockLog(), Sound.BLOCK_WOOD_BREAK);
        reg(Material.LOG_2, new BlockLog2(), Sound.BLOCK_WOOD_BREAK);
        reg(Material.LADDER, new BlockLadder(), Sound.BLOCK_WOOD_BREAK);
        reg(Material.VINE, new BlockVine());
        reg(Material.STONE_BUTTON, new BlockButton(Material.STONE_BUTTON));
        reg(Material.WOOD_BUTTON, new BlockButton(Material.WOOD_BUTTON), Sound.BLOCK_WOOD_BREAK);
        reg(Material.BED_BLOCK, new BlockBed());
        reg(Material.SKULL, new BlockSkull());
        reg(Material.TORCH, new BlockTorch());
        reg(Material.GOLD_PLATE, new BlockDirectDrops(Material.GOLD_PLATE, ToolType.PICKAXE));
        reg(Material.IRON_PLATE, new BlockDirectDrops(Material.IRON_PLATE, ToolType.PICKAXE));
        reg(Material.STONE_PLATE, new BlockDirectDrops(Material.STONE_PLATE, ToolType.PICKAXE));
        reg(Material.DAYLIGHT_DETECTOR, new BlockDaylightDetector());
        reg(Material.DAYLIGHT_DETECTOR_INVERTED, new BlockDaylightDetector());
        reg(Material.YELLOW_FLOWER, new BlockNeedsAttached());
        reg(Material.RED_ROSE, new BlockNeedsAttached());
        reg(Material.BROWN_MUSHROOM, new BlockMushroom(Material.BROWN_MUSHROOM));
        reg(Material.RED_MUSHROOM, new BlockMushroom(Material.RED_MUSHROOM));
        reg(Material.SUGAR_CANE_BLOCK, new BlockSugarCane(), Sound.BLOCK_GRASS_BREAK);
        reg(Material.SAPLING, new BlockSapling());
        reg(Material.RAILS, new BlockRails());
        reg(Material.ACTIVATOR_RAIL, new BlockRails());
        reg(Material.DETECTOR_RAIL, new BlockRails());
        reg(Material.POWERED_RAIL, new BlockRails());
        reg(Material.CARPET, new BlockCarpet(), Sound.BLOCK_CLOTH_BREAK);
        reg(Material.ENCHANTMENT_TABLE, new BlockEnchantmentTable());
        reg(Material.BREWING_STAND, new BlockBrewingStand());
        reg(Material.CACTUS, new BlockCactus());
        reg(Material.WATER, new BlockWater());
        reg(Material.STATIONARY_WATER, new BlockWater());
        reg(Material.LAVA, new BlockLava());
        reg(Material.STATIONARY_LAVA, new BlockLava());
        reg(Material.CAULDRON, new BlockCauldron());
        reg(Material.STANDING_BANNER, new BlockBanner());
        reg(Material.WALL_BANNER, new BlockBanner());
        reg(Material.SPONGE, new BlockSponge());
        reg(Material.TNT, new BlockTNT());
        reg(Material.DOUBLE_PLANT, new BlockDoublePlant());
        reg(Material.PUMPKIN, new BlockPumpkin());
        reg(Material.JACK_O_LANTERN, new BlockPumpkinBase(Material.JACK_O_LANTERN));
        reg(Material.SEA_LANTERN, new BlockRandomDrops(Material.PRISMARINE_CRYSTALS, 2, 3));
        reg(Material.REDSTONE_LAMP_ON, new BlockLamp());
        reg(Material.REDSTONE_LAMP_OFF, new BlockLamp());
        reg(Material.REDSTONE_WIRE, new BlockRedstone());
        reg(Material.REDSTONE_TORCH_ON, new BlockRedstoneTorch());
        reg(Material.REDSTONE_TORCH_OFF, new BlockRedstoneTorch());
        reg(Material.DIODE_BLOCK_ON, new BlockRedstoneRepeater());
        reg(Material.DIODE_BLOCK_OFF, new BlockRedstoneRepeater());
        reg(Material.MAGMA, new BlockMagma());
        reg(Material.NETHER_WART_BLOCK, new BlockDirectDrops(Material.NETHER_WART_BLOCK, ToolType.AXE));
        reg(Material.RED_NETHER_BRICK, new BlockDirectDrops(Material.RED_NETHER_BRICK, ToolType.PICKAXE));
        reg(Material.BONE_BLOCK, new BlockDirectDrops(Material.BONE_BLOCK, ToolType.PICKAXE));
        reg(Material.OBSERVER, new BlockObserver());
        reg(Material.REDSTONE_COMPARATOR_ON, new BlockRedstoneComparator());
        reg(Material.REDSTONE_COMPARATOR_OFF, new BlockRedstoneComparator());
        reg(Material.BEACON, new BlockBeacon());
        reg(Material.PURPUR_PILLAR, new BlockPurpurPillar());
        reg(Material.PURPUR_BLOCK, new BlockDirectDrops(Material.PURPUR_BLOCK, ToolType.PICKAXE));
        reg(Material.END_ROD, new BlockEndRod());
        reg(Material.CONCRETE, new BlockDirectDrops(Material.CONCRETE));
        reg(Material.CONCRETE_POWDER, new BlockConcretePowder());
        reg(Material.WHITE_GLAZED_TERRACOTTA, new BlockDirectDrops(Material.WHITE_GLAZED_TERRACOTTA));
        reg(Material.BLACK_GLAZED_TERRACOTTA, new BlockDirectDrops(Material.BLACK_GLAZED_TERRACOTTA));
        reg(Material.BLUE_GLAZED_TERRACOTTA, new BlockDirectDrops(Material.BLUE_GLAZED_TERRACOTTA));
        reg(Material.BROWN_GLAZED_TERRACOTTA, new BlockDirectDrops(Material.BROWN_GLAZED_TERRACOTTA));
        reg(Material.CYAN_GLAZED_TERRACOTTA, new BlockDirectDrops(Material.CYAN_GLAZED_TERRACOTTA));
        reg(Material.GRAY_GLAZED_TERRACOTTA, new BlockDirectDrops(Material.GRAY_GLAZED_TERRACOTTA));
        reg(Material.GREEN_GLAZED_TERRACOTTA, new BlockDirectDrops(Material.GREEN_GLAZED_TERRACOTTA));
        reg(Material.LIGHT_BLUE_GLAZED_TERRACOTTA, new BlockDirectDrops(Material.LIGHT_BLUE_GLAZED_TERRACOTTA));
        reg(Material.LIME_GLAZED_TERRACOTTA, new BlockDirectDrops(Material.LIME_GLAZED_TERRACOTTA));
        reg(Material.MAGENTA_GLAZED_TERRACOTTA, new BlockDirectDrops(Material.MAGENTA_GLAZED_TERRACOTTA));
        reg(Material.ORANGE_GLAZED_TERRACOTTA, new BlockDirectDrops(Material.ORANGE_GLAZED_TERRACOTTA));
        reg(Material.PINK_GLAZED_TERRACOTTA, new BlockDirectDrops(Material.PINK_GLAZED_TERRACOTTA));
        reg(Material.PURPLE_GLAZED_TERRACOTTA, new BlockDirectDrops(Material.PURPLE_GLAZED_TERRACOTTA));
        reg(Material.RED_GLAZED_TERRACOTTA, new BlockDirectDrops(Material.RED_GLAZED_TERRACOTTA));
        reg(Material.SILVER_GLAZED_TERRACOTTA, new BlockDirectDrops(Material.SILVER_GLAZED_TERRACOTTA));
        reg(Material.YELLOW_GLAZED_TERRACOTTA, new BlockDirectDrops(Material.YELLOW_GLAZED_TERRACOTTA));


        reg(Material.FLINT_AND_STEEL, new ItemFlintAndSteel());
        reg(Material.SIGN, new ItemSign());
        reg(Material.REDSTONE, new ItemPlaceAs(Material.REDSTONE_WIRE));
        reg(Material.SUGAR_CANE, new ItemPlaceAs(Material.SUGAR_CANE_BLOCK));
        reg(Material.DIODE, new ItemPlaceAs(Material.DIODE_BLOCK_OFF));
        reg(Material.BREWING_STAND_ITEM, new ItemPlaceAs(Material.BREWING_STAND));
        reg(Material.CAULDRON_ITEM, new ItemPlaceAs(Material.CAULDRON));
        reg(Material.FLOWER_POT_ITEM, new ItemPlaceAs(Material.FLOWER_POT));
        reg(Material.SKULL_ITEM, new ItemPlaceAs(Material.SKULL));
        reg(Material.REDSTONE_COMPARATOR, new ItemPlaceAs(Material.REDSTONE_COMPARATOR_OFF));
        reg(Material.BED, new ItemPlaceAs(Material.BED_BLOCK));
        reg(Material.BUCKET, new ItemBucket());
        reg(Material.WATER_BUCKET, new ItemFilledBucket(Material.WATER));
        reg(Material.LAVA_BUCKET, new ItemFilledBucket(Material.LAVA));
        reg(Material.WOOD_HOE, new ItemHoe());
        reg(Material.STONE_HOE, new ItemHoe());
        reg(Material.IRON_HOE, new ItemHoe());
        reg(Material.GOLD_HOE, new ItemHoe());
        reg(Material.DIAMOND_HOE, new ItemHoe());
        reg(Material.WOOD_SPADE, new ItemShovel());
        reg(Material.STONE_SPADE, new ItemShovel());
        reg(Material.IRON_SPADE, new ItemShovel());
        reg(Material.GOLD_SPADE, new ItemShovel());
        reg(Material.DIAMOND_SPADE, new ItemShovel());
        reg(Material.MONSTER_EGG, new ItemSpawn());
        reg(Material.SEEDS, new ItemSeeds(Material.CROPS, Material.SOIL));
        reg(Material.MELON_SEEDS, new ItemSeeds(Material.MELON_STEM, Material.SOIL));
        reg(Material.PUMPKIN_SEEDS, new ItemSeeds(Material.PUMPKIN_STEM, Material.SOIL));
        reg(Material.NETHER_STALK, new ItemSeeds(Material.NETHER_WARTS, Material.SOUL_SAND));
        reg(Material.CARROT_ITEM, new ItemFoodSeeds(Material.CARROT, Material.SOIL, 3, 4.8f));
        reg(Material.POTATO_ITEM, new ItemFoodSeeds(Material.POTATO, Material.SOIL, 1, 0.6f));
        reg(Material.INK_SACK, new ItemDye());
        reg(Material.BANNER, new ItemBanner());
        reg(Material.WOOD_DOOR, new ItemPlaceAs(Material.WOODEN_DOOR));
        reg(Material.IRON_DOOR, new ItemPlaceAs(Material.IRON_DOOR_BLOCK));
        reg(Material.SPRUCE_DOOR_ITEM, new ItemPlaceAs(Material.SPRUCE_DOOR));
        reg(Material.BIRCH_DOOR_ITEM, new ItemPlaceAs(Material.BIRCH_DOOR));
        reg(Material.JUNGLE_DOOR_ITEM, new ItemPlaceAs(Material.JUNGLE_DOOR));
        reg(Material.ACACIA_DOOR_ITEM, new ItemPlaceAs(Material.ACACIA_DOOR));
        reg(Material.DARK_OAK_DOOR_ITEM, new ItemPlaceAs(Material.DARK_OAK_DOOR));
        reg(Material.WRITTEN_BOOK, new ItemWrittenBook());
        reg(Material.ITEM_FRAME, new ItemItemFrame());
        reg(Material.APPLE, new ItemFood(4, 12.4f));
        reg(Material.BAKED_POTATO, new ItemFood(5, 7.2f));
        reg(Material.BREAD, new ItemFood(5, 6f));
        reg(Material.COOKED_CHICKEN, new ItemFood(6, 7.2f));
        reg(Material.COOKED_FISH, new ItemFishCooked());
        reg(Material.COOKED_MUTTON, new ItemFood(6, 9.6f));
        reg(Material.COOKED_BEEF, new ItemFood(8, 12.8f));
        reg(Material.COOKED_RABBIT, new ItemFood(5, 6f));
        reg(Material.COOKIE, new ItemFood(2, 0.4f));
        reg(Material.GOLDEN_APPLE, new ItemGoldenApple());
        reg(Material.GOLDEN_CARROT, new ItemFood(6, 14.4f));
        reg(Material.GRILLED_PORK, new ItemFood(8, 12.8f));
        reg(Material.MELON, new ItemFood(2, 1.2f));
        reg(Material.MUSHROOM_SOUP, new ItemFood(6, 7.2f));
        reg(Material.POISONOUS_POTATO, new ItemPoisonousPotato());
        reg(Material.PUMPKIN_PIE, new ItemFood(8, 4.8f));
        reg(Material.RABBIT_STEW, new ItemFood(10, 12f));
        reg(Material.RAW_BEEF, new ItemFood(3, 1.8f));
        reg(Material.RAW_CHICKEN, new ItemRawChicken());
        reg(Material.RAW_FISH, new ItemFishRaw());
        reg(Material.MUTTON, new ItemFood(2, 1.2f));
        reg(Material.PORK, new ItemFood(3, 1.8f));
        reg(Material.RABBIT, new ItemFood(3, 1.8f));
        reg(Material.ROTTEN_FLESH, new ItemRottenFlesh());
        reg(Material.SPIDER_EYE, new ItemFood(2, 3.2f)); // todo: effect
        reg(Material.ARMOR_STAND, new ItemArmorStand());
        reg(Material.MILK_BUCKET, new ItemMilk());
        reg(Material.MINECART, new ItemMinecart(GlowMinecart.MinecartType.RIDEABLE));
        reg(Material.COMMAND_MINECART, new ItemMinecart(GlowMinecart.MinecartType.COMMAND));
        reg(Material.EXPLOSIVE_MINECART, new ItemMinecart(GlowMinecart.MinecartType.TNT));
        reg(Material.HOPPER_MINECART, new ItemMinecart(GlowMinecart.MinecartType.HOPPER));
        reg(Material.POWERED_MINECART, new ItemMinecart(GlowMinecart.MinecartType.FURNACE));
        reg(Material.STORAGE_MINECART, new ItemMinecart(GlowMinecart.MinecartType.CHEST));
        reg(Material.END_CRYSTAL, new ItemEndCrystal());
        reg(Material.BOAT, new ItemBoat(TreeSpecies.GENERIC));
        reg(Material.BOAT_SPRUCE, new ItemBoat(TreeSpecies.REDWOOD));
        reg(Material.BOAT_BIRCH, new ItemBoat(TreeSpecies.BIRCH));
        reg(Material.BOAT_JUNGLE, new ItemBoat(TreeSpecies.JUNGLE));
        reg(Material.BOAT_ACACIA, new ItemBoat(TreeSpecies.ACACIA));
        reg(Material.BOAT_DARK_OAK, new ItemBoat(TreeSpecies.DARK_OAK));
        reg(Material.PAINTING, new ItemPainting());
        reg(Material.FIREWORK, new ItemFirework());
    }

    private void reg(Material material, ItemType type) {
        if (material.isBlock() != type instanceof BlockType) {
            throw new IllegalArgumentException("Cannot mismatch item and block: " + material + ", " + type);
        }

        if (idToType.containsKey(material.getId())) {
            throw new IllegalArgumentException("Cannot use " + type + " for " + material + ", is already " + idToType.get(material.getId()));
        }

        idToType.put(material.getId(), type);
        type.setId(material.getId());


        if (material.isBlock()) {
            nextBlockId = Math.max(nextBlockId, material.getId() + 1);
            if (type.getClass() != BlockType.class) {
                ((BlockType) type).setPlaceSound(Sound.BLOCK_STONE_BREAK);
            }
        } else {
            nextItemId = Math.max(nextItemId, material.getId() + 1);
        }
    }

    private void reg(Material material, ItemType type, Sound sound) {
        if (material.isBlock() != type instanceof BlockType) {
            throw new IllegalArgumentException("Cannot mismatch item and block: " + material + ", " + type);
        }

        if (idToType.containsKey(material.getId())) {
            throw new IllegalArgumentException("Cannot use " + type + " for " + material + ", is already " + idToType.get(material.getId()));
        }

        idToType.put(material.getId(), type);
        type.setId(material.getId());


        if (material.isBlock()) {
            nextBlockId = Math.max(nextBlockId, material.getId() + 1);
            ((BlockType) type).setPlaceSound(sound);
        } else {
            nextItemId = Math.max(nextItemId, material.getId() + 1);
        }
    }

    /**
     * Register a new, non-Vanilla ItemType. It will be assigned an ID automatically.
     *
     * @param type the ItemType to register.
     */
    public void register(ItemType type) {
        int id;
        if (type instanceof BlockType) {
            id = nextBlockId;
        } else {
            id = nextItemId;
        }

        while (idToType.containsKey(id)) {
            ++id;
        }

        idToType.put(id, type);
        type.setId(id);

        if (type instanceof BlockType) {
            nextBlockId = id + 1;
        } else {
            nextItemId = id + 1;
        }
    }

    private ItemType createDefault(int id) {
        Material material = Material.getMaterial(id);
        if (material == null || id == 0) {
            return null;
        }

        ItemType result;
        if (material.isBlock()) {
            result = new BlockType();
        } else {
            result = new ItemType();
        }
        reg(material, result);
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Type access

    public ItemType getItem(int id) {
        ItemType type = idToType.get(id);
        if (type == null) {
            type = createDefault(id);
        }
        return type;
    }

    public BlockType getBlock(int id) {
        ItemType itemType = getItem(id);
        if (itemType instanceof BlockType) {
            return (BlockType) itemType;
        }
        return null;
    }

    public ItemType getItem(Material mat) {
        return getItem(mat.getId());
    }

    public BlockType getBlock(Material mat) {
        return getBlock(mat.getId());
    }

}
