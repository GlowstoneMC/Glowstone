package net.glowstone.generator.objects.trees;

import java.util.Random;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Material;

public class BirchTree extends GenericTree {

    /**
     * Initializes this tree with a random height, preparing it to attempt to generate.
     *
     * @param random   the PRNG
     * @param delegate the BlockStateDelegate used to check for space and to fill wood and leaf
     */
    public BirchTree(Random random, BlockStateDelegate delegate) {
        super(random, delegate);
        setHeight(random.nextInt(3) + 5);
        setTypes(Material.BIRCH_LOG, Material.BIRCH_LEAVES);
    }
}
