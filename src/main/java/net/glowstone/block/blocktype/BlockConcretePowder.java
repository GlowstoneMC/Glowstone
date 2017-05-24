package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

public class BlockConcretePowder extends BlockFalling {
    public BlockConcretePowder() {
        super(Material.CONCRETE_POWDER);
    }

    @Override
    public void onNearBlockChanged(GlowBlock me, BlockFace face, GlowBlock other, Material oldType, byte oldData, Material newType, byte newData) {
        if ((face == BlockFace.NORTH ||
                face == BlockFace.EAST ||
                face == BlockFace.SOUTH ||
                face == BlockFace.WEST ||
                face == BlockFace.UP ||
                face == BlockFace.DOWN) && other.isLiquid()) {
            me.setType(Material.CONCRETE);
        } else {
            super.onNearBlockChanged(me, face, other, oldType, oldData, newType, newData);
        }
    }

    private BlockFace[] faces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};

    @Override
    public void onBlockChanged(GlowBlock block, Material oldType, byte oldData, Material newType, byte data) {
        for (BlockFace face : faces) {
            if (block.getRelative(face).isLiquid()) {
                block.setType(Material.CONCRETE);
            }
        }
    }
}
