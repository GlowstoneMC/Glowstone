package net.glowstone.generator.objects.trees;

import java.util.Random;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;

public class BirchTree extends GenericTree {

    /**
     * Initializes this tree with a random height, preparing it to attempt to generate.
     *
     * @param random the PRNG
     * @param location the base of the trunk
     * @param delegate the BlockStateDelegate used to check for space and to fill wood and leaf
     *     blocks
     */
    public BirchTree(Random random, Location location, BlockStateDelegate delegate) {
        super(random, location, delegate);
        setHeight(random.nextInt(3) + 5);
        setTypes(2, 2);
    }
}
