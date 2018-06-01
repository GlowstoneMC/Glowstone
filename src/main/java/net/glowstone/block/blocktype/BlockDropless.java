package net.glowstone.block.blocktype;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import net.glowstone.block.GlowBlock;
import org.bukkit.inventory.ItemStack;

public class BlockDropless extends BlockType {

    public static final Collection<ItemStack> EMPTY_STACK = Collections
        .unmodifiableList(Arrays.asList(new ItemStack[0]));

    @Override
    public final Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        return EMPTY_STACK;
    }
}
