package net.glowstone.block.blocktype;

import static org.bukkit.block.BlockFace.DOWN;
import static org.bukkit.block.BlockFace.EAST;
import static org.bukkit.block.BlockFace.NORTH;
import static org.bukkit.block.BlockFace.SOUTH;
import static org.bukkit.block.BlockFace.UP;
import static org.bukkit.block.BlockFace.WEST;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.ItemTable;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public abstract class BlockLiquid extends BlockType {

    private static final byte STRENGTH_SOURCE = 0;
    private static final byte STRENGTH_MAX = 1;

    private static final byte STRENGTH_MIN_WATER = 7;
    private static final byte STRENGTH_MIN_LAVA = 4;

    private static final int TICK_RATE_WATER = 4;
    private static final int TICK_RATE_LAVA = 20;
    private final Material bucketType;

    protected BlockLiquid(Material bucketType) {
        this.bucketType = bucketType;
    }

    private static boolean isSource(boolean isWater, byte data) {
        return data < STRENGTH_MAX || data > (isWater ? STRENGTH_MIN_WATER : STRENGTH_MIN_LAVA);
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

    /**
     * Get the bucket type to replace the empty bucket when the liquid has been collected.
     *
     * @return The associated bucket types material
     */
    public Material getBucketType() {
        return bucketType;
    }

    /**
     * Check if the BlockState block is collectible by a bucket.
     *
     * @param block The block state to check
     * @return Boolean representing if its collectible
     */
    public abstract boolean isCollectible(GlowBlockState block);

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
        ItemStack holding, Vector clickedLoc) {
        // 0 = Full liquid block
        state.setType(getMaterial());
        state.setRawData((byte) 0);
        state.getBlock().getWorld().requestPulse(state.getBlock());
    }

    @Override
    public void onNearBlockChanged(GlowBlock block, BlockFace face, GlowBlock changedBlock,
        Material oldType, byte oldData, Material newType, byte newData) {
        if (block.getState().isFlowed() && !(isWater(newType) || newType == Material.LAVA
            || newType == Material.STATIONARY_LAVA)) {
            block.getState().setFlowed(false);
        }
        block.getWorld().requestPulse(block);
    }

    /**
     * Pulse the block to calculate its flow.
     *
     * @param block The block to calculate flow of.
     */
    @Override
    public void receivePulse(GlowBlock block) {
        updatePhysics(block);
    }

    private void calculateFlow(GlowBlock block) {
        if (!block.getState().isFlowed()) {
            GlowBlockState state = block.getState();
            // see if we can flow down
            if (block.getY() > 0) {
                if (calculateTarget(block.getRelative(DOWN), DOWN, true)) {
                    if (!block.getRelative(UP).isLiquid()
                        && Byte.compare(state.getRawData(), STRENGTH_SOURCE) == 0) {
                        for (BlockFace face : SIDES) {
                            calculateTarget(block.getRelative(face), face, true);
                        }
                    }
                } else {
                    // we can't flow down, or if we're a source block, let's flow horizontally
                    // search 5 blocks out
                    for (int j = 1; j < 6; j++) {
                        // from each horizontal face
                        for (BlockFace face : SIDES) {
                            if (calculateTarget(block.getRelative(face, j).getRelative(DOWN), face,
                                false) && calculateTarget(block.getRelative(face), face, true)) {
                                state.setFlowed(true);
                            }
                        }
                        // if we already found a match at this radius, stop
                        if (state.isFlowed()) {
                            return;
                        }
                    }
                    for (BlockFace face : SIDES) {
                        calculateTarget(block.getRelative(face), face, true);
                    }
                    state.setFlowed(true);
                }
            }
        }
    }

    private boolean calculateTarget(GlowBlock target, BlockFace direction, boolean flow) {
        // Don't flow inside unloaded chunks
        if (!target.getChunk().isLoaded()) {
            return false;
        }
        if (target.getType() == Material.AIR || ItemTable.instance()
            .getBlock(target.getType()) instanceof BlockNeedsAttached) {
            // we flowed
            if (flow) {
                flow(target.getRelative(direction.getOppositeFace()), direction);
            }
            return true;
        }
        if (target.isLiquid()) {
            // let's mix
            if (flow) {
                mix(target, direction, target.getRelative(direction.getOppositeFace()).getType(),
                    target.getType());
            }
            return true;
        }
        // it is solid, we can't flow
        return false;
    }

    private void flow(GlowBlock source, BlockFace direction) {
        // if we're not going down
        BlockFromToEvent fromToEvent = new BlockFromToEvent(source, direction);
        if (fromToEvent.isCancelled()) {
            return;
        }
        byte strength = fromToEvent.getBlock().getState().getRawData();
        if (DOWN != fromToEvent.getFace()) {
            if (Byte.compare(strength, isWater(fromToEvent.getBlock().getType())
                || fromToEvent.getBlock().getBiome() == Biome.HELL ? STRENGTH_MIN_WATER
                : STRENGTH_MIN_LAVA) < 0) {
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
        GlowBlock toBlock = (GlowBlock) fromToEvent.getToBlock();
        toBlock.setType(fromToEvent.getBlock().getType(), strength, false);
        toBlock.getWorld().requestPulse(toBlock);
    }

    private void mix(GlowBlock target, BlockFace direction, Material flowingMaterial,
        Material targetMaterial) {
        if (flowingMaterial == Material.WATER && targetMaterial == Material.LAVA) {
            if (target.getState().getRawData() == STRENGTH_SOURCE) {
                target.setType(Material.OBSIDIAN);
            } else if (direction == DOWN) {
                target.setType(Material.COBBLESTONE);
            }
        }
        if (flowingMaterial == Material.LAVA && (targetMaterial == Material.WATER
            || targetMaterial == Material.STATIONARY_WATER)) {
            if (direction == DOWN) {
                target.setType(Material.STONE);
            }
            if (direction == NORTH || direction == SOUTH || direction == EAST
                || direction == WEST) {
                target.setType(Material.COBBLESTONE);
            }
        }
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
                    if (count < 2 && face != UP
                        && Byte.compare(block.getRelative(face).getState().getRawData(),
                            STRENGTH_SOURCE) == 0) {
                        count++;
                    }
                    if (!connected && face == UP
                        || Byte.compare(block.getRelative(face).getState().getRawData(),
                            block.getState().getRawData()) < 0) {
                        connected = true;
                        if (block.getWorld().getServer().getClassicWater()) {
                            block.getState().setRawData(STRENGTH_SOURCE);
                        }
                    }
                    if (block.getWorld().getServer().getClassicWater()
                        && Byte.compare(block.getRelative(face).getState().getRawData(),
                            STRENGTH_SOURCE) == 0) {
                        block.getRelative(face).setType(Material.AIR);
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
        if (!(Byte.compare(block.getState().getRawData(),
            isWater(block.getType()) || block.getBiome() == Biome.HELL ? STRENGTH_MIN_WATER
                : STRENGTH_MIN_LAVA) == 0) || block.getRelative(DOWN).getType() == Material.AIR) {
            calculateFlow(block);
        }
    }

    @Override
    public boolean isPulseOnce(GlowBlock block) {
        return true;
    }

    @Override
    public int getPulseTickSpeed(GlowBlock block) {
        return isWater(block.getType()) || block.getBiome() == Biome.HELL ? TICK_RATE_WATER
            : TICK_RATE_LAVA;
    }
}
