package net.glowstone.block.blocktype;

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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class BlockVine extends BlockClimbable {

    private static final BlockFace[] FACES = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.DOWN, BlockFace.UP};
    private static final BlockFace[] HORIZONTAL_FACES = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};

    @Override
    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        switch (against)  {
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
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
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
    public boolean canAbsorb(GlowBlock block, BlockFace face, ItemStack holding) {
        return true;
    }

    @Override
    public boolean canOverride(GlowBlock block, BlockFace face, ItemStack holding) {
        return true;
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        if (tool != null && tool.getType() == Material.SHEARS)
            return Collections.singletonList(new ItemStack(Material.VINE));

        return BlockDropless.EMPTY_STACK;
    }

    @Override
    public boolean canTickRandomly() {
        return true;
    }

    @Override
    public void updateBlock(GlowBlock block) {
        if (random.nextInt(4) == 0) {
            final GlowBlockState state = block.getState();
            final MaterialData data = state.getData();
            if (data instanceof Vine) {
                final Vine vine = (Vine) data;
                final boolean hasNearVineBlocks = hasNearVineBlocks(block);
                final BlockFace face = FACES[random.nextInt(FACES.length)];
                if (block.getY() < 255 && face == BlockFace.UP && block.getRelative(face).isEmpty()) {
                    if (!hasNearVineBlocks) {
                        final Vine v = (Vine) data;
                        for (BlockFace f : HORIZONTAL_FACES) {
                            if (random.nextInt(2) == 0 || !block.getRelative(f).getRelative(face).getType().isSolid()) {
                                v.removeFromFace(f);
                            }
                        }
                        putVineOnHorizontalBlockFace(block.getRelative(face), v, block);
                    }
                } else if (Arrays.asList(HORIZONTAL_FACES).contains(face) && !vine.isOnFace(face)) {
                    if (!hasNearVineBlocks) {
                        final GlowBlock b = block.getRelative(face);
                        if (b.isEmpty()) {
                            final BlockFace fcw = getClockwiseFace(face);
                            final BlockFace fccw = getCounterClockwiseFace(face);
                            final GlowBlock bcw = b.getRelative(fcw);
                            final GlowBlock bccw = b.getRelative(fccw);
                            final boolean isOnCWFace = vine.isOnFace(fcw);
                            final boolean isOnCCWFace = vine.isOnFace(fccw);
                            if (isOnCWFace && bcw.getType().isSolid()) {
                                putVine(b, new Vine(fcw), block);
                            } else if (isOnCCWFace && bccw.getType().isSolid()) {
                                putVine(b, new Vine(fccw), block);
                            } else if (isOnCWFace && bcw.isEmpty() && block.getRelative(fcw).getType().isSolid()) {
                                putVine(bcw, new Vine(face.getOppositeFace()), block);
                            } else if (isOnCCWFace && bccw.isEmpty() && block.getRelative(fccw).getType().isSolid()) {
                                putVine(bccw, new Vine(face.getOppositeFace()), block);
                            } else if (b.getRelative(BlockFace.UP).getType().isSolid()) {
                                putVine(b, new Vine(), block);
                            }
                        } else if (b.getType().isOccluding()) {
                            vine.putOnFace(face);
                            putVine(block, vine, null);
                        }
                    }
                } else if (block.getY() > 1) {
                    final GlowBlock b = block.getRelative(BlockFace.DOWN);
                    final Vine v = (Vine) data;
                    if (b.getType() == Material.VINE || b.isEmpty()) {
                        for (BlockFace f : HORIZONTAL_FACES) {
                            if (random.nextInt(2) == 0) {
                                v.removeFromFace(f);
                            }
                        }
                        putVineOnHorizontalBlockFace(b, v, b.isEmpty() ? block : null);
                    }
                }
            } else {
                warnMaterialData(Vine.class, data);
            }
        }
    }

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

    private void putVine(GlowBlock block, Vine vine, GlowBlock fromBlock) {
        final GlowBlockState state = block.getState();
        state.setType(Material.VINE);
        state.setData(vine);
        if (fromBlock != null) {
            BlockSpreadEvent spreadEvent = new BlockSpreadEvent(block, fromBlock, state);
            EventFactory.callEvent(spreadEvent);
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

    private static boolean hasNearVineBlocks(GlowBlock block) {
        final GlowWorld world = block.getWorld();
        int vineCount = 0;
        for (int x = 0; x < 9; x++) {
            for (int z = 0; z < 9; z++) {
                for (int y = 0; y < 3; y++) {
                    if (world.getBlockAt(block.getLocation().add(x - 4, y - 1, z - 4)).getType() == Material.VINE) {
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
