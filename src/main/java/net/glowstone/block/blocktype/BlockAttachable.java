package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;
import org.bukkit.material.SimpleAttachableMaterialData;

public class BlockAttachable extends BlockNeedsAttached {

    public void setAttachedFace(BlockState state, BlockFace attachedFace) {
        byte data = state.getRawData();
        switch (attachedFace) {
            case UP:
                data |= 0;
                break;
            case WEST:
                data |= 1;
                break;
            case EAST:
                data |= 2;
                break;
            case NORTH:
                data |= 3;
                break;
            case SOUTH:
                data |= 4;
                break;
            case DOWN:
                data |= 5;
                break;
        }
        state.setRawData(data);
    }

    @Override
    protected BlockFace getAttachedFace(GlowBlock me) {
        MaterialData data = me.getState().getData();
        if (data instanceof SimpleAttachableMaterialData) {
            return ((SimpleAttachableMaterialData) data).getAttachedFace();
        } else {
            warnMaterialData(SimpleAttachableMaterialData.class, data);
            return BlockFace.DOWN;
        }
    }
}
