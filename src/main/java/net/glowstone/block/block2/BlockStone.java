package net.glowstone.block.block2;

/**
 * Todo: Javadoc for BlockStone.
 */
public class BlockStone extends GlowBlockType {

    public static final String ID = "minecraft:stone";
    public static final BlockProperty VARIANT = GlowBlockProperty.ofEnum("variant", Variant.class);

    public BlockStone() {
        super(ID, VARIANT);
    }

    public enum Variant {
        STONE,
        GRANITE,
        SMOOTH_GRANITE,
        DIORITE,
        SMOOTH_DIORITE,
        ANDESITE,
        SMOOTH_ANDESITE
    }

}
