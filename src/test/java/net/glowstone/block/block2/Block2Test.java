package net.glowstone.block.block2;

import net.glowstone.block.block2.sponge.BlockState;
import net.glowstone.block.block2.sponge.BlockType;
import net.glowstone.block.block2.types.StoneVariant;
import org.junit.Test;

/**
 * Simple tests for the new block type system.
 */
public class Block2Test {

    static {
        BlockType t;
        //t = BlockRegistry.instance.getBlock("minecraft:stone");
        //t = Blocks.STONE;
    }

    @Test
    public void stuff() {
        BlockRegistry reg = BlockRegistry.instance;
        System.out.println(reg);

        BlockType t = reg.getBlock("minecraft:stone");
        System.out.println(t);

        BlockState s = t.getDefaultState();
        System.out.println(s);

        BlockState s2 = s.cycleProperty(s.getPropertyByName("variant").get());
        System.out.println(s2);

        BlockState s3 = s.withProperty(s.getPropertyByName("variant").get(), StoneVariant.ANDESITE);
        System.out.println(s3);
        System.out.println(s3.getType());
    }

}
