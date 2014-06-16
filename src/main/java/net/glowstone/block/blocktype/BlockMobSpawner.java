package net.glowstone.block.blocktype;

import net.glowstone.GlowChunk;
import net.glowstone.block.entity.TEMobSpawner;
import net.glowstone.block.entity.TileEntity;

public class BlockMobSpawner extends BlockDropless {
    @Override
    public TileEntity createTileEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new TEMobSpawner(chunk.getBlock(cx, cy, cz));
    }
}
