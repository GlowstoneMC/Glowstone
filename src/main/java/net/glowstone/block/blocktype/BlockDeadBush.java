package net.glowstone.block.blocktype;

import java.util.Collection;
import net.glowstone.block.GlowBlock;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

public class BlockDeadBush extends BlockNeedsAttached {

    @Override
    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        int typeIdBelow = block.getWorld()
            .getBlockTypeIdAt(block.getX(), block.getY() - 1, block.getZ());
        switch (Material.getMaterial(typeIdBelow)) {
            case SAND:
            case STAINED_CLAY:
            case HARD_CLAY:
            case DIRT:
                return true;
            default:
                return false;
        }
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock me, ItemStack tool) {
        return BlockDropless.EMPTY_STACK;
    }
}
