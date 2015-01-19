package net.glowstone.block.block2;

import net.glowstone.block.block2.details.*;
import net.glowstone.inventory.ToolType;
import org.bukkit.Material;

/**
 * Vanilla Minecraft block types.
 */
public final class Blocks {

    public static final GlowBlockType AIR = of("air", 0).register();
    public static final GlowBlockType STONE = of("stone", 1).enumProperty("variant", StoneVariant.class).behavior(new RequiresTool(ToolType.PICKAXE), new StoneDrops()).register();
    public static final GlowBlockType GRASS = of("grass", 2).behavior(new DirectDrops(Material.GRASS)).register();
    public static final GlowBlockType DIRT = of("dirt", 3).enumProperty("variant", DirtVariant.class).behavior(new DirtDrops()).register();
    public static final GlowBlockType COBBLESTONE = of("cobblestone", 4).register();
    public static final GlowBlockType WOOD = of("planks", 5).enumProperty("variant", TreeVariant.class).register();
    public static final GlowBlockType SAPLING = of("sapling", 6).rangeProperty("stage", 0, 1).enumProperty("type", TreeVariant.class).idResolver(new SaplingIdResolver()).register();
    public static final GlowBlockType BEDROCK = of("bedrock", 7).register();
    // <- liquids
    public static final GlowBlockType SAND = of("sand", 12).enumProperty("variant", SandVariant.class).behavior(new Falling()).register();
    public static final GlowBlockType GRAVEL = of("gravel", 13).behavior(new Falling()).register();
    public static final GlowBlockType GOLD_ORE = of("gold_ore", 14).behavior(new RequiresTool(ToolType.IRON_PICKAXE)).register();
    public static final GlowBlockType IRON_ORE = of("iron_ore", 15).behavior(new RequiresTool(ToolType.STONE_PICKAXE)).register();
    public static final GlowBlockType COAL_ORE = of("coal_ore", 16).behavior(new RequiresTool(ToolType.PICKAXE)).register();
    public static final GlowBlockType LOG = of("log", 17).enumProperty("axis", BlockAxis.class).partialProperty("variant", TreeVariant.class, TreeVariant.FIRST_HALF)/*.behavior(new LogPlacement())*/.register();
    public static final GlowBlockType LEAVES = of("leaves", 18).booleanProperty("check_decay").booleanProperty("decayable").partialProperty("variant", TreeVariant.class, TreeVariant.FIRST_HALF).behavior(new LeavesDrops()).register();
    public static final GlowBlockType SPONGE = of("sponge", 19).booleanProperty("wet").behavior(new SpongePlacement()).register();
    public static final GlowBlockType GLASS = of("glass", 20).behavior(new NoDrops()).register();
    public static final GlowBlockType LAPIS_ORE = of("lapis_ore", 21).behavior(new RequiresTool(ToolType.STONE_PICKAXE), new RandomDrops(Material.INK_SACK, 4, 8, 4)).register();
    public static final GlowBlockType LAPIS_BLOCK = of("lapis_block", 22).behavior(new RequiresTool(ToolType.STONE_PICKAXE)).register();
    public static final GlowBlockType DISPENSER = of("dispenser", 23).booleanProperty("triggered").enumProperty("facing", BlockFacing.class)/*.behavior(new DispenserPlacement())*/.register();

    private Blocks() {
    }

    static void init() {
        // nothing, just used to make sure blocks above are registered
    }

    private static BlockTypeBuilder of(String id, int oldId) {
        return new BlockTypeBuilder("minecraft:" + id).oldId(oldId);
    }

}
