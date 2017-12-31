package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.entity.BlockEntity;
import net.glowstone.block.entity.MobSpawnerEntity;

public class BlockMobSpawner extends BlockDropless {

    @Override
    public BlockEntity createBlockEntity(GlowBlock block) {
        return new MobSpawnerEntity(block);
    }
}
