package net.glowstone.generator.objects.trees;

import net.glowstone.util.BlockStateDelegate;

import java.util.Random;

public class BirchTree extends GenericTree {

    /**
     * Initializes this tree with a random height, preparing it to attempt to generate.
     *
     * @param random the PRNG
     * @param delegate the BlockStateDelegate used to check for space and to fill wood and leaf
     */
    public BirchTree(Random random, BlockStateDelegate delegate) {
        super(random, delegate);
        setHeight(random.nextInt(3) + 5);
        setTypes(2, 2);
    }
}
