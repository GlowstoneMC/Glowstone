package net.glowstone.constants;

import static org.bukkit.TreeType.ACACIA;
import static org.bukkit.TreeType.BIG_TREE;
import static org.bukkit.TreeType.BIRCH;
import static org.bukkit.TreeType.BROWN_MUSHROOM;
import static org.bukkit.TreeType.COCOA_TREE;
import static org.bukkit.TreeType.DARK_OAK;
import static org.bukkit.TreeType.JUNGLE;
import static org.bukkit.TreeType.JUNGLE_BUSH;
import static org.bukkit.TreeType.MEGA_REDWOOD;
import static org.bukkit.TreeType.REDWOOD;
import static org.bukkit.TreeType.RED_MUSHROOM;
import static org.bukkit.TreeType.SMALL_JUNGLE;
import static org.bukkit.TreeType.SWAMP;
import static org.bukkit.TreeType.TALL_BIRCH;
import static org.bukkit.TreeType.TALL_REDWOOD;
import static org.bukkit.TreeType.TREE;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.glowstone.generator.objects.trees.AcaciaTree;
import net.glowstone.generator.objects.trees.BigTree;
import net.glowstone.generator.objects.trees.BirchTree;
import net.glowstone.generator.objects.trees.BrownMushroomTree;
import net.glowstone.generator.objects.trees.CocoaTree;
import net.glowstone.generator.objects.trees.DarkOakTree;
import net.glowstone.generator.objects.trees.GenericTree;
import net.glowstone.generator.objects.trees.JungleBush;
import net.glowstone.generator.objects.trees.JungleTree;
import net.glowstone.generator.objects.trees.MegaJungleTree;
import net.glowstone.generator.objects.trees.MegaRedwoodTree;
import net.glowstone.generator.objects.trees.RedMushroomTree;
import net.glowstone.generator.objects.trees.RedwoodTree;
import net.glowstone.generator.objects.trees.SwampTree;
import net.glowstone.generator.objects.trees.TallBirchTree;
import net.glowstone.generator.objects.trees.TallRedwoodTree;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;
import org.bukkit.TreeType;

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

    public static GenericTree newInstance(TreeType type, Random random, Location loc,
        BlockStateDelegate delegate) {
        try {
            Constructor<? extends GenericTree> c = classTable.get(type)
                .getConstructor(Random.class, Location.class, BlockStateDelegate.class);
            return c.newInstance(random, loc, delegate);
        } catch (Exception ex) {
            return new GenericTree(random, loc, delegate);
        }
    }

    private static void set(TreeType type, Class<? extends GenericTree> clazz) {
        classTable.put(type, clazz);
    }
}
