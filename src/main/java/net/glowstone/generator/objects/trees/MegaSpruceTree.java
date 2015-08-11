package net.glowstone.generator.objects.trees;

import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;

import java.util.Random;

public class MegaSpruceTree extends MegaPineTree {

    public MegaSpruceTree(Random random, Location location, BlockStateDelegate delegate) {
        super(random, location, delegate);
        setLeavesHeight(leavesHeight + 10);
    }
}
