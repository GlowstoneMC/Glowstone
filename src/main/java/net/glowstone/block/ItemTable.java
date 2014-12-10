package net.glowstone.block;

import net.glowstone.block.blocktype.*;
import net.glowstone.block.itemtype.*;
import net.glowstone.inventory.ToolType;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

/**
 * The lookup table for block and item types.
 */
public final class ItemTable {

    private static final ItemTable INSTANCE = new ItemTable();

    static {
        INSTANCE.registerBuiltins();
    }

    public static ItemTable instance() {
        return INSTANCE;
    }

    private ItemTable() {
    }

    ////////////////////////////////////////////////////////////////////////////
    // Data

    private final Map<Integer, ItemType> idToType = new HashMap<>(512);

    private int nextBlockId, nextItemId;

    ////////////////////////////////////////////////////////////////////////////
    // Registration

    private void registerBuiltins() {
        reg(Material.NOTE_BLOCK, new BlockNote());
        reg(Material.MOB_SPAWNER, new BlockMobSpawner());
        reg(Material.SIGN_POST, new BlockSign());
        reg(Material.WALL_SIGN, new BlockSign());
        reg(Material.WORKBENCH, new BlockWorkbench());
        reg(Material.ENDER_CHEST, new BlockEnderchest());
        reg(Material.CHEST, new BlockChest());
        reg(Material.DISPENSER, new BlockDispenser());
        reg(Material.DROPPER, new BlockDropper());
        reg(Material.BOOKSHELF, new BlockDirectDrops(Material.BOOK, 3));
        reg(Material.CLAY, new BlockDirectDrops(Material.CLAY_BALL, 4));
        reg(Material.HARD_CLAY, new BlockDirectDrops(ToolType.PICKAXE));
        reg(Material.STAINED_CLAY, new BlockDirectDrops(ToolType.PICKAXE));
        reg(Material.DOUBLE_STEP, new BlockDoubleSlab());
        reg(Material.DOUBLE_STEP_2, new BlockDoubleSlab());
        reg(Material.WOOD_DOUBLE_STEP, new BlockDoubleSlab());
        reg(Material.SOIL, new BlockDirectDrops(Material.DIRT));
        reg(Material.GLASS, new BlockDropless());
        reg(Material.THIN_GLASS, new BlockDropless());
        reg(Material.GLOWSTONE, new BlockRandomDrops(Material.GLOWSTONE_DUST, 2, 4));
        reg(Material.MYCEL, new BlockDirectDrops(Material.DIRT));
        reg(Material.GRASS, new BlockDirectDrops(Material.DIRT));
        reg(Material.DIRT, new BlockDirectDrops(Material.DIRT));
        reg(Material.GRAVEL, new BlockGravel());
        reg(Material.ICE, new BlockDropless());
        reg(Material.PACKED_ICE, new BlockDropless());
        reg(Material.SNOW, new BlockSnow());
        reg(Material.SNOW_BLOCK, new BlockDropless());
        reg(Material.PRISMARINE, new BlockDirectDrops(ToolType.PICKAXE));
        reg(Material.RED_SANDSTONE, new BlockDirectDrops(ToolType.PICKAXE));
        reg(Material.SANDSTONE, new BlockDirectDrops(ToolType.PICKAXE));
        reg(Material.NETHER_BRICK, new BlockDirectDrops(ToolType.PICKAXE));
        reg(Material.NETHER_FENCE, new BlockDirectDrops(Material.NETHER_FENCE, ToolType.PICKAXE));
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
        reg(Material.COAL_ORE, new BlockDirectDrops(Material.COAL, ToolType.PICKAXE));
        reg(Material.COAL_BLOCK, new BlockDirectDrops(ToolType.PICKAXE));
        reg(Material.IRON_ORE, new BlockDirectDrops(ToolType.STONE_PICKAXE));
        reg(Material.IRON_BLOCK, new BlockDirectDrops(ToolType.STONE_PICKAXE));
        reg(Material.GOLD_ORE, new BlockDirectDrops(ToolType.IRON_PICKAXE));
        reg(Material.GOLD_BLOCK, new BlockDirectDrops(ToolType.IRON_PICKAXE));
        reg(Material.DIAMOND_ORE, new BlockDirectDrops(Material.DIAMOND, ToolType.IRON_PICKAXE));
        reg(Material.DIAMOND_BLOCK, new BlockDirectDrops(ToolType.IRON_PICKAXE));
        reg(Material.EMERALD_ORE, new BlockDirectDrops(Material.EMERALD, ToolType.IRON_PICKAXE));
        reg(Material.EMERALD_BLOCK, new BlockDirectDrops(ToolType.PICKAXE));
        reg(Material.LAPIS_ORE, new BlockRandomDrops(Material.INK_SACK, 4, 4, 8, ToolType.STONE_PICKAXE));
        reg(Material.LAPIS_BLOCK, new BlockDirectDrops(ToolType.STONE_PICKAXE));
        reg(Material.QUARTZ_ORE, new BlockDirectDrops(Material.QUARTZ, ToolType.PICKAXE));
        reg(Material.REDSTONE_ORE, new BlockRandomDrops(Material.REDSTONE, 0, 3, 4, ToolType.IRON_PICKAXE));
        reg(Material.REDSTONE_BLOCK, new BlockDirectDrops(ToolType.PICKAXE));
        reg(Material.CARROT, new BlockDirectDrops(Material.CARROT_ITEM));
        reg(Material.COCOA, new BlockDirectDrops(Material.INK_SACK, 3, 1));
        reg(Material.DEAD_BUSH, new BlockDropless());
        reg(Material.LONG_GRASS, new BlockTallGrass());
        reg(Material.HUGE_MUSHROOM_1, new BlockHugeMushroom(true));
        reg(Material.HUGE_MUSHROOM_2, new BlockHugeMushroom(false));
        reg(Material.LEAVES, new BlockLeaves());
        reg(Material.LEAVES_2, new BlockLeaves());
        reg(Material.MELON_BLOCK, new BlockMelon());
        reg(Material.MELON_STEM, new BlockMelonStem());
        reg(Material.NETHER_WARTS, new BlockDirectDrops(Material.NETHER_STALK));
        reg(Material.POTATO, new BlockDirectDrops(Material.POTATO_ITEM));
        reg(Material.PUMPKIN_STEM, new BlockPumpkinStem());
        reg(Material.CROPS, new BlockDirectDrops(Material.SEEDS));
        reg(Material.CAKE_BLOCK, new BlockDropless());
        reg(Material.WEB, new BlockWeb());
        reg(Material.FIRE, new BlockFire());
        reg(Material.MONSTER_EGGS, new BlockDropless());
        reg(Material.ENDER_PORTAL_FRAME, new BlockEnderPortalFrame());
        reg(Material.FENCE_GATE, new BlockFenceGate());
        reg(Material.ACACIA_FENCE_GATE, new BlockFenceGate());
        reg(Material.BIRCH_FENCE_GATE, new BlockFenceGate());
        reg(Material.DARK_OAK_FENCE_GATE, new BlockFenceGate());
        reg(Material.JUNGLE_FENCE_GATE, new BlockFenceGate());
        reg(Material.SPRUCE_FENCE_GATE, new BlockFenceGate());
        reg(Material.TRAP_DOOR, new BlockWoodenTrapDoor());
        reg(Material.IRON_TRAP_DOOR, new BlockIronTrapDoor());
        reg(Material.FURNACE, new BlockFurnace());
        reg(Material.LEVER, new BlockLever());
        reg(Material.HOPPER, new BlockHopper());
        reg(Material.ACACIA_STAIRS, new BlockStairs());
        reg(Material.BIRCH_WOOD_STAIRS, new BlockStairs());
        reg(Material.BRICK_STAIRS, new BlockStairs());
        reg(Material.COBBLESTONE_STAIRS, new BlockStairs());
        reg(Material.DARK_OAK_STAIRS, new BlockStairs());
        reg(Material.JUNGLE_WOOD_STAIRS, new BlockStairs());
        reg(Material.NETHER_BRICK_STAIRS, new BlockStairs());
        reg(Material.QUARTZ_STAIRS, new BlockStairs());
        reg(Material.SANDSTONE_STAIRS, new BlockStairs());
        reg(Material.RED_SANDSTONE_STAIRS, new BlockStairs());
        reg(Material.SPRUCE_WOOD_STAIRS, new BlockStairs());
        reg(Material.SMOOTH_STAIRS, new BlockStairs());
        reg(Material.WOOD_STAIRS, new BlockStairs());
        reg(Material.STEP, new BlockSlab());
        reg(Material.WOOD_STEP, new BlockSlab());
        reg(Material.STEP_2, new BlockSlab());
        reg(Material.HAY_BLOCK, new BlockHay());
        reg(Material.QUARTZ_BLOCK, new BlockQuartz());
        reg(Material.LOG, new BlockLog());
        reg(Material.LOG_2, new BlockLog2());
        reg(Material.LADDER, new BlockLadder());
        reg(Material.VINE, new BlockVine());
        reg(Material.STONE_BUTTON, new BlockButton(Material.STONE_BUTTON));
        reg(Material.WOOD_BUTTON, new BlockButton(Material.WOOD_BUTTON));
        reg(Material.BED_BLOCK, new BlockBed());
        reg(Material.SKULL, new BlockSkull());
        reg(Material.TORCH, new BlockTorch());
        reg(Material.GOLD_PLATE, new BlockDirectDrops(Material.GOLD_PLATE, ToolType.PICKAXE));
        reg(Material.IRON_PLATE, new BlockDirectDrops(Material.IRON_PLATE, ToolType.PICKAXE));
        reg(Material.STONE_PLATE, new BlockDirectDrops(Material.STONE_PLATE, ToolType.PICKAXE));
        reg(Material.DAYLIGHT_DETECTOR, new BlockDaylightDetector());
        reg(Material.DAYLIGHT_DETECTOR_INVERTED, new BlockDaylightDetector());
        reg(Material.ENCHANTMENT_TABLE, new BlockEnchantmentTable());
        reg(Material.ANVIL, new BlockAnvil());
        reg(Material.BREWING_STAND, new BlockBrewingStand());
        reg(Material.WATER, new BlockWater());
        reg(Material.STATIONARY_WATER, new BlockWater());
        reg(Material.LAVA, new BlockLava());
        reg(Material.STATIONARY_LAVA, new BlockLava());
        reg(Material.CAULDRON, new BlockDirectDrops(Material.CAULDRON_ITEM, ToolType.PICKAXE));
        reg(Material.STANDING_BANNER, new BlockBanner());
        reg(Material.WALL_BANNER, new BlockBanner());

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
        reg(Material.SEEDS, new ItemSeeds(Material.CROPS, Material.SOIL));
        reg(Material.MELON_SEEDS, new ItemSeeds(Material.MELON_STEM, Material.SOIL));
        reg(Material.PUMPKIN_SEEDS, new ItemSeeds(Material.PUMPKIN_STEM, Material.SOIL));
        reg(Material.NETHER_STALK, new ItemSeeds(Material.NETHER_WARTS, Material.SOUL_SAND));
        reg(Material.CARROT_ITEM, new ItemFoodSeeds(Material.CARROT, Material.SOIL));
        reg(Material.POTATO_ITEM, new ItemFoodSeeds(Material.POTATO, Material.SOIL));
        reg(Material.INK_SACK, new ItemDye());
        reg(Material.BANNER, new ItemBanner());
    }

    private void reg(Material material, ItemType type) {
        if (material.isBlock() != (type instanceof BlockType)) {
            throw new IllegalArgumentException("Cannot mismatch item and block: " + material + ", " + type);
        }

        if (idToType.containsKey(material.getId())) {
            throw new IllegalArgumentException("Cannot use " + type + " for " + material + ", is already " + idToType.get(material.getId()));
        }

        idToType.put(material.getId(), type);
        type.setId(material.getId());

        if (material.isBlock()) {
            nextBlockId = Math.max(nextBlockId, material.getId() + 1);
        } else {
            nextItemId = Math.max(nextItemId, material.getId() + 1);
        }
    }

    /**
     * Register a new, non-Vanilla ItemType. It will be assigned an ID automatically.
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
