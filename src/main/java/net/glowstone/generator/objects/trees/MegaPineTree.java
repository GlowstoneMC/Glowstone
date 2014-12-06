package net.glowstone.generator.objects.trees;

import java.util.Random;

import net.glowstone.util.BlockStateDelegate;

import org.bukkit.Location;

public class MegaPineTree extends MegaRedwoodTree {

    public MegaPineTree(Random random, Location location, BlockStateDelegate delegate) {
        super(random, location, delegate);
        setLeavesHeight(random.nextInt(5) + 3);
    }
}
