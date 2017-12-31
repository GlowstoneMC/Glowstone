package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.entity.BlockEntity;
import net.glowstone.block.entity.DropperEntity;
import net.glowstone.inventory.MaterialMatcher;
import net.glowstone.inventory.ToolType;

public class BlockDropper extends BlockDispenser {

    @Override
    public BlockEntity createBlockEntity(GlowBlock block) {
        return new DropperEntity(block);
    }

    @Override
    protected MaterialMatcher getNeededMiningTool(GlowBlock block) {
        return ToolType.PICKAXE;
    }
}
