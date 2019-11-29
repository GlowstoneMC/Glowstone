package net.glowstone.generator.objects.trees;

import java.util.Random;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Material;
import org.bukkit.World;

public class MegaRedwoodTree extends MegaJungleTree {

    protected int leavesHeight;

    /**
     * Initializes this tree with a random height, preparing it to attempt to generate.
     *
     * @param random   the PRNG
     * @param delegate the BlockStateDelegate used to check for space and to fill wood and leaf
     */
    public MegaRedwoodTree(Random random, BlockStateDelegate delegate) {
        super(random, delegate);
        setHeight(random.nextInt(15) + random.nextInt(3) + 13);
        setTypes(Material.SPRUCE_LOG, Material.SPRUCE_LEAVES);
        setLeavesHeight(random.nextInt(5) + (random.nextBoolean() ? 3 : 13));
    }

    protected final void setLeavesHeight(int leavesHeight) {
        this.leavesHeight = leavesHeight;
    }

    @Override
    public boolean generate(World world, Random random, int blockX, int blockY, int blockZ) {
        if (cannotGenerateAt(blockX, blockY, blockZ, world)) {
            return false;
        }

        // generates the leaves
        int previousRadius = 0;
        for (int y = blockY + height - leavesHeight; y <= blockY + height; y++) {
            int n = blockY + height - y;
            int radius = (int) Math.floor((float) n / leavesHeight * 3.5F);
            if (radius == previousRadius && n > 0 && y % 2 == 0) {
                radius++;
            }
            generateLeaves(blockX, y, blockZ, radius, false, world);
            previousRadius = radius;
        }

        // generates the trunk
        generateTrunk(world, blockX, blockY, blockZ);

        // blocks below trunk are always dirt
        generateDirtBelowTrunk(world, blockX, blockY, blockZ);

        return true;
    }

    @Override
    protected void generateDirtBelowTrunk(World world, int blockX, int blockY,
                                          int blockZ) {
        // mega redwood tree does not replaces blocks below (surely to preserves podzol)
    }
}
