package net.glowstone.block.blocktype;

import java.util.Random;

import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;

public class BlockSapling extends BlockPlant implements IBlockGrowable {

    private final Random random = new Random();

    @Override
    public boolean isFertilizable(GlowBlock block) {
        return true;
    }

    @Override
    public boolean canGrowWithChance(GlowBlock block) {
        return (double) random.nextFloat() < 0.45D;
    }

    @Override
    public void grow(GlowPlayer player, GlowBlock block) {
    }
}
