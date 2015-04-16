package net.glowstone.block.blocktype;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Pumpkin;

import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;

public class BlockStem extends BlockCrops implements IBlockGrowable {
    private Material fruitType;
    private Material seedsType;

    public BlockStem(Material plantType) {
        if (plantType == Material.MELON_STEM) {
            fruitType = Material.MELON_BLOCK;
            seedsType = Material.MELON_SEEDS;
        } else if (plantType == Material.PUMPKIN_STEM) {
            fruitType = Material.PUMPKIN;
            seedsType = Material.PUMPKIN_SEEDS;
        }
    }

    @Override
    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        if (block.getRelative(BlockFace.DOWN).getType() == Material.SOIL) {
            return true;
        }
        return false;
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        if (block.getState().getRawData() >= CropState.RIPE.ordinal()) {
            return Collections.unmodifiableList(Arrays.asList(new ItemStack(seedsType, random.nextInt(4))));
        } else {
            return BlockDropless.EMPTY_STACK;
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

    @Override
    public void updateBlock(GlowBlock block) {
        // we check light level on the above block, meaning stems needs at least one free block above it
        // in order to grow naturally (vanilla behavior)
        if (block.getRelative(BlockFace.UP).getLightLevel() >= 9 &&
                random.nextInt((int) (25.0F / getGrowthRateModifier(block)) + 1) == 0) {

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
                int n = random.nextInt(4);
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
                final GlowBlock targetBlock = block.getRelative(face);
                final GlowBlockState targetBlockState = targetBlock.getState();
                final GlowBlock belowTargetBlock = targetBlock.getRelative(BlockFace.DOWN);
                if (targetBlock.getType() == Material.AIR
                        && (belowTargetBlock.getType() == Material.SOIL
                        || belowTargetBlock.getType() == Material.DIRT
                        || belowTargetBlock.getType() == Material.GRASS)) {
                    targetBlockState.setType(fruitType);
                    if (fruitType == Material.PUMPKIN) {
                        targetBlockState.setData(new Pumpkin(face.getOppositeFace()));
                    }
                    targetBlockState.update(true);
                }
            } else {
                cropState++;
                final GlowBlockState state = block.getState();
                state.setRawData((byte) cropState);
                BlockGrowEvent growEvent = new BlockGrowEvent(block, state);
                EventFactory.callEvent(growEvent);
                if (!growEvent.isCancelled()) {
                    state.update(true);
                }
            }
        }
    }
}
