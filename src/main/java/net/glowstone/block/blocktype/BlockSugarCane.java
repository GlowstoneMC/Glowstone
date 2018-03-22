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

public class BlockSugarCane extends BlockNeedsAttached {

    @Override
    public void onNearBlockChanged(GlowBlock block, BlockFace face, GlowBlock changedBlock,
        Material oldType, byte oldData, Material newType, byte newData) {
        updatePhysics(block);
    }

    @Override
    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        Block below = block.getRelative(BlockFace.DOWN);
        Material type = below.getType();
        switch (type) {
            case SUGAR_CANE_BLOCK:
                return true;
            case DIRT:
            case GRASS:
            case SAND:
                return isNearWater(below);
            default:
                return false;
        }
    }

    @Override
    public boolean canTickRandomly() {
        return true;
    }

    @Override
    public void updateBlock(GlowBlock block) {
        if (!canPlaceAt(block, BlockFace.DOWN)) {
            block.breakNaturally();
            return;
        }

        GlowBlock blockAbove = block.getRelative(BlockFace.UP);
        // check it's the highest block of sugar cane
        if (blockAbove.isEmpty()) {
            // check the current cane height
            Block blockBelow = block.getRelative(BlockFace.DOWN);
            int height = 1;
            while (blockBelow.getType() == Material.SUGAR_CANE_BLOCK) {
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
                    // grow the sugar cane on the above block
                    state.setRawData((byte) 0);
                    state.update(true);
                    state = blockAbove.getState();
                    state.setType(Material.SUGAR_CANE_BLOCK);
                    state.setRawData((byte) 0);
                    BlockGrowEvent growEvent = new BlockGrowEvent(blockAbove, state);
                    block.getEventFactory().callEvent(growEvent);
                    if (!growEvent.isCancelled()) {
                        state.update(true);
                    }
                    updatePhysics(blockAbove);
                }
            }
        }
    }

    private boolean isNearWater(Block block) {
        for (BlockFace face : SIDES) {
            switch (block.getRelative(face).getType()) {
                case WATER:
                case STATIONARY_WATER:
                    return true;
                default:
                    break;
            }
        }

        return false;
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock me, ItemStack tool) {
        // Overridden for sugar cane to remove data from the dropped item
        return Collections.unmodifiableList(Arrays.asList(new ItemStack(Material.SUGAR_CANE)));
    }
}
