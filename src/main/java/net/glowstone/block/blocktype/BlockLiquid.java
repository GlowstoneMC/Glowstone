package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.ItemTable;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.bukkit.block.BlockFace.*;

public abstract class BlockLiquid extends BlockType {

    private final Material bucketType;

    protected BlockLiquid(Material bucketType) {
        this.bucketType = bucketType;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Public accessors

    /**
     * Get the bucket type to replace the empty bucket when the liquid has
     * been collected.
     * @return The associated bucket types material
     */
    public Material getBucketType() {
        return bucketType;
    }

    /**
     * Check if the BlockState block is collectible by a bucket.
     * @param block The block state to check
     * @return Boolean representing if its collectible
     */
    public abstract boolean isCollectible(GlowBlockState block);

    ////////////////////////////////////////////////////////////////////////////
    // Overrides

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        // 0 = Full liquid block
        state.setType(getMaterial());
    
        state.setRawData((byte) 0);
        updatePhysics(state.getBlock());
    }

    @Override
    public void onNearBlockChanged(GlowBlock block, BlockFace face, GlowBlock changedBlock, Material oldType, byte oldData, Material newType, byte newData) {
        if (!(isWater(newType) || newType == Material.LAVA || newType == Material.STATIONARY_LAVA)) {
            block.getState().setFlowed(false);
        }
        updatePhysics(block);
    }

    /**
     * Pulse the block to calculate its flow.
     * @param block The block to calculate flow of.
     */
    @Override
    public void receivePulse(GlowBlock block) {
        calculateFlow(block);
    }

    private static final byte STRENGTH_SOURCE = 0;
    private static final byte STRENGTH_MAX = 1;
    private static final byte STRENGTH_MIN_WATER = 7;
    private static final byte STRENGTH_MIN_LAVA = 3;
    private static final int TICK_RATE_WATER = 4;
    private static final int TICK_RATE_LAVA = 20;
    private BlockFace[] hfaces = {NORTH, EAST, SOUTH, WEST};
    CountDownLatch latch = new CountDownLatch(4);

