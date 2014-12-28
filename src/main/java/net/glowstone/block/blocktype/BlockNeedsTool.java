package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.inventory.MaterialMatcher;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public abstract class BlockNeedsTool extends BlockType {
    @Override
    public final Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        MaterialMatcher neededTool = getNeededMiningTool(block);
        if (neededTool != null &&
                (tool == null || !neededTool.matches(tool.getType())))
            return BlockDropless.EMPTY_STACK;

        return getMinedDrops(block, tool);
    }

    protected Collection<ItemStack> getMinedDrops(GlowBlock block, ItemStack tool) {
        return super.getDrops(block, tool);
    }

    protected abstract MaterialMatcher getNeededMiningTool(GlowBlock block);
}
