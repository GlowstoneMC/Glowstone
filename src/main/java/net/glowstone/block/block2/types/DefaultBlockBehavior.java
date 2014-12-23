package net.glowstone.block.block2.types;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.block2.behavior.BlockBehavior;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;

/**
 * Default block behavior used when no other behavior is specified.
 */
public final class DefaultBlockBehavior implements BlockBehavior {

    public static final DefaultBlockBehavior instance = new DefaultBlockBehavior();

    private DefaultBlockBehavior() {
    }

    @Override
    public void doThing() {
    }

    @Override
    public boolean getValue() {
        return false;
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        return Arrays.asList(new ItemStack(block.getType(), 1, block.getData()));
    }
}
