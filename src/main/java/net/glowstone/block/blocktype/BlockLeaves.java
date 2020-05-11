package net.glowstone.block.blocktype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.glowstone.EventFactory;
import net.glowstone.GlowWorld;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class BlockLeaves extends BlockType {
    
    private static final int LEAVE_BLOCK_DECAY_RANGE = 6;
    private static final int LEAVE_BLOCK_MAP_RANGE = (LEAVE_BLOCK_DECAY_RANGE * 2) + 1;
    
    //map of neighbouring blocks within decay range, center is current leave block, includes blocks bordering max range
    private final byte[] blockMap = new byte[Math.pow(LEAVE_BLOCK_MAP_RANGE + 2, 3)];

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
        ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);
        state.setRawData((byte) (state.getRawData() | 0x04));
    }

    @NotNull
    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        int data = block.getData() & 0x03; //ignore "non-decay" and "check-decay" data.

        if (tool != null && tool.getType() == Material.SHEARS) {
            return Collections.unmodifiableList(Arrays.asList(
                new ItemStack(block.getType(), 1, (short) data)
            ));
        }

        // TODO: 1.13 leaves types
        if (block.getType() == Material.LEGACY_LEAVES_2) {
            data += 4;
        }

        List<ItemStack> drops = new ArrayList<>();

        if (ThreadLocalRandom.current().nextFloat() < (block.getData() == 3 ? .025f
            : .05f)) { //jungle leaves drop with 2.5% chance, others drop with 5%
            // TODO: 1.13 sapling types
            drops.add(new ItemStack(Material.LEGACY_SAPLING, 1, (short) data));
        }
        if (data == 0 && ThreadLocalRandom.current().nextFloat()
            < .005) { //oak leaves have a .5% chance to drop an apple
            drops.add(new ItemStack(Material.APPLE));
        }
        return Collections.unmodifiableList(drops);
    }

    @Override
    public boolean canTickRandomly() {
        return true;
    }

    @Override
    public void blockDestroy(GlowPlayer player, GlowBlock block, BlockFace face) {
        // vanilla set decay check in a 3x3x3 neighboring when a leaves block is removed
        GlowWorld world = block.getWorld();
        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                for (int y = 0; y < 3; y++) {
                    GlowBlock b = world.getBlockAt(block.getLocation().add(x - 1, y - 1, z - 1));
                    // TODO: 1.13 leaves
                    if (b.getType() == Material.LEGACY_LEAVES || b.getType() == Material.LEGACY_LEAVES_2) {
                        GlowBlockState state = b.getState();
                        if ((state.getRawData() & 0x08) == 0 && (state.getRawData() & 0x04)
                            == 0) { // check decay is off and decay is on
                            // set decay check on for this leaves block
                            state.setRawData((byte) (state.getRawData() | 0x08));
                            state.update(true);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void updateBlock(GlowBlock block) {
        GlowBlockState state = block.getState();
        // check decay flags
        if ((state.getRawData() & 0x08) == 0
                || (state.getRawData() & 0x04) != 0) {
            return;
        }
        GlowWorld world = block.getWorld();

        //build a 3D [decay range] radius box around the current block to map neighboring blocks
        for (int x = 0; x < LEAVE_BLOCK_MAP_RANGE; x++) {
            for (int z = 0; z < LEAVE_BLOCK_MAP_RANGE; z++) {
                for (int y = 0; y < LEAVE_BLOCK_MAP_RANGE; y++) {
                    GlowBlock b = world
                        .getBlockAt(block.getLocation().add(x - LEAVE_BLOCK_DECAY_RANGE, y - LEAVE_BLOCK_DECAY_RANGE, z - LEAVE_BLOCK_DECAY_RANGE));
                    byte val = 127;
                    // TODO: 1.13 leaves and log types
                    if (b.getType() == Material.LEGACY_LOG || b.getType() == Material.LEGACY_LOG_2) {
                        val = 0;
                    } else if (b.getType() == Material.LEGACY_LEAVES
                        || b.getType() == Material.LEGACY_LEAVES_2) {
                        val = -1;
                    }
                    setBlockInMap(val, x, y, z);
                }
            }
        }

        // browse the map in several passes to detect connected leaves:
        // leaf block that is 7 blocks away from log or without connection
        // to another connected leaves block will decay
        for (int i = 0; i < LEAVE_BLOCK_DECAY_RANGE; i++) {
            for (int x = 0; x < LEAVE_BLOCK_MAP_RANGE; x++) {
                for (int z = 0; z < LEAVE_BLOCK_MAP_RANGE; z++) {
                    for (int y = 0; y < LEAVE_BLOCK_MAP_RANGE; y++) {
                        if (getBlockInMap(x, y, z) != i) {
                            continue;
                        }
                        if (getBlockInMap(x - 1, y, z) == -1) {
                            setBlockInMap((byte) (i + 1), x - 1, y, z);
                        }
                        if (getBlockInMap(x, y - 1, z) == -1) {
                            setBlockInMap((byte) (i + 1), x, y - 1, z);
                        }
                        if (getBlockInMap(x, y, z - 1) == -1) {
                            setBlockInMap((byte) (i + 1), x, y, z - 1);
                        }
                        if (getBlockInMap(x + 1, y, z) == -1) {
                            setBlockInMap((byte) (i + 1), x + 1, y, z);
                        }
                        if (getBlockInMap(x, y + 1, z) == -1) {
                            setBlockInMap((byte) (i + 1), x, y + 1, z);
                        }
                        if (getBlockInMap(x, y, z + 1) == -1) {
                            setBlockInMap((byte) (i + 1), x, y, z + 1);
                        }
                    }
                }
            }
        }

        if (getBlockInMap(LEAVE_BLOCK_DECAY_RANGE, LEAVE_BLOCK_DECAY_RANGE, LEAVE_BLOCK_DECAY_RANGE) < 0) { // leaf decay
            LeavesDecayEvent decayEvent = new LeavesDecayEvent(block);
            EventFactory.getInstance().callEvent(decayEvent);
            if (!decayEvent.isCancelled()) {
                block.breakNaturally();
            }
        } else { // cancel decay check on this leaves block
            state.setRawData((byte) (state.getRawData() & -0x09));
            state.update(true);
        }
    }

    private byte getBlockInMap(int x, int y, int z) {
        return blockMap[((x + 1) * (LEAVE_BLOCK_MAP_RANGE + 2) + z + 1)
                * (LEAVE_BLOCK_MAP_RANGE + 2) + y + 1];
    }

    private void setBlockInMap(byte val, int x, int y, int z) {
        blockMap[((x + 1) * (LEAVE_BLOCK_MAP_RANGE + 2) + z + 1)
                * (LEAVE_BLOCK_MAP_RANGE + 2) + y + 1] = val;
    }
}
