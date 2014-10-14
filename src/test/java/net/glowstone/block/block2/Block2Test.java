package net.glowstone.block.block2;

import org.junit.Test;

/**
 * Simple tests for the new block type system.
 */
public class Block2Test {

    @Test
    public void stuff() {
        BlockRegistry r = BlockRegistry.instance;
        BlockType t = r.getBlock("minecraft:stone");
        System.out.println(r);
        System.out.println(t);
        BlockType t2 = t.withProperty(BlockStone.VARIANT, BlockStone.Variant.ANDESITE);
        System.out.println(t2);
        BlockType t3 = t.withProperty(BlockStone.VARIANT, BlockStone.Variant.ANDESITE);
        System.out.println(t.isBaseType() + " " + (t2 == t3));
        System.out.println(t2.getBaseType() + " " + (t2.getBaseType() == t));
        t.withProperty(BlockStone.VARIANT, 5);
    }

}
