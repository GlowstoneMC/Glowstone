package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class BlockDropless extends BlockType {
    private final Collection<ItemStack> emptyStack = Collections.unmodifiableList(Arrays.asList(new ItemStack[0]));

    @Override
    public final Collection<ItemStack> getDrops(GlowBlock block) {
        return emptyStack;
    }
}
