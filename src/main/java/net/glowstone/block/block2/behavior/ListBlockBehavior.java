package net.glowstone.block.block2.behavior;

import io.netty.util.Signal;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.block2.types.DefaultBlockBehavior;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * BlockBehavior which uses the first child implementing each method.
 */
public final class ListBlockBehavior implements BlockBehavior {

    private static final Signal NEXT = BaseBlockBehavior.NEXT;
    private static final BlockBehavior fallback = DefaultBlockBehavior.instance;

    private final List<BlockBehavior> children = new ArrayList<>();

    public ListBlockBehavior(List<BlockBehavior> children) {
        this.children.addAll(children);
    }

    @Override
    public void doThing() {
        for (BlockBehavior child : children) {
            try {
                child.doThing();
                return;
            } catch (Signal signal) {
                signal.expect(NEXT);
            }
        }
        fallback.doThing();
    }

    @Override
    public boolean getValue() {
        for (BlockBehavior child : children) {
            try {
                return child.getValue();
            } catch (Signal signal) {
                signal.expect(NEXT);
            }
        }
        return fallback.getValue();
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        for (BlockBehavior child : children) {
            try {
                return child.getDrops(block, tool);
            } catch (Signal signal) {
                signal.expect(NEXT);
            }
        }
        return fallback.getDrops(block, tool);
    }
}
