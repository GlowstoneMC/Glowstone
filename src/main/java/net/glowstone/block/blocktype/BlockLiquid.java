package net.glowstone.block.blocktype;

import lombok.Getter;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.ItemTable;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import static org.bukkit.block.BlockFace.DOWN;
import static org.bukkit.block.BlockFace.EAST;
import static org.bukkit.block.BlockFace.NORTH;
import static org.bukkit.block.BlockFace.SOUTH;
import static org.bukkit.block.BlockFace.UP;
import static org.bukkit.block.BlockFace.WEST;

// TODO: 1.13: water behavior changed
public abstract class BlockLiquid extends BlockType {

    private static final byte STRENGTH_SOURCE = 0;
    private static final byte STRENGTH_MAX = 1;

    private static final byte STRENGTH_MIN_WATER = 7;
    private static final byte STRENGTH_MIN_LAVA = 4;

    private static final int TICK_RATE_WATER = 4;
    private static final int TICK_RATE_LAVA = 20;
    /**
     * Get the bucket type to replace the empty bucket when the liquid has been collected.
     *
     * @return The associated bucket types material
     */
    @Getter
    private final Material bucketType;

    protected BlockLiquid(Material bucketType) {
        this.bucketType = bucketType;
    }

    private static boolean isSource(boolean isWater, byte data) {
        return data < STRENGTH_MAX || data > (isWater ? STRENGTH_MIN_WATER : STRENGTH_MIN_LAVA);
    }

    private static boolean isStationary(Material material) {
        switch (material) {
            case WATER:
            case LAVA:
                return true;
            default:
                return false;
        }
    }

    private static boolean isWater(Material material) {
        return material == Material.WATER;
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
        if (block.getState().isFlowed() && !(isWater(newType) || newType == Material.LAVA)) {
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
        // see if we can flow down
        if (block.getState().isFlowed() || block.getY() <= 0) {
            return;
        }
        GlowBlockState state = block.getState();
        if (calculateTarget(block.getRelative(DOWN), DOWN, true)) {
            if (!block.getRelative(UP).isLiquid()
                && state.getRawData() == STRENGTH_SOURCE) {
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
            if (strength < (isWater(fromToEvent.getBlock().getType())
                    || fromToEvent.getBlock().getWorld().getEnvironment() == World.Environment.NETHER ? STRENGTH_MIN_WATER
                    : STRENGTH_MIN_LAVA)) {
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
        if (flowingMaterial == Material.LAVA && (targetMaterial == Material.WATER)) {
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
    public void updatePhysicsAfterEvent(GlowBlock me) {
        super.updatePhysicsAfterEvent(me);
        if (isStationary(me.getType())) {
            // TODO: set flowing
            //me.setType(getOpposite(me.getType()), me.getData(), false);
        }
        boolean isWater = isWater(me.getType());
        if (me.getState().getRawData() != STRENGTH_SOURCE) {
            BlockFace[] faces = {UP, NORTH, EAST, SOUTH, WEST};
            boolean connected = false;
            int count = 0;
            for (BlockFace face : faces) {
                if (me.getRelative(face).getType() == me.getType()) {
                    if (isWater && count < 2 && face != UP
                        && me.getRelative(face).getState().getRawData() == STRENGTH_SOURCE) {
                        count++;
                    }
                    if (!connected && face == UP
                        || me.getRelative(face).getState().getRawData()
                            < me.getState().getRawData()) {
                        connected = true;
                        if (me.getWorld().getServer().getClassicWater()) {
                            me.getState().setRawData(STRENGTH_SOURCE);
                        }
                    }
                    if (me.getWorld().getServer().getClassicWater()
                        && me.getRelative(face).getState().getRawData() == STRENGTH_SOURCE) {
                        me.getRelative(face).setType(Material.AIR);
                    }
                }
            }
            if (!connected) {
                me.setType(Material.AIR);
                return;
            }
            if (count == 2) {
                me.getState().setRawData(STRENGTH_SOURCE);
                return;
            }
        }
        if (!(me.getState().getRawData()
                == (isWater || me.getWorld().getEnvironment() == World.Environment.NETHER ? STRENGTH_MIN_WATER
                        : STRENGTH_MIN_LAVA)) || me.getRelative(DOWN).getType() == Material.AIR) {
            calculateFlow(me);
        }
    }

    @Override
    public boolean isPulseOnce(GlowBlock block) {
        return true;
    }

    @Override
    public int getPulseTickSpeed(GlowBlock block) {
        return isWater(block.getType()) || block.getWorld().getEnvironment() == World.Environment.NETHER ? TICK_RATE_WATER
            : TICK_RATE_LAVA;
    }
}
