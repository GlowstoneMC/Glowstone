package net.glowstone.block.blocktype;

import org.bukkit.Material;

import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;

public class BlockMushroom extends BlockType implements IBlockGrowable {

    private final Material mushroomType;

    public BlockMushroom(Material mushroomType) {
        this.mushroomType = mushroomType;
    }

    @Override
    public boolean isFertilizable(GlowBlock block) {
        return true;
    }

    @Override
    public boolean canGrowWithChance(GlowBlock block) {
        return (double) random.nextFloat() < 0.4D;
    }

    @Override
    public void grow(GlowPlayer player, GlowBlock block) {
    }
}
