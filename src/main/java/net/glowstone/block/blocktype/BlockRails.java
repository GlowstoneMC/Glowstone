package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class BlockRails extends BlockNeedsAttached {
    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        return Collections.singletonList(new ItemStack(block.getType()));
    }
}
