package net.glowstone.generator.objects.trees;

import net.glowstone.util.BlockStateDelegate;

import java.util.Random;

public class TallBirchTree extends BirchTree {

    public TallBirchTree(Random random, BlockStateDelegate delegate) {
        super(random, delegate);
        setHeight(height + random.nextInt(7));
    }
}
