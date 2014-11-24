package net.glowstone.generator.objects.trees;

import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;

import java.util.Random;

public class JungleTree extends GenericTree {

    public JungleTree(Random random, Location location, BlockStateDelegate delegate) {
        super(random, location, delegate);
        setHeight(random.nextInt(7) + 4);
        setTypes(3, 3);
    }
}
