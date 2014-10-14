package net.glowstone.block.blocktype;

import java.util.Random;

import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.event.block.BlockGrowEvent;

import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;

public class BlockStem extends BlockPlant implements IBlockGrowable {
    private Material fruitType;
    private Material seedsType;
    private final Random random = new Random();

    public BlockStem(Material plantType) {
        if (plantType.equals(Material.MELON_STEM)) {
            fruitType = Material.MELON_BLOCK;
            seedsType = Material.MELON_SEEDS;
        } else if (plantType.equals(Material.PUMPKIN_STEM)) {
            fruitType = Material.PUMPKIN;
            seedsType = Material.PUMPKIN_SEEDS;
        }
    }

    @Override
    public boolean isFertilizable(GlowBlock block) {
        return block.getData() != CropState.RIPE.ordinal();
    }

    @Override
    public boolean canGrowWithChance(GlowBlock block) {
        return true;
    }

    @Override
    public void grow(GlowPlayer player, GlowBlock block) {
        final GlowBlockState state = block.getState();
        int cropState = block.getData()
            + (random.nextInt(CropState.MEDIUM.ordinal())
            + CropState.VERY_SMALL.ordinal());
        if (cropState > CropState.RIPE.ordinal()) {
            cropState = CropState.RIPE.ordinal();
        }
        state.setRawData((byte) cropState);
        BlockGrowEvent growEvent = new BlockGrowEvent(block, state);
        EventFactory.callEvent(growEvent);
        if (!growEvent.isCancelled()) {
            state.update(true);
        }
    }
}
