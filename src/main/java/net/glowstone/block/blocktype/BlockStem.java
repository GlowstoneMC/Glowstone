package net.glowstone.block.blocktype;

import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Pumpkin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

public class BlockStem extends BlockCrops {

    private Material fruitType;
    private Material seedsType;

    /**
     * Creates a block type for a stem whose fruit is horizontally adjacent (pumpkin or melon).
     *
     * @param plantType the plant type.
     */
    public BlockStem(Material plantType) {
        if (plantType == Material.MELON_STEM) {
            fruitType = Material.MELON;
            seedsType = Material.MELON_SEEDS;
        } else if (plantType == Material.PUMPKIN_STEM) {
            fruitType = Material.PUMPKIN;
            seedsType = Material.PUMPKIN_SEEDS;
        }
    }

    @Override
    public boolean canPlaceAt(GlowPlayer player, GlowBlock block, BlockFace against) {
        return block.getRelative(BlockFace.DOWN).getType() == Material.FARMLAND;
    }

    @NotNull
    @Override
    public Collection<ItemStack> getDrops(@NotNull GlowBlock block, ItemStack tool) {
        if (block.getState().getRawData() >= CropState.RIPE.ordinal()) {
            return Collections.unmodifiableList(
                Arrays.asList(new ItemStack(seedsType, ThreadLocalRandom.current().nextInt(4))));
        } else {
            return Collections.emptyList();
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
    public boolean canTickRandomly() {
        return true;
    }

    @Override
    public void grow(GlowPlayer player, GlowBlock block) {
        GlowBlockState state = block.getState();
        int cropState = block.getData()
            + ThreadLocalRandom.current().nextInt(CropState.MEDIUM.ordinal())
            + CropState.VERY_SMALL.ordinal();
        if (cropState > CropState.RIPE.ordinal()) {
            cropState = CropState.RIPE.ordinal();
        }
        state.setRawData((byte) cropState);
        BlockGrowEvent growEvent = new BlockGrowEvent(block, state);
        EventFactory.getInstance().callEvent(growEvent);
        if (!growEvent.isCancelled()) {
            state.update(true);
        }
    }

    @Override
    public void updateBlock(GlowBlock block) {
        // we check light level on the above block, meaning a stem needs at least one free block
        // above it in order to grow naturally (vanilla behavior)
        if (block.getRelative(BlockFace.UP).getLightLevel() >= 9
            && ThreadLocalRandom.current().nextInt(
            (int) (25.0F / getGrowthRateModifier(block)) + 1)
            == 0) {

            int cropState = block.getData();
            if (cropState >= CropState.RIPE.ordinal()) {
                // check around there's not already a fruit
                if (block.getRelative(BlockFace.EAST).getType() == fruitType
                    || block.getRelative(BlockFace.WEST).getType() == fruitType
                    || block.getRelative(BlockFace.NORTH).getType() == fruitType
                    || block.getRelative(BlockFace.SOUTH).getType() == fruitType) {
                    return;
                }
                // produce a fruit if possible
                int n = ThreadLocalRandom.current().nextInt(4);
                BlockFace face;
                switch (n) {
                    case 1:
                        face = BlockFace.WEST;
                        break;
                    case 2:
                        face = BlockFace.NORTH;
                        break;
                    case 3:
                        face = BlockFace.SOUTH;
                        break;
                    default:
                        face = BlockFace.EAST;
                }
                GlowBlock targetBlock = block.getRelative(face);
                GlowBlockState targetBlockState = targetBlock.getState();
                GlowBlock belowTargetBlock = targetBlock.getRelative(BlockFace.DOWN);
                if (targetBlock.getType() == Material.AIR
                    && (belowTargetBlock.getType() == Material.FARMLAND
                    || belowTargetBlock.getType() == Material.DIRT
                    || belowTargetBlock.getType() == Material.GRASS_BLOCK)) {
                    targetBlockState.setType(fruitType);
                    if (fruitType == Material.PUMPKIN) {
                        targetBlockState.setData(new Pumpkin(face.getOppositeFace()));
                    }
                    targetBlockState.update(true);
                }
            } else {
                cropState++;
                GlowBlockState state = block.getState();
                state.setRawData((byte) cropState);
                BlockGrowEvent growEvent = new BlockGrowEvent(block, state);
                EventFactory.getInstance().callEvent(growEvent);
                if (!growEvent.isCancelled()) {
                    state.update(true);
                }
            }
        }

        // we check for insufficient light on the block itself, then drop
        if (block.getLightLevel() < 8) {
            block.breakNaturally();
        }
    }
}
