package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

public class BlockNeedsAttached extends BlockType {
    @Override
    public void onNearBlockChanged(GlowBlock block, BlockFace face, GlowBlock changedBlock, Material oldType, byte oldData, Material newType, byte newData) {
        if (face == getAttachedFace(block)) {
            updatePhysics(block);
        }
    }

    @Override
    public void updatePhysics(GlowBlock me) {
        BlockFace attachedTo = getAttachedFace(me);
        if (me.getRelative(attachedTo).getType() == Material.AIR || !canPlaceAt(me, attachedTo)) {
            dropMe(me);
        }
    }

    protected void dropMe(GlowBlock me) {
        me.breakNaturally();
    }

    protected BlockFace getAttachedFace(GlowBlock me) {
        return BlockFace.DOWN;
    }
}
