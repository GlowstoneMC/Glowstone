package net.glowstone.block.blocktype;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.inventory.ItemStack;

public class BlockCactus extends BlockType {

    @Override
    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        Material below = block.getRelative(BlockFace.DOWN).getType();
        return (below == Material.CACTUS || below == Material.SAND) && !hasNearBlocks(block);
    }

    @Override
    public void onNearBlockChanged(GlowBlock block, BlockFace face, GlowBlock changedBlock,
        Material oldType, byte oldData, Material newType, byte newData) {
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
        // TODO: Drop entity if the block has near blocks
        GlowBlock blockAbove = block.getRelative(BlockFace.UP);
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
                    EventFactory.getInstance().callEvent(growEvent);
                    if (!growEvent.isCancelled()) {
                        state.update(true);
                    }
                    updatePhysics(blockAbove);
                }
            }
        }
    }

    private boolean hasNearBlocks(GlowBlock block) {
        for (BlockFace face : SIDES) {
            if (!canPlaceNear(block.getRelative(face).getType())) {
                return true;
            }
        }
        return false;
    }

    private boolean canPlaceNear(Material type) {
        // TODO: return true for non-buildable blocks
        switch (type) {
            case GRASS_BLOCK:
            case DIRT:
            case SAND:
            case GLASS:
            case STONE:
            case FURNACE:
            case BURNING_FURNACE:
            case STAINED_GLASS:
            case THIN_GLASS:
            case FENCE:
            case ACACIA_FENCE:
            case BIRCH_FENCE:
            case DARK_OAK_FENCE:
            case IRON_FENCE:
            case JUNGLE_FENCE:
            case NETHER_FENCE:
            case SPRUCE_FENCE:
            case ACACIA_FENCE_GATE:
            case BIRCH_FENCE_GATE:
            case DARK_OAK_FENCE_GATE:
            case SPRUCE_FENCE_GATE:
            case JUNGLE_FENCE_GATE:
            case FENCE_GATE:
            case ACACIA_DOOR:
            case BIRCH_DOOR:
            case DARK_OAK_DOOR:
            case IRON_DOOR:
            case JUNGLE_DOOR:
            case SPRUCE_DOOR:
            case WOODEN_DOOR:
            case TRAP_DOOR:
            case IRON_TRAPDOOR:
            case SPONGE:
            case COBBLESTONE:
            case MOSSY_COBBLESTONE:
                return false;
            default:
                return true;
        }
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock me, ItemStack tool) {
        // Overridden for cactus to remove data from the dropped item
        return Collections.unmodifiableList(Arrays.asList(new ItemStack(Material.CACTUS)));
    }
}
