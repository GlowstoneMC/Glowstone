package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.ItemTable;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.material.MaterialData;
import org.bukkit.material.SimpleAttachableMaterialData;

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

    @Override
    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        return ItemTable.instance().getBlock(block.getRelative(against).getType()) instanceof BlockNeedsAttached;
    }

    protected void dropMe(GlowBlock me) {
        me.breakNaturally();
    }

    protected BlockFace getAttachedFace(GlowBlock me) {
        MaterialData data = me.getState().getData();
        if (data instanceof SimpleAttachableMaterialData) {
            return ((SimpleAttachableMaterialData) data).getAttachedFace();
        } else {
            return BlockFace.DOWN;
        }
    }
}
