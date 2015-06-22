package net.glowstone.generator.objects.trees;

import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Random;

public class RedMushroomTree extends BrownMushroomTree {

    public RedMushroomTree(Random random, Location location, BlockStateDelegate delegate) {
        super(random, location, delegate);
        this.type = Material.HUGE_MUSHROOM_2;
    }
}
