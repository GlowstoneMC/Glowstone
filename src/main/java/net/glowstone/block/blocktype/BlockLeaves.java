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

public class BlockLeaves extends BlockType {

    private final byte[] blockMap = new byte[11 * 11 * 11];

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
        ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);
        state.setRawData((byte) (state.getRawData() | 0x04));
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        int data = block.getData() & 0x03; //ignore "non-decay" and "check-decay" data.

        if (tool != null && tool.getType() == Material.SHEARS) {
            return Collections.unmodifiableList(Arrays.asList(
                new ItemStack(block.getType(), 1, (short) data)
            ));
        }

        if (block.getType() == Material.LEAVES_2) {
            data += 4;
        }

        List<ItemStack> drops = new ArrayList<>();

        if (ThreadLocalRandom.current().nextFloat() < (block.getData() == 3 ? .025f
            : .05f)) { //jungle leaves drop with 2.5% chance, others drop with 5%
            drops.add(new ItemStack(Material.SAPLING, 1, (short) data));
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
                    if (b.getType() == Material.LEAVES || b.getType() == Material.LEAVES_2) {
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
        if ((state.getRawData() & 0x08) != 0
            && (state.getRawData() & 0x04) == 0) { // check decay is on and decay is on
            GlowWorld world = block.getWorld();

            // build a 9x9x9 box to map neighboring blocks
            for (int x = 0; x < 9; x++) {
                for (int z = 0; z < 9; z++) {
                    for (int y = 0; y < 9; y++) {
                        GlowBlock b = world
                            .getBlockAt(block.getLocation().add(x - 4, y - 4, z - 4));
                        byte val = 127;
                        if (b.getType() == Material.LOG || b.getType() == Material.LOG_2) {
                            val = 0;
                        } else if (b.getType() == Material.LEAVES
                            || b.getType() == Material.LEAVES_2) {
                            val = -1;
                        }
                        setBlockInMap(val, x, y, z);
                    }
                }
            }

            // browse the map in several pass to detect connected leaves:
            // leaf block that is 5 blocks away from log or without connection
            // to another connected leaves block will decay
            for (int i = 0; i < 4; i++) {
                for (int x = 0; x < 9; x++) {
                    for (int z = 0; z < 9; z++) {
                        for (int y = 0; y < 9; y++) {
                            if (getBlockInMap(x, y, z) == i) {
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
            }

            if (getBlockInMap(4, 4, 4) < 0) { // leaf decay
                LeavesDecayEvent decayEvent = new LeavesDecayEvent(block);
                EventFactory.callEvent(decayEvent);
                if (!decayEvent.isCancelled()) {
                    block.breakNaturally();
                }
            } else { // cancel decay check on this leaves block
                state.setRawData((byte) (state.getRawData() & -0x09));
                state.update(true);
            }
        }
    }

    private byte getBlockInMap(int x, int y, int z) {
        return blockMap[((x + 1) * 11 + z + 1) * 11 + y + 1];
    }

    private void setBlockInMap(byte val, int x, int y, int z) {
        blockMap[((x + 1) * 11 + z + 1) * 11 + y + 1] = val;
    }
}
