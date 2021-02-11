package net.glowstone.block.blocktype;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;
import net.glowstone.EventFactory;
import net.glowstone.GlowWorld;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Vine;
import org.bukkit.util.Vector;

public class BlockVine extends BlockClimbable {

    // constants used in vine data
    private static final int VINE_NORTH = 0x4;
    private static final int VINE_EAST = 0x8;
    private static final int VINE_WEST = 0x2;
    private static final int VINE_SOUTH = 0x1;

    private static final BlockFace[] HORIZONTAL_FACES = {BlockFace.NORTH, BlockFace.SOUTH,
        BlockFace.EAST, BlockFace.WEST};

    private static BlockFace getClockwiseFace(BlockFace face) {
        switch (face) {
            case NORTH:
                return BlockFace.EAST;
            case SOUTH:
                return BlockFace.WEST;
            case EAST:
                return BlockFace.SOUTH;
            case WEST:
                return BlockFace.NORTH;
            default:
                return BlockFace.NORTH;
        }
    }

    private static BlockFace getCounterClockwiseFace(BlockFace face) {
        switch (face) {
            case NORTH:
                return BlockFace.WEST;
            case SOUTH:
                return BlockFace.EAST;
            case EAST:
                return BlockFace.NORTH;
            case WEST:
                return BlockFace.SOUTH;
            default:
                return BlockFace.NORTH;
        }
    }

    @Override
    public boolean canPlaceAt(GlowPlayer player, GlowBlock block, BlockFace against) {
        switch (against) {
            case NORTH:
            case SOUTH:
            case EAST:
            case WEST:
                return block.getRelative(against.getOppositeFace()).getType().isSolid();
            case UP:
                return block.getRelative(against).getType().isSolid();
            default:
                return false;
        }
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
        ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);

