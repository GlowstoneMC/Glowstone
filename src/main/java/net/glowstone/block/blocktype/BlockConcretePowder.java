package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

public class BlockConcretePowder extends BlockFalling {

    public BlockConcretePowder() {
        // TODO: 1.13: material colored powder
        super(Material.LEGACY_CONCRETE_POWDER);
    }

    @Override
    public void onNearBlockChanged(GlowBlock me, BlockFace face, GlowBlock other, Material oldType,
        byte oldData, Material newType, byte newData) {
        if ((face == BlockFace.NORTH
                        || face == BlockFace.EAST
                        || face == BlockFace.SOUTH
                        || face == BlockFace.WEST
                        || face == BlockFace.UP
                        || face == BlockFace.DOWN)
                && other.isLiquid()) {
            me.setType(Material.LEGACY_CONCRETE);
        } else {
            super.onNearBlockChanged(me, face, other, oldType, oldData, newType, newData);
        }
    }

    @Override
    public void onBlockChanged(GlowBlock block, Material oldType, byte oldData, Material newType,
        byte data) {
        for (BlockFace face : ADJACENT) {
            if (block.getRelative(face).isLiquid()) {
                block.setType(Material.LEGACY_CONCRETE);
            }
        }
    }
}
