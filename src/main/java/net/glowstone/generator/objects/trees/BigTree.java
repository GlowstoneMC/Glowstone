package net.glowstone.generator.objects.trees;

import net.glowstone.util.BlockStateDelegate;

import java.util.Random;

public class BigTree extends BigOakTree {

    /**
     * Initializes this tree with a random height, preparing it to attempt to generate.
     *
     * @param random the PRNG
     * @param delegate the BlockStateDelegate used to check for space and to fill wood and leaf
     *     blocks
     */
    public BigTree(Random random, BlockStateDelegate delegate) {
        super(random, delegate);
        setMaxLeafDistance(4);
    }
}
