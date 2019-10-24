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

import com.google.common.collect.ImmutableMap;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;
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
import org.bukkit.TreeType;

public final class GlowTree {

    private static final ImmutableMap<TreeType,
            BiFunction<Random, BlockStateDelegate, ? extends GenericTree>> CONSTRUCTORS =
            ImmutableMap
                    .<TreeType, BiFunction<Random, BlockStateDelegate, ? extends GenericTree>>
                            builder()
                    .put(TREE, GenericTree::new)
                    .put(BIG_TREE, BigTree::new)
                    .put(REDWOOD, RedwoodTree::new)
                    .put(TALL_REDWOOD, TallRedwoodTree::new)
                    .put(BIRCH, BirchTree::new)
                    .put(JUNGLE, MegaJungleTree::new)
                    .put(SMALL_JUNGLE, JungleTree::new)
                    .put(COCOA_TREE, CocoaTree::new)
                    .put(JUNGLE_BUSH, JungleBush::new)
                    .put(RED_MUSHROOM, RedMushroomTree::new)
                    .put(BROWN_MUSHROOM, BrownMushroomTree::new)
                    .put(SWAMP, SwampTree::new)
                    .put(ACACIA, AcaciaTree::new)
                    .put(DARK_OAK, DarkOakTree::new)
                    .put(MEGA_REDWOOD, MegaRedwoodTree::new)
                    .put(TALL_BIRCH, TallBirchTree::new)
            .build();

    private static final Map<TreeType, Class<? extends GenericTree>> classTable = new EnumMap<>(TreeType.class);

    static {

    }

    private GlowTree() {
    }

    /**
     * Creates a tree of any vanilla type.
     *
     * @param type the tree type
     * @param random the PRNG
     * @param delegate the BlockStateDelegate used to check for space and to fill wood and leaf
     *     blocks
     * @return a new tree of {@code type}
     */
    public static GenericTree newInstance(TreeType type, Random random,
            BlockStateDelegate delegate) {
        return CONSTRUCTORS.getOrDefault(type, GenericTree::new).apply(random, delegate);
    }
}
