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

    public static final GlowBlockType AIR = of("air", 0).register();
    public static final GlowBlockType STONE = of("stone", 1).enumProperty("variant", StoneType.values(), EnumNames.stone()).behavior(new RequiresTool(ToolType.PICKAXE), new StoneDrops()).register();
    public static final GlowBlockType GRASS = of("grass", 2).behavior(new DirectDrops(Material.GRASS)).register();
    public static final GlowBlockType DIRT = of("dirt", 3).enumProperty("variant", DirtType.values(), EnumNames.dirt()).behavior(new DirtDrops()).register();
    public static final GlowBlockType COBBLESTONE = of("cobblestone", 4).register();
    public static final GlowBlockType WOOD = of("planks", 5).enumProperty("variant", TreeSpecies.values(), EnumNames.tree()).register();
    public static final GlowBlockType SAPLING = of("sapling", 6).rangeProperty("stage", 0, 1).enumProperty("type", TreeSpecies.values(), EnumNames.tree()).idResolver(new SaplingIdResolver()).register();
    public static final GlowBlockType BEDROCK = of("bedrock", 7).register();
    // flowing_water, water, flowing_lava, lava
    public static final GlowBlockType SAND = of("sand", 12).enumProperty("variant", SandType.values(), EnumNames.sand()).behavior(new Falling()).register();
    public static final GlowBlockType GRAVEL = of("gravel", 13).behavior(new Falling()).register();
    public static final GlowBlockType GOLD_ORE = of("gold_ore", 14).behavior(new RequiresTool(ToolType.IRON_PICKAXE)).register();
    public static final GlowBlockType IRON_ORE = of("iron_ore", 15).behavior(new RequiresTool(ToolType.STONE_PICKAXE)).register();
    public static final GlowBlockType COAL_ORE = of("coal_ore", 16).behavior(new RequiresTool(ToolType.PICKAXE)).register();
    public static final GlowBlockType LOG = of("log", 17).enumProperty("axis", BlockAxis.values()).enumProperty("variant", TREES_FIRST, EnumNames.tree())/*.behavior(new LogPlacement())*/.register();
    public static final GlowBlockType LEAVES = of("leaves", 18).booleanProperty("check_decay").booleanProperty("decayable").enumProperty("variant", TREES_SECOND, EnumNames.tree()).behavior(new LeavesDrops()).register();
    public static final GlowBlockType SPONGE = of("sponge", 19).booleanProperty("wet").behavior(new SpongePlacement()).register();
    public static final GlowBlockType GLASS = of("glass", 20).behavior(new NoDrops()).register();
    public static final GlowBlockType LAPIS_ORE = of("lapis_ore", 21).behavior(new RequiresTool(ToolType.STONE_PICKAXE), new RandomDrops(Material.INK_SACK, 4, 8, 4)).register();
    public static final GlowBlockType LAPIS_BLOCK = of("lapis_block", 22).behavior(new RequiresTool(ToolType.STONE_PICKAXE)).register();
    public static final GlowBlockType DISPENSER = of("dispenser", 23).booleanProperty("triggered").enumProperty("facing", BlockFacing.values())/*.behavior(new DispenserPlacement())*/.register();
    public static final GlowBlockType SANDSTONE = of("sandstone", 24).enumProperty("type", SandstoneType.values(), EnumNames.sandstone()).register();
    public static final GlowBlockType NOTEBLOCK = of("noteblock", 25).register();
    public static final GlowBlockType BED = of("bed", 26).enumProperty("facing", BlockFacing.CARDINAL).booleanProperty("occupied").enumProperty("part", BedPart.values()).register();
    public static final GlowBlockType GOLDEN_RAIL = of("golden_rail", 27).booleanProperty("powered").enumProperty("shape", RailShape.NO_CORNERS).register();
    public static final GlowBlockType DETECTOR_RAIL = of("detector_rail", 28).booleanProperty("powered").enumProperty("shape", RailShape.NO_CORNERS).register();
    public static final GlowBlockType STICKY_PISTON = of("sticky_piston", 29).booleanProperty("extended").enumProperty("facing", BlockFacing.values()).register();
    public static final GlowBlockType WEB = of("web", 30).register();
    public static final GlowBlockType TALLGRASS = of("tallgrass", 31).enumProperty("type", GrassSpecies.values(), EnumNames.grass()).register();
    public static final GlowBlockType DEADBUSH = of("deadbush", 32).register();
    public static final GlowBlockType PISTON = of("piston", 33).booleanProperty("extended").enumProperty("facing", BlockFacing.values()).register();
    public static final GlowBlockType PISTON_HEAD = of("piston_head", 34).enumProperty("facing", BlockFacing.values()).enumProperty("type", PistonType.values()).register();
    public static final GlowBlockType WOOL = of("wool", 35).enumProperty("color", DyeColor.values(), EnumNames.color()).register();
    // piston_extension (12 states)
    public static final GlowBlockType YELLOW_FLOWER = of("yellow_flower", 37).enumProperty("type", FlowerType.YELLOW_FLOWER).register();
    public static final GlowBlockType RED_FLOWER = of("red_flower", 38).enumProperty("type", FlowerType.RED_FLOWER).register();
    public static final GlowBlockType BROWN_MUSHROOM = of("brown_mushroom", 39).register();
    public static final GlowBlockType RED_MUSHROOM = of("red_mushroom", 40).register();
    public static final GlowBlockType GOLD_BLOCK = of("gold_block", 41).register();
    public static final GlowBlockType IRON_BLOCK = of("iron_block", 42).register();
    // double_stone_slab, stone_slab
    public static final GlowBlockType BRICK_BLOCK = of("red_mushroom", 45).register();
    public static final GlowBlockType TNT = of("tnt", 46).register();
    public static final GlowBlockType BOOKSHELF = of("bookshelf", 47).register();
    public static final GlowBlockType MOSSY_COBBLESTONE = of("mossy_cobblestone", 48).register();
    public static final GlowBlockType OBSIDIAN = of("obsidian", 49).register();
    public static final GlowBlockType TORCH = of("torch", 50).enumProperty("facing", BlockFacing.NOT_DOWN).register();
    public static final GlowBlockType FIRE = of("fire", 51).rangeProperty(/*???*/"lifetime", 0, 15).register();
    public static final GlowBlockType MOB_SPAWNER = of("mob_spawner", 52).register();
    public static final GlowBlockType OAK_STAIRS = of("oak_stairs", 53).stairs().register();
    public static final GlowBlockType CHEST = of("chest", 54).enumProperty("facing", BlockFacing.CARDINAL).register();
    public static final GlowBlockType REDSTONE_WIRE = of("redstone_wire", 55).rangeProperty("power", 0, 15).register();
    public static final GlowBlockType DIAMOND_ORE = of("diamond_ore", 56).register();
    public static final GlowBlockType DIAMOND_BLOCK = of("diamond_block", 57).register();
    public static final GlowBlockType CRAFTING_TABLE = of("crafting_table", 58).register();
    public static final GlowBlockType WHEAT = of("wheat", 59).rangeProperty("age", 0, 7).register();
    public static final GlowBlockType FARMLAND = of("farmland", 60).rangeProperty("moisture", 0, 7).register();
    public static final GlowBlockType FURNACE = of("furnace", 61).enumProperty("facing", BlockFacing.CARDINAL).register();
    public static final GlowBlockType LIT_FURNACE = of("lit_furnace", 62).enumProperty("facing", BlockFacing.CARDINAL).register();
    public static final GlowBlockType STANDING_SIGN = of("standing_sign", 63).rangeProperty("rotation", 0, 15).register();
    public static final GlowBlockType WOODEN_DOOR = of("wooden_door", 64).door().register();
    public static final GlowBlockType LADDER = of("ladder", 65).enumProperty("facing", BlockFacing.CARDINAL).register();
    public static final GlowBlockType RAIL = of("rail", 66).enumProperty("shape", RailShape.values()).register();
    public static final GlowBlockType STONE_STAIRS = of("stone_stairs", 67).stairs().register();
    public static final GlowBlockType WALL_SIGN = of("wall_sign", 68).enumProperty("facing", BlockFacing.CARDINAL).register();
    public static final GlowBlockType LEVER = of("lever", 69).enumProperty("facing", LeverFacing.values()).booleanProperty("powered").register();
    public static final GlowBlockType STONE_PRESSURE_PLATE = of("stone_pressure_plate", 70).booleanProperty("powered").register();
    public static final GlowBlockType IRON_DOOR = of("iron_door", 71).door().register();
    public static final GlowBlockType WOODEN_PRESSURE_PLATE = of("wooden_pressure_plate", 72).booleanProperty("powered").register();
    public static final GlowBlockType REDSTONE_ORE = of("redstone_ore", 73).register();
    public static final GlowBlockType LIT_REDSTONE_OR = of("lit_redstone_ore", 74).register();
    public static final GlowBlockType UNLIT_REDSTONE_TORCH = of("unlit_redstone_torch", 75).enumProperty("facing", BlockFacing.NOT_DOWN).register();
    public static final GlowBlockType REDSTONE_TORCH = of("redstone_torch", 76).enumProperty("facing", BlockFacing.NOT_DOWN).register();
    public static final GlowBlockType STONE_BUTTON = of("stone_button", 77).enumProperty("facing", BlockFacing.values()).booleanProperty("powered").register();
    public static final GlowBlockType SNOW_LAYER = of("snow_layer", 78).rangeProperty("layers", 1, 8).register();
    public static final GlowBlockType ICE = of("ice", 79).register();
    public static final GlowBlockType SNOW = of("snow", 80).register();
    public static final GlowBlockType CACTUS = of("cactus", 81).rangeProperty("age", 0, 15).register();
    public static final GlowBlockType CLAY = of("clay", 82).register();
    public static final GlowBlockType REEDS = of("reeds", 83).rangeProperty("age", 0, 15).register();
    public static final GlowBlockType JUKEBOX = of("jukebox", 84).booleanProperty("has_record").register();
    public static final GlowBlockType FENCE = of("fence", 85).register();
    public static final GlowBlockType PUMPKIN = of("pumpkin", 86).enumProperty("facing", BlockFacing.CARDINAL).register();
    public static final GlowBlockType NETHERRACK = of("netherrack", 87).register();
    public static final GlowBlockType SOUL_SAND = of("soul_sand", 88).register();
    public static final GlowBlockType GLOWSTONE = of("glowstone", 89).register();

    private Blocks() {
    }

    static void init() {
        // nothing, just used to make sure blocks above are registered
    }

    private static BlockTypeBuilder of(String id, int oldId) {
        return new BlockTypeBuilder("minecraft:" + id).oldId(oldId);
    }

}
