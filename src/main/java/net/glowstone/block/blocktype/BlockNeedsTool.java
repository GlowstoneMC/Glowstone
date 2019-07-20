package net.glowstone.block.blocktype;

import java.util.Collection;
import java.util.Collections;
import net.glowstone.block.GlowBlock;
import net.glowstone.inventory.MaterialMatcher;
import org.bukkit.inventory.ItemStack;

public abstract class BlockNeedsTool extends BlockType {

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        MaterialMatcher neededTool = getNeededMiningTool(block);
        if (neededTool != null
            && (tool == null || !neededTool.matches(tool.getType()))) {
            return Collections.emptyList();
        }

        return getMinedDrops(block);
    }

    protected abstract MaterialMatcher getNeededMiningTool(GlowBlock block);
}
