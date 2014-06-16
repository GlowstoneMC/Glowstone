package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class BlockRandomDrops extends BlockType {
    private final Random random = new Random();
    private final Material dropType;
    private final short data;
    private final int minDrops;
    private final int maxDrops;

    public BlockRandomDrops(Material dropType, int data, int minDrops, int maxDrops) {
        this.dropType = dropType;
        this.data = (short) data;
        this.minDrops = minDrops;
        this.maxDrops = maxDrops;
    }

    public BlockRandomDrops(Material dropType, int minDrops, int maxDrops) {
        this(dropType, 0, minDrops, maxDrops);
    }

    public BlockRandomDrops(Material dropType, int maxDrops) {
        this(dropType, 1, maxDrops);
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block) {
        return Collections.unmodifiableList(Arrays.asList(new ItemStack(dropType, random.nextInt(maxDrops - minDrops + 1) + minDrops, data)));
    }
}
