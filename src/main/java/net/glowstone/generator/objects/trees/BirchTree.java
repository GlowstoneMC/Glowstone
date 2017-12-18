package net.glowstone.generator.objects.trees;

import java.util.Random;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;

public class BirchTree extends GenericTree {

    /**
     * Sets a random height and prepares a tree to generate.
     *
     * @param random the PRNG to use for generation
     * @param location the bottom of the trunk
     * @param delegate the block-state delegate which will be used to check whether this tree can
     *     grow, handle updating of blocks with wood and leaves if it does grow
     */
    public BirchTree(Random random, Location location, BlockStateDelegate delegate) {
        super(random, location, delegate);
        setHeight(random.nextInt(3) + 5);
        setTypes(2, 2);
    }
}
