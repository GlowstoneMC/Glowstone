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

public class BlockStem extends BlockNeedsAttached {
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
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        int amount = random.nextInt(4);
        if (amount == 0) {
            return BlockDropless.EMPTY_STACK;
        }
        return Collections.unmodifiableList(Arrays.asList(new ItemStack(seedsType, amount)));
    }

    @Override
    public boolean canTickRandomly() {
        return true;
    }

    @Override
    public void updateBlock(GlowBlock block) {
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
