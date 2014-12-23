package net.glowstone.block.block2;

import net.glowstone.block.block2.types.*;
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
    public static final GlowBlockType SAPLING = of("sapling", 6).rangeProperty("stage", 0, 1).enumProperty("type", TreeVariant.class).register();

    private Blocks() {
    }

    static void init() {
        // nothing, just used to make sure <clinit> is called
    }

    private static BlockTypeBuilder of(String id, int oldId) {
        return new BlockTypeBuilder("minecraft:" + id).oldId(oldId);
    }

}
