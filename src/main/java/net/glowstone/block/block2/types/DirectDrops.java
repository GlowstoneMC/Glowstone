package net.glowstone.block.block2.types;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.block2.behavior.BaseBlockBehavior;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;

public class DirectDrops extends BaseBlockBehavior {
    private final Material type;
    private final int amount;
    private final short data;

    public DirectDrops(Material type, int amount, int data) {
        this.type = type;
        this.amount = amount;
        this.data = (short) data;
    }

    public DirectDrops(Material type, int amount) {
        this(type, 0, amount);
    }

    public DirectDrops(Material type) {
        this(type, 0, 1);
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        return Arrays.asList(new ItemStack(type, amount, data));
    }

}
