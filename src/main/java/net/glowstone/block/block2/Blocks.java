package net.glowstone.block.block2;

import net.glowstone.block.block2.details.*;
import net.glowstone.inventory.ToolType;
import org.bukkit.*;

/**
 * Vanilla Minecraft block types.
 */
public final class Blocks {

    private static final TreeSpecies[] TREES_FIRST = {TreeSpecies.GENERIC, TreeSpecies.REDWOOD, TreeSpecies.BIRCH, TreeSpecies.JUNGLE};
    private static final TreeSpecies[] TREES_SECOND = {TreeSpecies.ACACIA, TreeSpecies.DARK_OAK};

    public static final GlowBlockType AIR = of("air").register();
    public static final GlowBlockType STONE = of("stone").enumProperty("variant", StoneType.values(), EnumNames.stone()).behavior(new RequiresTool(ToolType.PICKAXE), new StoneDrops()).register();
    public static final GlowBlockType GRASS = of("grass").behavior(new DirectDrops(Material.GRASS)).register();
    public static final GlowBlockType DIRT = of("dirt").enumProperty("variant", DirtType.values(), EnumNames.dirt()).behavior(new DirtDrops()).register();
    public static final GlowBlockType COBBLESTONE = of("cobblestone").register();
    public static final GlowBlockType WOOD = of("planks").enumProperty("variant", TreeSpecies.values(), EnumNames.tree()).register();
    public static final GlowBlockType SAPLING = of("sapling").rangeProperty("stage", 0, 1).enumProperty("type", TreeSpecies.values(), EnumNames.tree()).register();
    public static final GlowBlockType BEDROCK = of("bedrock").register();
    // flowing_water, water, flowing_lava, lava
    public static final GlowBlockType SAND = of("sand").enumProperty("variant", SandType.values(), EnumNames.sand()).behavior(new Falling()).register();
    public static final GlowBlockType GRAVEL = of("gravel").behavior(new Falling()).register();
    public static final GlowBlockType GOLD_ORE = of("gold_ore").behavior(new RequiresTool(ToolType.IRON_PICKAXE)).register();
    public static final GlowBlockType IRON_ORE = of("iron_ore").behavior(new RequiresTool(ToolType.STONE_PICKAXE)).register();
    public static final GlowBlockType COAL_ORE = of("coal_ore").behavior(new RequiresTool(ToolType.PICKAXE)).register();
    public static final GlowBlockType LOG = of("log").enumProperty("axis", BlockAxis.values()).enumProperty("variant", TREES_FIRST, EnumNames.tree())/*.behavior(new LogPlacement())*/.register();
    public static final GlowBlockType LEAVES = of("leaves").booleanProperty("check_decay").booleanProperty("decayable").enumProperty("variant", TREES_SECOND, EnumNames.tree()).behavior(new LeavesDrops()).register();
    public static final GlowBlockType SPONGE = of("sponge").booleanProperty("wet").behavior(new SpongePlacement()).register();
    public static final GlowBlockType GLASS = of("glass").behavior(new NoDrops()).register();
    public static final GlowBlockType LAPIS_ORE = of("lapis_ore").behavior(new RequiresTool(ToolType.STONE_PICKAXE), new RandomDrops(Material.INK_SACK, 4, 8, 4)).register();
    public static final GlowBlockType LAPIS_BLOCK = of("lapis_block").behavior(new RequiresTool(ToolType.STONE_PICKAXE)).register();
    public static final GlowBlockType DISPENSER = of("dispenser").booleanProperty("triggered").enumProperty("facing", BlockFacing.values())/*.behavior(new DispenserPlacement())*/.register();
    public static final GlowBlockType SANDSTONE = of("sandstone").enumProperty("type", SandstoneType.values(), EnumNames.sandstone()).register();
    public static final GlowBlockType NOTEBLOCK = of("noteblock").register();
    public static final GlowBlockType BED = of("bed").enumProperty("facing", BlockFacing.CARDINAL).booleanProperty("occupied").enumProperty("part", BedPart.values()).register();
    public static final GlowBlockType GOLDEN_RAIL = of("golden_rail").booleanProperty("powered").enumProperty("shape", RailShape.NO_CORNERS).register();
    public static final GlowBlockType DETECTOR_RAIL = of("detector_rail").booleanProperty("powered").enumProperty("shape", RailShape.NO_CORNERS).register();
    public static final GlowBlockType STICKY_PISTON = of("sticky_piston").booleanProperty("extended").enumProperty("facing", BlockFacing.values()).register();
    public static final GlowBlockType WEB = of("web").register();
    public static final GlowBlockType TALLGRASS = of("tallgrass").enumProperty("type", GrassSpecies.values(), EnumNames.grass()).register();
    public static final GlowBlockType DEADBUSH = of("deadbush").register();
    public static final GlowBlockType PISTON = of("piston").booleanProperty("extended").enumProperty("facing", BlockFacing.values()).register();
    public static final GlowBlockType PISTON_HEAD = of("piston_head").enumProperty("facing", BlockFacing.values()).enumProperty("type", PistonType.values()).register();
    public static final GlowBlockType WOOL = of("wool").enumProperty("color", DyeColor.values(), EnumNames.color()).register();
    // piston_extension (12 states)
    public static final GlowBlockType YELLOW_FLOWER = of("yellow_flower").enumProperty("type", FlowerType.YELLOW_FLOWER).register();
    public static final GlowBlockType RED_FLOWER = of("red_flower").enumProperty("type", FlowerType.RED_FLOWER).register();
    public static final GlowBlockType BROWN_MUSHROOM = of("brown_mushroom").register();
    public static final GlowBlockType RED_MUSHROOM = of("red_mushroom").register();
    public static final GlowBlockType GOLD_BLOCK = of("gold_block").register();
    public static final GlowBlockType IRON_BLOCK = of("iron_block").register();
    // double_stone_slab, stone_slab
    public static final GlowBlockType BRICK_BLOCK = of("red_mushroom").register();
    public static final GlowBlockType TNT = of("tnt").register();
    public static final GlowBlockType BOOKSHELF = of("bookshelf").register();
    public static final GlowBlockType MOSSY_COBBLESTONE = of("mossy_cobblestone").register();
    public static final GlowBlockType OBSIDIAN = of("obsidian").register();
    public static final GlowBlockType TORCH = of("torch").enumProperty("facing", BlockFacing.NOT_DOWN).register();
    public static final GlowBlockType FIRE = of("fire").rangeProperty(/*???*/"lifetime", 0, 15).register();
    public static final GlowBlockType MOB_SPAWNER = of("mob_spawner").register();
    public static final GlowBlockType OAK_STAIRS = of("oak_stairs").stairs().register();
    public static final GlowBlockType CHEST = of("chest").enumProperty("facing", BlockFacing.CARDINAL).register();
    public static final GlowBlockType REDSTONE_WIRE = of("redstone_wire").rangeProperty("power", 0, 15).register();
    public static final GlowBlockType DIAMOND_ORE = of("diamond_ore").register();
    public static final GlowBlockType DIAMOND_BLOCK = of("diamond_block").register();
    public static final GlowBlockType CRAFTING_TABLE = of("crafting_table").register();
    public static final GlowBlockType WHEAT = of("wheat").rangeProperty("age", 0, 7).register();
    public static final GlowBlockType FARMLAND = of("farmland").rangeProperty("moisture", 0, 7).register();
    public static final GlowBlockType FURNACE = of("furnace").enumProperty("facing", BlockFacing.CARDINAL).register();
    public static final GlowBlockType LIT_FURNACE = of("lit_furnace").enumProperty("facing", BlockFacing.CARDINAL).register();
    public static final GlowBlockType STANDING_SIGN = of("standing_sign").rangeProperty("rotation", 0, 15).register();
    public static final GlowBlockType WOODEN_DOOR = of("wooden_door").door().register();
    public static final GlowBlockType LADDER = of("ladder").enumProperty("facing", BlockFacing.CARDINAL).register();
    public static final GlowBlockType RAIL = of("rail").enumProperty("shape", RailShape.values()).register();
    public static final GlowBlockType STONE_STAIRS = of("stone_stairs").stairs().register();
    public static final GlowBlockType WALL_SIGN = of("wall_sign").enumProperty("facing", BlockFacing.CARDINAL).register();
    public static final GlowBlockType LEVER = of("lever").enumProperty("facing", LeverFacing.values()).booleanProperty("powered").register();
    public static final GlowBlockType STONE_PRESSURE_PLATE = of("stone_pressure_plate").booleanProperty("powered").register();
    public static final GlowBlockType IRON_DOOR = of("iron_door").door().register();
    public static final GlowBlockType WOODEN_PRESSURE_PLATE = of("wooden_pressure_plate").booleanProperty("powered").register();
    public static final GlowBlockType REDSTONE_ORE = of("redstone_ore").register();
    public static final GlowBlockType LIT_REDSTONE_ORE = of("lit_redstone_ore").register();
    public static final GlowBlockType UNLIT_REDSTONE_TORCH = of("unlit_redstone_torch").enumProperty("facing", BlockFacing.NOT_DOWN).register();
    public static final GlowBlockType REDSTONE_TORCH = of("redstone_torch").enumProperty("facing", BlockFacing.NOT_DOWN).register();
    public static final GlowBlockType STONE_BUTTON = of("stone_button").enumProperty("facing", BlockFacing.values()).booleanProperty("powered").register();
    public static final GlowBlockType SNOW_LAYER = of("snow_layer").rangeProperty("layers", 1, 8).register();
    public static final GlowBlockType ICE = of("ice").register();
    public static final GlowBlockType SNOW = of("snow").register();
    public static final GlowBlockType CACTUS = of("cactus").rangeProperty("age", 0, 15).register();
    public static final GlowBlockType CLAY = of("clay").register();
    public static final GlowBlockType REEDS = of("reeds").rangeProperty("age", 0, 15).register();
    public static final GlowBlockType JUKEBOX = of("jukebox").booleanProperty("has_record").register();
    public static final GlowBlockType FENCE = of("fence").register();
    public static final GlowBlockType PUMPKIN = of("pumpkin").enumProperty("facing", BlockFacing.CARDINAL).register();
    public static final GlowBlockType NETHERRACK = of("netherrack").register();
    public static final GlowBlockType SOUL_SAND = of("soul_sand").register();
    public static final GlowBlockType GLOWSTONE = of("glowstone").register();

    private Blocks() {
    }

    static void init() {
        // nothing, just used to make sure blocks above are registered
    }

    private static BlockTypeBuilder of(String id) {
        return new BlockTypeBuilder("minecraft:" + id);
    }

}
