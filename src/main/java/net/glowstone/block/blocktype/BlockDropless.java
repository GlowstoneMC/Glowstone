package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public class BlockDropless extends BlockType {

    @NotNull
    @Override
    public final Collection<ItemStack> getDrops(@NotNull GlowBlock block, ItemStack tool) {
        return Collections.emptyList();
    }
}
