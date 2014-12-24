package net.glowstone.block.block2;

import net.glowstone.block.block2.sponge.BlockState;
import net.glowstone.block.block2.sponge.BlockType;
import net.glowstone.block.block2.details.StoneVariant;
import org.junit.Test;

/**
 * Simple tests for the new block type system.
 */
public class Block2Test {

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

        int i = 0;
        while (true) {
            BlockType type = reg.getByTypeId(i);
            if (type == null) break;
            System.out.println(type);
            System.out.println("  0> " + type.getDefaultState());

            for (byte j = 1; j < 16; ++j) {
                BlockState state = type.getStateFromDataValue(j);
                if (state != null) {
                    System.out.println("  " + j + ": " + state);
                }
            }
            ++i;
        }
    }

}
