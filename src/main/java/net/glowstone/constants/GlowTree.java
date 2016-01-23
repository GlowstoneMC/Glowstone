package net.glowstone.constants;

import net.glowstone.generator.objects.trees.*;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;
import org.bukkit.TreeType;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.bukkit.TreeType.*;

public final class GlowTree {

    private static final Map<TreeType, Class<? extends GenericTree>> classTable = new HashMap<>();

    static {
        set(TREE, GenericTree.class);
        set(BIG_TREE, BigTree.class);
        set(REDWOOD, RedwoodTree.class);
        set(TALL_REDWOOD, TallRedwoodTree.class);
        set(BIRCH, BirchTree.class);
        set(JUNGLE, MegaJungleTree.class);
        set(SMALL_JUNGLE, JungleTree.class);
        set(COCOA_TREE, CocoaTree.class);
        set(JUNGLE_BUSH, JungleBush.class);
        set(RED_MUSHROOM, RedMushroomTree.class);
        set(BROWN_MUSHROOM, BrownMushroomTree.class);
        set(SWAMP, SwampTree.class);
        set(ACACIA, AcaciaTree.class);
        set(DARK_OAK, DarkOakTree.class);
        set(MEGA_REDWOOD, MegaRedwoodTree.class);
        set(TALL_BIRCH, TallBirchTree.class);
    }

    private GlowTree() {
    }

    public static GenericTree newInstance(TreeType type, Random random, Location loc, BlockStateDelegate delegate) {
        try {
            Constructor<? extends GenericTree> c = classTable.get(type).getConstructor(Random.class, Location.class, BlockStateDelegate.class);
            return c.newInstance(random, loc, delegate);
        } catch (Exception ex) {
            return new GenericTree(random, loc, delegate);
        }
    }

    private static void set(TreeType type, Class<? extends GenericTree> clazz) {
        classTable.put(type, clazz);
    }
}
