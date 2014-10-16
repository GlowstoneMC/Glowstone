package net.glowstone.block.blocktype;

import net.glowstone.GlowChunk;
import net.glowstone.block.entity.TEBrewingStand;
import net.glowstone.block.entity.TileEntity;

public class BlockBrewingStand extends BlockContainer {

    @Override
    public TileEntity createTileEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new TEBrewingStand(chunk.getBlock(cx, cy, cz));
    }
}
