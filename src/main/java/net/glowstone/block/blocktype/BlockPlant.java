package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;

public class BlockPlant extends BlockType {

    @Override
    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        final Material type = block.getRelative(BlockFace.DOWN).getType();
        if (type.equals(Material.GRASS) || type.equals(Material.DIRT)
                || type.equals(Material.SOIL)) {
            return true;
        }
        return false;
    }
}
