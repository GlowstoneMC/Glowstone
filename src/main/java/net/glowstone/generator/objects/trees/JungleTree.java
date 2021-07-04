package net.glowstone.generator.objects.trees;

import net.glowstone.util.BlockStateDelegate;

import java.util.Random;

public class JungleTree extends GenericTree {

    /**
     * Initializes this tree with a random height, preparing it to attempt to generate.
     *  @param random the PRNG
     * @param delegate the BlockStateDelegate used to check for space and to fill wood and leaf
     */
    public JungleTree(Random random, BlockStateDelegate delegate) {
        super(random, delegate);
        setHeight(random.nextInt(7) + 4);
        setTypes(3, 3);
    }
}
