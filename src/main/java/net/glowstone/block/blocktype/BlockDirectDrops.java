package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class BlockDirectDrops extends BlockType {
    private final Material dropType;
    private final short data;
    private final int amount;

    public BlockDirectDrops(Material dropType, int data, int amount) {
        this.dropType = dropType;
        this.amount = amount;
        this.data = (short) data;
    }

    public BlockDirectDrops(Material dropType, int amount) {
        this(dropType, 0, amount);
    }

    public BlockDirectDrops(Material dropType) {
        this(dropType, 0, 1);
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block) {
        return Collections.unmodifiableList(Arrays.asList(new ItemStack(dropType, amount, data)));
    }
}
