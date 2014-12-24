package net.glowstone.block.block2.details;

import net.glowstone.block.GlowBlock;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class RandomDrops extends BaseBlockBehavior {
    private final Random random = new Random();

    private final Material dropType;
    private final int minDrops;
    private final int maxDrops;
    private final short data;

    public RandomDrops(Material dropType, int minDrops, int maxDrops, int data) {
        this.dropType = dropType;
        this.minDrops = minDrops;
        this.maxDrops = maxDrops;
        this.data = (short) data;
    }

    public RandomDrops(Material dropType, int minDrops, int maxDrops) {
        this(dropType, 0, minDrops, maxDrops);
    }

    public RandomDrops(Material dropType, int maxDrops) {
        this(dropType, 1, maxDrops);
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        return Collections.unmodifiableList(Arrays.asList(new ItemStack(dropType, random.nextInt(maxDrops - minDrops + 1) + minDrops, data)));
    }
}
