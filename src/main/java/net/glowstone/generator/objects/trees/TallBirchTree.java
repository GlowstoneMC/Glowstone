package net.glowstone.generator.objects.trees;

import java.util.Random;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;

public class TallBirchTree extends BirchTree {

    public TallBirchTree(Random random, Location location, BlockStateDelegate delegate) {
        super(random, location, delegate);
        setHeight(height + random.nextInt(7));
    }
}
