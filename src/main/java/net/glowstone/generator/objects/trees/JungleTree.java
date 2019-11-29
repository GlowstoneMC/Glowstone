package net.glowstone.generator.objects.trees;

import java.util.Random;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Material;

public class JungleTree extends GenericTree {

    /**
     * Initializes this tree with a random height, preparing it to attempt to generate.
     *
     * @param random   the PRNG
     * @param delegate the BlockStateDelegate used to check for space and to fill wood and leaf
     */
    public JungleTree(Random random, BlockStateDelegate delegate) {
        super(random, delegate);
        setHeight(random.nextInt(7) + 4);
        setTypes(Material.JUNGLE_LOG, Material.JUNGLE_LEAVES);
    }
}