    private void calculateFlow(GlowBlock block) {
        GlowBlockState state = block.getState();
        // see if we can flow down
        if (!calculateTarget(block.getRelative(DOWN), DOWN, block.getType(), state.getRawData(), true)) {
            // we can't flow down, let's flow horizontally
            // search 5 blocks out
            for (int j = 1; j <= 5; j++) {
                // from each horizontal face
                for (BlockFace face : hfaces) {
                    int m = j;
                    switch (face) {
                        case NORTH:
                        case WEST:
                            m *= -1;
                            break;
                    }
                    new BlockLiquidSearchThread(face, block, m).start();
                }
                try {
                    latch.await(1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // if we already found a match at this radius, stop
                if (state.getFlowed()) {
                    return;
                }
            }
            for (BlockFace face : hfaces) {
                calculateTarget(block.getRelative(face), face, block.getType(), state.getRawData(), true);
            }
            state.setFlowed(true);
        }
    }

    public class BlockLiquidSearchThread extends Thread {

        BlockFace face;
        GlowBlock block;
        GlowBlockState state;
        int m;

        public BlockLiquidSearchThread(BlockFace face, GlowBlock block, int m) {
            this.face = face;
            this.block = block;
            state = this.block.getState();
            this.m = m;
        }

        public void run() {
            if (calculateTarget(block.getWorld().getBlockAt(block.getX() + (face == EAST || face == WEST ? m : 0), block.getY() - 1, block.getZ() + (face == NORTH || face == SOUTH ? m : 0)), face, block.getType(), state.getRawData(), false)) {
                if (calculateTarget(block.getRelative(face), face, block.getType(), state.getRawData(), true) && !state.getFlowed()) {
                    state.setFlowed(true);
                }
            }
            latch.countDown();
        }
    }

    private boolean calculateTarget(GlowBlock target, BlockFace direction, Material type, byte strength, boolean flow) {
        if (target.getType() == Material.AIR || ItemTable.instance().getBlock(target.getType()) instanceof BlockNeedsAttached) {
            // we flowed
            if (flow) {
                flow(target, direction, type, strength);
            }
            return true;
        } else if (target.isLiquid()) {
            // let's mix
            if (flow) {
                mix(target, direction, type, target.getType());
            }
            return true;
        }
        // it is solid, we can't flow
        return false;
    }

    private void flow(GlowBlock target, BlockFace direction, Material type, byte strength) {
        // if we're not going down
        if (DOWN != direction) {
            if (Byte.compare(strength, isWater(type) ? STRENGTH_MIN_WATER : STRENGTH_MIN_LAVA) < 0) {
                // decrease the strength
                strength += 1;
            } else {
                // no strength, can't flow
                return;
            }
        } else {
            // reset the strength if we're going down
            strength = STRENGTH_MAX;
        }
        // flow to the target
        target.setType(type, strength, true);
        target.getWorld().requestPulse(target, isWater(target.getType()) ? TICK_RATE_WATER : TICK_RATE_LAVA);
    }

    private void mix(GlowBlock target, BlockFace direction, Material flowingMaterial, Material targetMaterial) {
        if (flowingMaterial == Material.WATER && targetMaterial == Material.LAVA) {
            if (target.getState().getRawData() == STRENGTH_SOURCE) {
                target.setType(Material.OBSIDIAN);
            } else if (direction == DOWN) {
                target.setType(Material.COBBLESTONE);
            }
        }
        if (flowingMaterial == Material.LAVA && (targetMaterial == Material.WATER || targetMaterial == Material.STATIONARY_WATER)) {
            if (direction == DOWN) {
                target.setType(Material.STONE);
            }
            if (direction == NORTH || direction == SOUTH || direction == EAST || direction == WEST) {
                target.setType(Material.COBBLESTONE);
            }
        }
    }

    private static boolean isSource(boolean isWater, byte data) {
        return data < STRENGTH_MAX || data > (isWater ? STRENGTH_MIN_WATER : STRENGTH_MIN_LAVA);
    }

    @Override
    public void updatePhysics(GlowBlock block) {
        if (isStationary(block.getType())) {
            block.setType(getOpposite(block.getType()), block.getData(), false);
        }
        if (Byte.compare(block.getState().getRawData(), STRENGTH_SOURCE) != 0) {
            BlockFace[] faces = {UP, NORTH, EAST, SOUTH, WEST};
            boolean connected = false;
            int count = 0;
            for (BlockFace face : faces) {
                if (block.getRelative(face).getType() == block.getType()) {
                    if (count < 2 && face != UP && Byte.compare(block.getRelative(face).getState().getRawData(), STRENGTH_SOURCE) == 0) {
                        count++;
                    }
                    if (!connected && face == UP || Byte.compare(block.getRelative(face).getState().getRawData(), block.getState().getRawData()) < 0) {
                        connected = true;
                    }
                }
            }
            if (!connected) {
                block.setType(Material.AIR);
                return;
            }
            if (count == 2) {
                block.getState().setRawData(STRENGTH_SOURCE);
                return;
            }
        }
        block.getWorld().requestPulse(block, isWater(block.getType()) ? TICK_RATE_WATER : TICK_RATE_LAVA);
    }
    
    @Override
    public void updateBlock(GlowBlock block) {
        updatePhysics(block);
    }
    
    @Override
    public boolean canTickRandomly() {
        return true;
    }

    private static boolean isStationary(Material material) {
        switch (material) {
            case STATIONARY_WATER:
            case STATIONARY_LAVA:
                return true;
            default:
                return false;
        }
    }

    private static boolean isWater(Material material) {
        switch (material) {
            case STATIONARY_WATER:
            case WATER:
                return true;
            default:
                return false;
        }
    }

    private static Material getOpposite(Material material) {
        switch (material) {
            case STATIONARY_WATER:
                return Material.WATER;
            case STATIONARY_LAVA:
                return Material.LAVA;
            case WATER:
                return Material.STATIONARY_WATER;
            case LAVA:
                return Material.STATIONARY_LAVA;
            default:
                return Material.AIR;
        }
    }

}
