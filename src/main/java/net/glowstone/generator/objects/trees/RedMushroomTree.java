package net.glowstone.generator.objects.trees;

import java.util.Random;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;
import org.bukkit.Material;

public class RedMushroomTree extends BrownMushroomTree {

    /**
     * Initializes this mushroom, preparing it to attempt to generate.
     *
     * @param random the PRNG
     * @param location the base of the trunk
     * @param delegate the BlockStateDelegate used to check for space and to fill wood and leaf
     *     blocks
     */
    public RedMushroomTree(Random random, Location location, BlockStateDelegate delegate) {
        super(random, location, delegate);
        type = Material.HUGE_MUSHROOM_2;
    }
}
