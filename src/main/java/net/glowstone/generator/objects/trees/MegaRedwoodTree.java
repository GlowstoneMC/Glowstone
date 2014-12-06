package net.glowstone.generator.objects.trees;

import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;

import java.util.Random;

public class MegaRedwoodTree extends MegaJungleTree {

    private int leavesHeight;

    public MegaRedwoodTree(Random random, Location location, BlockStateDelegate delegate) {
        super(random, location, delegate);
        setHeight(random.nextInt(15) + random.nextInt(3) + 13);
        setTypes(1, 1);
        setLeavesHeight(random.nextInt(5) + (random.nextBoolean() ? 3 : 13));
    }

    protected final void setLeavesHeight(int leavesHeight) {
        this.leavesHeight = leavesHeight;
    }

    @Override
    public boolean generate() {
        if (!canHeightFit() || !canPlaceOn() || !canPlace()) {
            return false;
        }

        // generates the leaves
        int previousRadius = 0;
        for (int y = loc.getBlockY() + height - leavesHeight; y <= loc.getBlockY() + height; y++) {
            int n = loc.getBlockY() + height - y;
            int radius = (int) Math.floor(((float) n / (float) leavesHeight) * 3.5F);
            if (radius == previousRadius && n > 0 && y % 2 == 0) {
                radius++;
            }
            generateLeaves(loc.getBlockX(), y, loc.getBlockZ(), radius, false);
            previousRadius = radius;
        }

        // generates the trunk
        generateTrunk();

        // blocks below trunk are always dirt
        generateDirtBelowTrunk();

        return true;
    }
}
