package net.glowstone.generator.objects.trees;

import java.util.Random;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;

public class MegaRedwoodTree extends MegaJungleTree {

    protected int leavesHeight;

    /**
     * Initializes this tree with a random height, preparing it to attempt to generate.
     *  @param random the PRNG
     * @param delegate the BlockStateDelegate used to check for space and to fill wood and leaf
     */
    public MegaRedwoodTree(Random random, BlockStateDelegate delegate) {
        super(random, delegate);
        setHeight(random.nextInt(15) + random.nextInt(3) + 13);
        setTypes(1, 1);
        setLeavesHeight(random.nextInt(5) + (random.nextBoolean() ? 3 : 13));
    }

    protected final void setLeavesHeight(int leavesHeight) {
        this.leavesHeight = leavesHeight;
    }

    @Override
    public boolean generate(Location loc) {
        if (cannotGenerateAt(loc)) {
            return false;
        }

        // generates the leaves
        int previousRadius = 0;
        for (int y = loc.getBlockY() + height - leavesHeight; y <= loc.getBlockY() + height; y++) {
            int n = loc.getBlockY() + height - y;
            int radius = (int) Math.floor((float) n / leavesHeight * 3.5F);
            if (radius == previousRadius && n > 0 && y % 2 == 0) {
                radius++;
            }
            generateLeaves(loc.getBlockX(), y, loc.getBlockZ(), radius, false, loc.getWorld());
            previousRadius = radius;
        }

        // generates the trunk
        generateTrunk(loc);

        // blocks below trunk are always dirt
        generateDirtBelowTrunk(loc);

        return true;
    }

    @Override
    protected void generateDirtBelowTrunk(Location loc) {
        // mega redwood tree does not replaces blocks below (surely to preserves podzol)
    }
}
