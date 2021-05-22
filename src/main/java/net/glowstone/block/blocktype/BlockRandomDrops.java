package net.glowstone.block.blocktype;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;
import net.glowstone.block.GlowBlock;
import net.glowstone.inventory.MaterialMatcher;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A block type that drops a random number of items when broken, and isn't affected by the Fortune
 * enchantment (unlike {@link BlockOre}).
 */
public class BlockRandomDrops extends BlockNeedsTool {

    private final Material dropType;
    private final short data;
    private final int minDrops;
    private final int maxDrops;
    private final MaterialMatcher neededTool;

    /**
     * Creates a block type that drops a random number of items when broken.
     *
     * @param dropType   the item to drop when broken
     * @param data       the block-data or damage value for the dropped item
     * @param minDrops   the minimum number of items to drop when broken
     * @param maxDrops   the maximum number of items to drop when broken
     * @param neededTool the tool(s) that can break this block, or null if no tool is needed
     */
    public BlockRandomDrops(Material dropType, int data, int minDrops, int maxDrops,
                            MaterialMatcher neededTool) {
        this.dropType = dropType;
        this.neededTool = neededTool;
        this.data = (short) data;
        this.minDrops = minDrops;
        this.maxDrops = maxDrops;
    }

    /**
     * Creates a block type that drops a random number of items when broken, and can be broken with
     * no tool.
     *
     * @param dropType the item to drop when broken
     * @param data     the block-data or damage value for the dropped item
     * @param minDrops the minimum number of items to drop when broken
     * @param maxDrops the maximum number of items to drop when broken
     */
    public BlockRandomDrops(Material dropType, int data, int minDrops, int maxDrops) {
        this(dropType, data, minDrops, maxDrops, null);
    }

    /**
     * Creates a block type that drops a random number of items with block-data or damage value 0
     * when broken, and can be broken with no tool.
     *
     * @param dropType the item to drop when broken
     * @param minDrops the minimum number of items to drop when broken
     * @param maxDrops the maximum number of items to drop when broken
     */
    public BlockRandomDrops(Material dropType, int minDrops, int maxDrops) {
        this(dropType, 0, minDrops, maxDrops, null);
    }

    /**
     * Creates a block type that drops a random number of items that's at least 1, with block-data
     * or damage value 0, and can be broken with no tool.
     *
     * @param dropType the item to drop when broken
     * @param maxDrops the maximum number of items to drop when broken
     */
    public BlockRandomDrops(Material dropType, int maxDrops) {
        this(dropType, 1, maxDrops);
    }

    @NotNull
    @Override
    public Collection<ItemStack> getMinedDrops(GlowBlock block) {
        return Collections.unmodifiableList(Arrays.asList(new ItemStack(dropType,
            ThreadLocalRandom.current().nextInt(maxDrops - minDrops + 1) + minDrops, data)));
    }

    @Override
    public MaterialMatcher getNeededMiningTool(GlowBlock block) {
        return neededTool;
    }
}
