package net.glowstone.generator.objects.trees;

import java.util.Random;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;

public class MegaSpruceTree extends MegaPineTree {

    public MegaSpruceTree(Random random, Location location, BlockStateDelegate delegate) {
        super(random, location, delegate);
        setLeavesHeight(leavesHeight + 10);
    }
}
