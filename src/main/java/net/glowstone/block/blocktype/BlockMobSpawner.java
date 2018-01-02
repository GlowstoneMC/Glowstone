package net.glowstone.block.blocktype;

import net.glowstone.block.entity.BlockEntity;
import net.glowstone.block.entity.MobSpawnerEntity;
import net.glowstone.chunk.GlowChunk;

public class BlockMobSpawner extends BlockDropless {

    @Override
    public BlockEntity createBlockEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new MobSpawnerEntity(chunk.getBlock(cx, cy, cz));
    }
}
