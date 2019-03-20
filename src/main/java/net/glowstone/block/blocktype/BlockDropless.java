package net.glowstone.block.blocktype;

import java.util.Collection;
import java.util.Collections;
import net.glowstone.block.GlowBlock;
import org.bukkit.inventory.ItemStack;

public class BlockDropless extends BlockType {

    @Override
    public final Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        return Collections.emptyList();
    }
}
