package net.glowstone.block.blocktype;

import java.util.Collection;
import java.util.Collections;
import net.glowstone.block.GlowBlock;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BlockDropless extends BlockType {

    @NotNull
    @Override
    public final Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        return Collections.emptyList();
    }
}
