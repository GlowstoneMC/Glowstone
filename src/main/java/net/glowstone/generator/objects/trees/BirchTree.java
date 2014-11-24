package net.glowstone.generator.objects.trees;

import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;

import java.util.Random;

public class BirchTree extends GenericTree {

    public BirchTree(Random random, Location location, BlockStateDelegate delegate) {
        super(random, location, delegate);
        setHeight(random.nextInt(3) + 5);
        setTypes(2, 2);
    }
}
