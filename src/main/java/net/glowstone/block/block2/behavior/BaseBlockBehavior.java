package net.glowstone.block.block2.behavior;

import io.netty.util.Signal;
import net.glowstone.block.GlowBlock;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

/**
 * BlockBehavior for child behaviors to extend.
 */
public class BaseBlockBehavior implements BlockBehavior {

    static final Signal NEXT = Signal.valueOf("blockBehavior_next");

    protected BaseBlockBehavior() {
    }

    @Override
    public void doThing() {
        throw NEXT;
    }

    @Override
    public boolean getValue() {
        throw NEXT;
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        throw NEXT;
    }
}
