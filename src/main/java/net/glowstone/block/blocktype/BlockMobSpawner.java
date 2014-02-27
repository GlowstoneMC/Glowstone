package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.entity.TEMobSpawner;
import net.glowstone.block.entity.TileEntity;

public class BlockMobSpawner extends BlockType {
    @Override
    public TileEntity createTileEntity(GlowBlock block) {
        return new TEMobSpawner(block);
    }
}