        MaterialData data = state.getData();
        if (data instanceof Vine) {
            if (face == BlockFace.DOWN || face == BlockFace.UP) {
                return;
            } else {
                ((Vine) data).putOnFace(face.getOppositeFace());
            }
            state.setData(data);
        } else {
            warnMaterialData(Vine.class, data);
        }
    }

    @Override
    public void onNearBlockChanged(GlowBlock block, BlockFace face, GlowBlock changedBlock,
                                   Material oldType, byte oldData, Material newType, byte newData) {
        updatePhysics(block);
    }

    @Override
    public void updatePhysicsAfterEvent(GlowBlock me) {
        super.updatePhysicsAfterEvent(me);

        byte data = me.getData();

        GlowBlock above = me.getRelative(BlockFace.UP);
        // above block is a vine and shares the same face
        boolean connectedAbove = above.getType() == Material.VINE && (above.getData() & data) != 0;
        // if not connected to a vine above we need to be attached to a solid block
        if (!connectedAbove) {
            if ((data & VINE_NORTH) != 0 && me.getRelative(BlockFace.NORTH).getType().isSolid()) {
                return;
            } else if ((data & VINE_EAST) != 0 && me.getRelative(BlockFace.EAST).getType().isSolid()) {
                return;
            } else if ((data & VINE_SOUTH) != 0 && me.getRelative(BlockFace.SOUTH).getType().isSolid()) {
                return;
            } else if ((data & VINE_WEST) != 0 && me.getRelative(BlockFace.WEST).getType().isSolid()) {
                return;
            }
            me.breakNaturally();
        }
    }

    @Override
    public boolean canAbsorb(GlowBlock block, BlockFace face, ItemStack holding) {
        return true;
    }

    @Override
    public boolean canOverride(GlowBlock block, BlockFace face, ItemStack holding) {
        return true;
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        if (tool != null && tool.getType() == Material.SHEARS) {
            return Arrays.asList(new ItemStack(Material.VINE));
        }

        return Collections.emptyList();
    }

    @Override
    public boolean canTickRandomly() {
        return true;
    }

    @Override
    public void updateBlock(GlowBlock block) {
        if (ThreadLocalRandom.current().nextInt(4) == 0) {
            GlowBlockState state = block.getState();
            MaterialData data = state.getData();
            if (!(data instanceof Vine)) {
                warnMaterialData(Vine.class, data);
                return;
            }
            Vine vine = (Vine) data;
            boolean hasNearVineBlocks = hasNearVineBlocks(block);
            BlockFace face = ADJACENT[ThreadLocalRandom.current().nextInt(ADJACENT.length)];
            if (block.getY() < 255 && face == BlockFace.UP && block.getRelative(face)
                .isEmpty()) {
                if (!hasNearVineBlocks) {
                    Vine v = (Vine) data;
                    for (BlockFace f : HORIZONTAL_FACES) {
                        if (ThreadLocalRandom.current().nextInt(2) == 0 || !block.getRelative(f)
                            .getRelative(face).getType().isSolid()) {
                            v.removeFromFace(f);
                        }
                    }
                    putVineOnHorizontalBlockFace(block.getRelative(face), v, block);
                }
            } else if (Arrays.asList(HORIZONTAL_FACES).contains(face) && !vine.isOnFace(face)) {
                if (!hasNearVineBlocks) {
                    GlowBlock b = block.getRelative(face);
                    if (b.isEmpty()) {
                        BlockFace clockwiseFace = getClockwiseFace(face);
                        BlockFace counterClockwiseFace = getCounterClockwiseFace(face);
                        GlowBlock clockwiseBlock = b.getRelative(clockwiseFace);
                        GlowBlock counterClockwiseBlock = b.getRelative(counterClockwiseFace);
                        boolean isOnCwFace = vine.isOnFace(clockwiseFace);
                        boolean isOnCcwFace = vine.isOnFace(counterClockwiseFace);
                        if (isOnCwFace && clockwiseBlock.getType().isSolid()) {
                            putVine(b, new Vine(clockwiseFace), block);
                        } else if (isOnCcwFace && counterClockwiseBlock.getType().isSolid()) {
                            putVine(b, new Vine(counterClockwiseFace), block);
                        } else if (isOnCwFace && clockwiseBlock.isEmpty() && block
                            .getRelative(clockwiseFace).getType().isSolid()) {
                            putVine(clockwiseBlock, new Vine(face.getOppositeFace()), block);
                        } else if (isOnCcwFace && counterClockwiseBlock.isEmpty() && block
                            .getRelative(counterClockwiseFace).getType().isSolid()) {
                            putVine(counterClockwiseBlock, new Vine(face.getOppositeFace()),
                                block);
                        } else if (b.getRelative(BlockFace.UP).getType().isSolid()) {
                            putVine(b, new Vine(), block);
                        }
                    } else if (b.getType().isOccluding()) {
                        vine.putOnFace(face);
                        putVine(block, vine, null);
                    }
                }
            } else if (block.getY() > 1) {
                GlowBlock b = block.getRelative(BlockFace.DOWN);
                Vine v = (Vine) data;
                if (b.getType() == Material.VINE || b.isEmpty()) {
                    for (BlockFace f : HORIZONTAL_FACES) {
                        if (ThreadLocalRandom.current().nextInt(2) == 0) {
                            v.removeFromFace(f);
                        }
                    }
                    putVineOnHorizontalBlockFace(b, v, b.isEmpty() ? block : null);
                }
            }
        }
    }

    private void putVine(GlowBlock block, Vine vine, GlowBlock fromBlock) {
        GlowBlockState state = block.getState();
        state.setType(Material.VINE);
        state.setData(vine);
        if (fromBlock != null) {
            BlockSpreadEvent spreadEvent = new BlockSpreadEvent(block, fromBlock, state);
            EventFactory.getInstance().callEvent(spreadEvent);
            if (!spreadEvent.isCancelled()) {
                state.update(true);
            }
        } else {
            state.update(true);
        }
    }

    private void putVineOnHorizontalBlockFace(GlowBlock block, Vine vine, GlowBlock fromBlock) {
        boolean isOnHorizontalFace = false;
        for (BlockFace f : HORIZONTAL_FACES) {
            if (vine.isOnFace(f)) {
                isOnHorizontalFace = true;
                break;
            }
        }
        if (isOnHorizontalFace) {
            putVine(block, vine, fromBlock);
        }
    }

    private boolean hasNearVineBlocks(GlowBlock block) {
        GlowWorld world = block.getWorld();
        int vineCount = 0;
        for (int x = 0; x < 9; x++) {
            for (int z = 0; z < 9; z++) {
                for (int y = 0; y < 3; y++) {
                    if (world.getBlockAt(block.getLocation().add(x - 4, y - 1, z - 4)).getType()
                        == Material.VINE) {
                        if (++vineCount >= 5) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
