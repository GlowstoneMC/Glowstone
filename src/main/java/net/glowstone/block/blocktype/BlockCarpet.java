package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

public class BlockCarpet extends BlockNeedsAttached {

    @Override
    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        return block.getWorld().getBlockTypeIdAt(block.getX(), block.getY() - 1, block.getZ())
            != Material.AIR.getId();
    }
}
