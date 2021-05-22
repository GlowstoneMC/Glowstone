package net.glowstone.block.blocktype;

import java.util.Collection;
import java.util.Collections;
import net.glowstone.block.GlowBlock;
import net.glowstone.inventory.MaterialMatcher;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class BlockNeedsTool extends BlockType {

    @NotNull
    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        MaterialMatcher neededTool = getNeededMiningTool(block);
        if (neededTool != null
            && (tool == null || !neededTool.matches(tool.getType()))) {
            return Collections.emptyList();
        }

        return getMinedDrops(block);
    }

    public abstract MaterialMatcher getNeededMiningTool(GlowBlock block);
}
