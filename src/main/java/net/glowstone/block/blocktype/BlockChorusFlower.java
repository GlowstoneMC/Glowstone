package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

public class BlockChorusFlower extends BlockType {

    private static final BlockFace[] FACES = new BlockFace[] { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };

    @Override
    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        GlowBlock under = block.getRelative(BlockFace.DOWN);
        if (under.getType() == Material.ENDER_STONE || under.getType() == Material.CHORUS_PLANT) {
            return true;
        } else if (under.getType() == Material.AIR) {
            boolean hasSupport = false;
            for (BlockFace side : FACES) {
                GlowBlock relative = block.getRelative(side);
                if (relative.getType() == Material.CHORUS_PLANT) {
                    if (hasSupport) { //only one chorus plant allowed on the side
                        return false;
                    } else {
                        hasSupport = true;
                    }
                }
            }
            if (hasSupport) {
                return true;
            }
        }
        return false;
    }
}
