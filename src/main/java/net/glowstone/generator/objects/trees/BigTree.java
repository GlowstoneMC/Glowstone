package net.glowstone.generator.objects.trees;

import java.util.Random;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;

public class BigTree extends BigOakTree {

    public BigTree(Random random, Location location, BlockStateDelegate delegate) {
        super(random, location, delegate);
        setMaxLeafDistance(4);
    }
}
