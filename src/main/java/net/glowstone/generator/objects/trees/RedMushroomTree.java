package net.glowstone.generator.objects.trees;

import java.util.Random;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;
import org.bukkit.Material;

public class RedMushroomTree extends BrownMushroomTree {

    public RedMushroomTree(Random random, Location location, BlockStateDelegate delegate) {
        super(random, location, delegate);
        type = Material.HUGE_MUSHROOM_2;
    }
}
