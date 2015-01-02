package net.glowstone.generator;

import java.util.Random;

import net.glowstone.generator.objects.trees.*;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;

public class TreeGenerator {

    private final BlockStateDelegate delegate;
    private boolean forceUpdate;

    public TreeGenerator() {
        this(new BlockStateDelegate());
        forceUpdate = true;
    }

    public TreeGenerator(BlockStateDelegate delegate) {
        this.delegate = delegate;
        forceUpdate = false;
    }

    public boolean generate(Random random, Location loc, TreeType type) {
        GenericTree tree;
        switch (type) {
            case TREE:
            case BIG_TREE:
                tree = new GenericTree(random, loc, delegate);
                break;
            case SWAMP:
                tree = new SwampTree(random, loc, delegate);
                break;
            case REDWOOD:
                tree = new RedwoodTree(random, loc, delegate);
                break;
            case TALL_REDWOOD:
                tree = new TallRedwoodTree(random, loc, delegate);
                break;
            case MEGA_REDWOOD:
                tree = new MegaRedwoodTree(random, loc, delegate);
                break;
            case BIRCH:
                tree = new BirchTree(random, loc, delegate);
                break;
            case TALL_BIRCH:
                tree = new TallBirchTree(random, loc, delegate);
                break;
            case JUNGLE:
                tree = new MegaJungleTree(random, loc, delegate);
                break;
            case SMALL_JUNGLE:
                tree = new JungleTree(random, loc, delegate);
                break;
            case COCOA_TREE:
                tree = new CocoaTree(random, loc, delegate);
                break;
            case JUNGLE_BUSH:
                tree = new JungleBush(random, loc, delegate);
                break;
            case ACACIA:
                tree = new AcaciaTree(random, loc, delegate);
                break;
            case DARK_OAK:
                tree = new DarkOakTree(random, loc, delegate);
                break;
            case BROWN_MUSHROOM:
                tree = new HugeMushroom(random, loc, Material.HUGE_MUSHROOM_1, delegate);
                break;
            case RED_MUSHROOM:
                tree = new HugeMushroom(random, loc, Material.HUGE_MUSHROOM_2, delegate);
                break;
            default:
                return false;
        }

        if (tree.generate()) {
            if (forceUpdate) {
                delegate.updateBlockStates();
            }
            return true;
        }

        return false;
    }
}
