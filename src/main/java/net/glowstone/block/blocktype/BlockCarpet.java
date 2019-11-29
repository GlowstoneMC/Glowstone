package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.block.BlockFace;

public class BlockCarpet extends BlockNeedsAttached {

    @Override
    public boolean canPlaceAt(GlowPlayer player, GlowBlock block, BlockFace against) {
        return !block.getWorld().getBlockAt(block.getX(), block.getY() - 1, block.getZ()).getType().isEmpty();
    }
}
