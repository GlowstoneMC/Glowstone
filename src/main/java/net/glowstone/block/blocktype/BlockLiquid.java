package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public abstract class BlockLiquid extends BlockType {

    private final Material bucketType;

    private static final BlockFace[] dirNESW = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
    private static final BlockFace[] dirNESWU = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP};
    private static final BlockFace[] dirNESWD = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.DOWN};
    private static final BlockFace[] dirNESWUD = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};

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
    private static final byte STRENGTH_MIN_LAVA = 4;
    private static final int TICK_RATE_WATER = 5;
    private static final int TICK_RATE_LAVA = 20;

    private void calculateFlow(GlowBlock block) {

        GlowBlockState oldState = block.getState();
        GlowBlockState newState = block.getState();
        boolean isWater = isWater(newState.getType());
        List<GlowBlock> updates = new ArrayList<>(6);

        if (isSource(isWater, newState.getRawData())) {
            // We are a source block, let's spread.

            for (BlockFace face : dirNESWD) {
                GlowBlock target = block.getRelative(face);

                // Check mixing liquid types.
                if (target.isLiquid() && !isWater(target.getType()) && isWater) {
                    target.setType(isSource(isWater, target.getData()) ? Material.OBSIDIAN : Material.COBBLESTONE, true);
                    updates.add(target);
                } else if (target.isLiquid() && isWater(target.getType()) && !isWater) {
                    target.setType(face == BlockFace.DOWN ? Material.STONE : Material.COBBLESTONE, true);
                    updates.add(target);
                } else if (target.isLiquid() && target.getData() > STRENGTH_MAX || target.getType().isTransparent()) {
                    // No mixes, just spread normally!
                    target.setType(newState.getType(), STRENGTH_MAX, true);
                    target.getWorld().requestPulse(target, isWater ? TICK_RATE_WATER : TICK_RATE_LAVA);
                }
            }

        } else {
            // We are flowing, let's calculate!

            // Let's check that we can still stand.
            int sourceBlocks = 0;
            boolean sourceAbove = false;
            boolean fluidAbove = false;
            byte strength = isWater ? STRENGTH_MIN_WATER : STRENGTH_MIN_LAVA;
            for (BlockFace face : dirNESWU) {
                GlowBlock target = block.getRelative(face);

                // Check that we are touching liquid.
                if (target.isLiquid() && isWater(target.getType()) == isWater) {

                    // Found sources? Lets score them.
                    if (isSource(isWater, target.getData())) {
                        strength = STRENGTH_MAX;
                        sourceBlocks++;
                        if (face == BlockFace.UP) {
                            sourceAbove = true;
                        }
                    } else {
                        // No source, lets get strength.
                        if (face == BlockFace.UP) {
                            strength = STRENGTH_SOURCE;
                            fluidAbove = true;
                        } else if (target.getData() < strength) {
                            strength = target.getData();
                        }
                    }
                }
            }

            if (isWater && sourceBlocks > (sourceAbove ? 2 : 1)) {
                // We can now become a source.
                newState.setRawData(STRENGTH_SOURCE);
            } else if (sourceBlocks > 0 && newState.getRawData() != STRENGTH_MAX) {
                // We are attached to the source, max strength.
                newState.setRawData(STRENGTH_MAX);
            } else if (sourceBlocks < 1 && strength == (isWater ? STRENGTH_MIN_WATER : STRENGTH_MIN_LAVA)) {
                // Water is now too weak to continue!
                newState.setType(Material.AIR);
            } else if (!fluidAbove && sourceBlocks < 1 && newState.getRawData() != strength + 1) {
                // We should correct our water strength now.
                newState.setRawData((byte) (strength + 1));
            } else {
                // The water stream is stable, let's spread!
                byte newData = (byte) (newState.getRawData() + 1);

                // Start with flowing down, otherwise outwards.
                GlowBlock down = block.getRelative(BlockFace.DOWN);
                // Check mixing liquid types.
                if (down.isLiquid() && !isWater(down.getType()) && isWater) {
                    down.setType(isSource(isWater, down.getData()) ? Material.OBSIDIAN : Material.COBBLESTONE, true);
                    updates.add(down);
                } else if (down.isLiquid() && isWater(down.getType()) && !isWater) {
                    down.setType(Material.STONE, true);
                    updates.add(down);
                } else if (down.isLiquid() && down.getData() > STRENGTH_MAX || down.getType().isTransparent()) {
                    // No mixes, just spread normally!
                    down.setType(newState.getType(), STRENGTH_MAX, true);
                    down.getWorld().requestPulse(down, isWater ? TICK_RATE_WATER : TICK_RATE_LAVA);
                } else if (!down.isLiquid() && newData <= (isWater ? STRENGTH_MIN_WATER : STRENGTH_MIN_LAVA)) { // No downwards? Check outwards.

                    for (BlockFace face : dirNESW) {
                        GlowBlock target = block.getRelative(face);

                        // Check mixing liquid types.
                        if (target.isLiquid() && !isWater(target.getType()) && isWater) {
                            target.setType(isSource(isWater, target.getData()) ? Material.OBSIDIAN : Material.COBBLESTONE, true);
                            updates.add(target);
                        } else if (target.isLiquid() && isWater(target.getType()) && !isWater) {
                            target.setType(Material.COBBLESTONE, true);
                            updates.add(target);
                        } else if (target.isLiquid() && target.getData() > newData || target.getType().isTransparent()) {
                            // No mixes, just spread normally!
                            target.setType(newState.getType(), newData, true);
                            target.getWorld().requestPulse(target, isWater ? TICK_RATE_WATER : TICK_RATE_LAVA);
                        }
                    }
                }
            }
        }

        // Nothing changed? Lets stop pulsing.
        if (oldState.getType() == newState.getType()
                && oldState.getRawData() == newState.getRawData()) {
            newState.setType(getOpposite(oldState.getType()));
            newState.setData(oldState.getData());
            block.getWorld().cancelPulse(block);
        } else {
            for (BlockFace face : dirNESWUD) {
                GlowBlock target = block.getRelative(face);
                if (target.isLiquid()) {
                    block.getWorld().requestPulse(target, isWater ? TICK_RATE_WATER : TICK_RATE_LAVA);
                }
            }
        }

        // Lets update our changes.
        newState.update(true, false);

        // Update any other changes afterwards to force pulses for other sources.
        for (GlowBlock update : updates) {
            update.setType(update.getType());
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
