package net.glowstone.block.blocktype;

import lombok.Getter;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.ItemTable;
import net.glowstone.block.data.Waterlogged;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import static org.bukkit.block.BlockFace.*;

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

    private static boolean isWaterlogged(GlowBlock block) {
        return block.getBlockData() instanceof Waterlogged
            && ((Waterlogged) block.getBlockData()).isWaterlogged();
    }

    // These account for waterlogged blocks:

    private static boolean isLiquid(GlowBlock block) {
        return isWaterlogged(block) || block.isLiquid();
    }

    private static Material getType(GlowBlock block) {
        return isWaterlogged(block) ? Material.WATER : block.getType();
    }

    private static byte getStrength(GlowBlock block) {
        return isWaterlogged(block) ? STRENGTH_SOURCE : block.getState().getRawData();
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
            if (!isLiquid(block.getRelative(UP))
                && getStrength(block) == STRENGTH_SOURCE) {
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
        if (getType(target.getRelative(direction.getOppositeFace())) == Material.WATER
            && target.getBlockData() instanceof Waterlogged) {
            // Fill the block with water, if waterloggable
            if (flow) {
                soak(target, direction);
            }
            return true;
        }
        if (isLiquid(target)) {
            // let's mix
            if (flow) {
                mix(target, direction, getType(target.getRelative(direction.getOppositeFace())),
                    getType(target));
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
        GlowBlock block = (GlowBlock) fromToEvent.getBlock();
        byte strength = getStrength(block);
        if (DOWN != fromToEvent.getFace()) {
            if (strength < (isWater(getType(block))
                    || fromToEvent.getBlock().getBiome() == Biome.NETHER ? STRENGTH_MIN_WATER
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
        // doesn't need to take waterlogging into account, because it will never flow into a waterloggable block
        // (because waterlogged blocks always "have" water source blocks in them)
        GlowBlock toBlock = (GlowBlock) fromToEvent.getToBlock();
        toBlock.setType(fromToEvent.getBlock().getType(), strength, false);
        toBlock.getWorld().requestPulse(toBlock);
    }

    private void soak(GlowBlock target, BlockFace direction) {
        if (getStrength(target.getRelative(direction.getOppositeFace())) == STRENGTH_MIN_WATER) {
            // no strength, can't waterlog
            return;
        }
        boolean source = false; // is a water source block
        BlockFace[] faces = {UP, NORTH, EAST, SOUTH, WEST};
        int count = 0;
        for (BlockFace face : faces) {
            if (isWater(getType(target.getRelative(face)))) {
                if (count < 2 && face != UP
                    && getStrength(target.getRelative(face)) == STRENGTH_SOURCE) {
                    count++;
                }
                if (target.getWorld().getServer().getClassicWater()
                    && (face == UP || getStrength(target.getRelative(face)) < getStrength(target))) {
                    source = true;
                    break;
                }
            }
        }
        source = source || count == 2;  // found 2 adjacent source blocks
        if (source) {  // only waterlog with source strength
            ((Waterlogged) target.getBlockData()).setWaterlogged(true);
        }
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
        if (isStationary(getType(me))) {
            // TODO: set flowing
            //me.setType(getOpposite(me.getType()), me.getData(), false);
        }
        boolean isWater = isWater(getType(me));
        byte strength = getStrength(me);
        if (strength != STRENGTH_SOURCE) {
            BlockFace[] faces = {UP, NORTH, EAST, SOUTH, WEST};
            boolean connected = false;
            int count = 0;
            for (BlockFace face : faces) {
                if (getType(me.getRelative(face)) != getType(me)) {
                    continue;
                }
                byte neighborStrength = getStrength(me.getRelative(face));
                if (count < 2 && face != UP
                    && (isWater && neighborStrength == STRENGTH_SOURCE)) {
                    count++;
                }
                if (!connected && face == UP || neighborStrength < strength) {
                    connected = true;
                    if (me.getWorld().getServer().getClassicWater()) {
                        me.getState().setRawData(STRENGTH_SOURCE);
                    }
                }
                if (me.getWorld().getServer().getClassicWater() && neighborStrength == STRENGTH_SOURCE) {
                    me.getRelative(face).setType(Material.AIR);
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
        if (getStrength(me)
                != (isWater || me.getBiome() == Biome.NETHER ? STRENGTH_MIN_WATER
                        : STRENGTH_MIN_LAVA) || me.getRelative(DOWN).getType() == Material.AIR) {
            calculateFlow(me);
        }
    }

    @Override
    public boolean isPulseOnce(GlowBlock block) {
        return true;
    }

    @Override
    public int getPulseTickSpeed(GlowBlock block) {
        return isWater(getType(block)) || block.getBiome() == Biome.NETHER ? TICK_RATE_WATER
            : TICK_RATE_LAVA;
    }
}
