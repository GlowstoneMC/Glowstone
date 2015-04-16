package net.glowstone.block.blocktype;

import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockGrowEvent;

public class BlockCactus extends BlockType {

    private static final BlockFace[] NEAR_BLOCKS = new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST};

    @Override
    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        Material below = block.getRelative(BlockFace.DOWN).getType();
        return (below == Material.CACTUS || below == Material.SAND) && !hasNearBlocks(block);
    }

    @Override
    public void onNearBlockChanged(GlowBlock block, BlockFace face, GlowBlock changedBlock, Material oldType, byte oldData, Material newType, byte newData) {
        updatePhysics(block);
    }

    @Override
    public void updatePhysics(GlowBlock me) {
        if (!canPlaceAt(me, BlockFace.DOWN)) {
            me.breakNaturally();
        }
    }

    @Override
    public boolean canTickRandomly() {
        return true;
    }

    @Override
    public void updateBlock(GlowBlock block) {
        final GlowBlock blockAbove = block.getRelative(BlockFace.UP);
        // check it's the highest block of cactus
        if (blockAbove.isEmpty()) {
            // check the current cactus height
            Block blockBelow = block.getRelative(BlockFace.DOWN);
            int height = 1;
            while (blockBelow.getType() == Material.CACTUS) {
                height++;
                blockBelow = blockBelow.getRelative(BlockFace.DOWN);
            }
            if (height < 3) {
                GlowBlockState state = block.getState();
                if (state.getRawData() < 15) {
                    // increase age
                    state.setRawData((byte) (state.getRawData() + 1));
                    state.update(true);
                } else {
                    // grow the cactus on the above block
                    state.setRawData((byte) 0);
                    state.update(true);
                    state = blockAbove.getState();
                    state.setType(Material.CACTUS);
                    state.setRawData((byte) 0);
                    BlockGrowEvent growEvent = new BlockGrowEvent(blockAbove, state);
                    EventFactory.callEvent(growEvent);
                    if (!growEvent.isCancelled()) {
                        state.update(true);
                    }
                    updatePhysics(blockAbove);
                }
            }
        }
    }

    private boolean hasNearBlocks(GlowBlock block) {
        for (BlockFace face : NEAR_BLOCKS) {
            if (!canPlaceNear(block.getRelative(face).getType())) {
                return true;
            }
        }
        return false;
    }

    private boolean canPlaceNear(Material type) {
        // TODO: return true for non-buildable blocks
        switch (type) {
            case AIR:
            case WATER:
            case STATIONARY_WATER:
            case LAVA:
            case STATIONARY_LAVA:
            case PORTAL:
            case ENDER_PORTAL:
            case RED_ROSE:
            case YELLOW_FLOWER:
                return true;
            default:
                return false;
        }
    }
}
