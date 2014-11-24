package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.inventory.MaterialMatcher;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class BlockRandomDrops extends BlockNeedsTool {
    private final Random random = new Random();
    private final Material dropType;
    private final short data;
    private final int minDrops;
    private final int maxDrops;
    private final MaterialMatcher neededTool;

    public BlockRandomDrops(Material dropType, int data, int minDrops, int maxDrops, MaterialMatcher neededTool) {
        this.dropType = dropType;
        this.neededTool = neededTool;
        this.data = (short) data;
        this.minDrops = minDrops;
        this.maxDrops = maxDrops;
    }

    public BlockRandomDrops(Material dropType, int data, int minDrops, int maxDrops) {
        this(dropType, data, minDrops, maxDrops, null);
    }

    public BlockRandomDrops(Material dropType, int minDrops, int maxDrops) {
        this(dropType, 0, minDrops, maxDrops, null);
    }

    public BlockRandomDrops(Material dropType, int maxDrops) {
        this(dropType, 1, maxDrops);
    }

    @Override
    public Collection<ItemStack> getMinedDrops(GlowBlock block, ItemStack tool) {
        return Collections.unmodifiableList(Arrays.asList(new ItemStack(dropType, random.nextInt(maxDrops - minDrops + 1) + minDrops, data)));
    }

    @Override
    protected MaterialMatcher getNeededMiningTool(GlowBlock block) {
        return neededTool;
    }
}
