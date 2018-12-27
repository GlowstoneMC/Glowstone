package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.ItemTable;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Sign;
import org.bukkit.material.SimpleAttachableMaterialData;

public class BlockNeedsAttached extends BlockType {

    @Override
    public void onNearBlockChanged(GlowBlock block, BlockFace face, GlowBlock changedBlock,
        Material oldType, byte oldData, Material newType, byte newData) {
        if (face == getAttachedFace(block)) {
            updatePhysics(block);
        }
    }

    @Override
    public void updatePhysicsAfterEvent(GlowBlock me) {
        super.updatePhysicsAfterEvent(me);
        BlockFace attachedTo = getAttachedFace(me);
        if (attachedTo == null) {
            return;
        }
        if (me.getRelative(attachedTo).getType() == Material.AIR || !canPlaceAt(null, me,
            attachedTo.getOppositeFace())) {
            dropMe(me);
        }
    }

    /**
     * Called to determine if the target block can be attached to
     * when right clicking it.
     *
     * @param block The location the block is being placed at.
     * @param against The face the block is being placed against.
     * @return Whether the black can be attached to.
    */
    public boolean canAttachTo(GlowBlock block, BlockFace against) {
        return !(ItemTable.instance().getBlock(
            block.getRelative(against.getOppositeFace()).getType()) instanceof BlockNeedsAttached);
    }

    @Override
    public boolean canPlaceAt(GlowPlayer player, GlowBlock block, BlockFace against) {
        return !(!canAttachTo(block, against) && against == BlockFace.UP);
    }

    protected void dropMe(GlowBlock me) {
        me.breakNaturally();
    }

    protected BlockFace getAttachedFace(GlowBlock me) {
        MaterialData data = me.getState().getData();
        if (data instanceof SimpleAttachableMaterialData) {
            return ((SimpleAttachableMaterialData) data).getAttachedFace();
        } else if (data instanceof Sign) {
            return ((Sign) data).getAttachedFace();
        } else {
            return BlockFace.DOWN;
        }
    }
}
