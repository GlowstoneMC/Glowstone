package net.glowstone.generator.objects.trees;

import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;

import java.util.Random;

public class BigTree extends BigOakTree {

    public BigTree(Random random, Location location, BlockStateDelegate delegate) {
        super(random, location, delegate);
        setMaxLeafDistance(4);
    }
}
