package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class BlockDeadBush extends BlockNeedsAttached {

    @Override
    public Collection<ItemStack> getDrops(GlowBlock me, ItemStack tool) {
        return BlockDropless.EMPTY_STACK;
    }
}
