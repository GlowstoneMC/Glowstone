package net.glowstone.generator.objects.trees;

import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Material;

import java.util.Random;

public class RedMushroomTree extends BrownMushroomTree {

    /**
     * Initializes this mushroom, preparing it to attempt to generate.
     *
     * @param random the PRNG
     * @param delegate the BlockStateDelegate used to check for space and to fill wood and leaf
     */
    public RedMushroomTree(Random random, BlockStateDelegate delegate) {
        super(random, delegate);
        type = Material.RED_MUSHROOM_BLOCK;
    }
}
