package net.glowstone.generator.objects.trees;

import java.util.Random;
import net.glowstone.util.BlockStateDelegate;

public class TallBirchTree extends BirchTree {

    public TallBirchTree(Random random, BlockStateDelegate delegate) {
        super(random, delegate);
        setHeight(height + random.nextInt(7));
    }
}
