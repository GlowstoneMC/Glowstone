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
    public static final GlowBlockType GOLD_BLOCK = of("brown_mushroom", 41).register();
    public static final GlowBlockType IRON_BLOCK = of("red_mushroom", 42).register();

    private Blocks() {
    }

    static void init() {
        // nothing, just used to make sure blocks above are registered
    }

    private static BlockTypeBuilder of(String id, int oldId) {
        return new BlockTypeBuilder("minecraft:" + id).oldId(oldId);
    }

}
