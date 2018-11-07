package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.block.BlockFace;

public abstract class BlockClimbable extends BlockType {

    @Override
    public boolean canPlaceAt(GlowPlayer player, GlowBlock block, BlockFace against) {
        return against != BlockFace.DOWN && against != BlockFace.UP && isTargetOccluding(block,
            against.getOppositeFace());
    }

    protected boolean isTargetOccluding(GlowBlockState state, BlockFace face) {
        return isTargetOccluding(state.getBlock(), face);
    }

    protected boolean isTargetOccluding(GlowBlock block, BlockFace face) {
        return block.getRelative(face).getType().isOccluding();
    }
}
