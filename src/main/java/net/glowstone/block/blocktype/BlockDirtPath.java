package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

public class BlockDirtPath extends BlockDirectDrops {

    public BlockDirtPath() {
        super(Material.DIRT);
    }

    @Override
    public void onNearBlockChanged(GlowBlock block, BlockFace face, GlowBlock changedBlock,
                                   Material oldType, byte oldData, Material newType, byte newData) {
        if (face == BlockFace.UP && newType.isSolid()) {
            block.setType(Material.DIRT);
        }
    }
}
