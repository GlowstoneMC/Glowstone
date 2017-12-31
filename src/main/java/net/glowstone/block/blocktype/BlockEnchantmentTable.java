package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.entity.BlockEntity;
import net.glowstone.block.entity.EnchantingTableEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.MaterialMatcher;
import net.glowstone.inventory.ToolType;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class BlockEnchantmentTable extends BlockNeedsTool {

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face,
        Vector clickedLoc) {
        return player.openEnchanting(block.getLocation(), false) != null;
    }

    @Override
    public BlockEntity createBlockEntity(GlowBlock block) {
        return new EnchantingTableEntity(block);
    }

    @Override
    protected MaterialMatcher getNeededMiningTool(GlowBlock block) {
        return ToolType.PICKAXE;
    }
}
