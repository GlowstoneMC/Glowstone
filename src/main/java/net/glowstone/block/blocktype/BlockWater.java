package net.glowstone.block.blocktype;

import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.constants.GlowBiomeClimate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.material.MaterialData;

public class BlockWater extends BlockLiquid {

    public BlockWater() {
        super(Material.WATER_BUCKET);
    }

    @Override
    public boolean isCollectible(GlowBlockState target) {
        return (target.getType() == Material.WATER || target.getType() == Material.WATER)
            &&
            (target.getRawData() == 0 || target.getRawData() == 8); // 8 for backwards compatibility
    }

    @Override
    public void updateBlock(GlowBlock block) {
        super.updateBlock(block);
        if (block.getLightFromBlocks() <= 11 - block.getMaterialValues().getLightOpacity()) {
            if (block.getRelative(BlockFace.UP).isEmpty() && hasNearSolidBlock(block)
                && GlowBiomeClimate.isCold(block)) {
                GlowBlockState state = block.getState();
                state.setType(Material.ICE);
                state.setData(new MaterialData(Material.ICE));
                BlockSpreadEvent spreadEvent = new BlockSpreadEvent(state.getBlock(), block, state);
                EventFactory.getInstance().callEvent(spreadEvent);
                if (!spreadEvent.isCancelled()) {
                    state.update(true);
                }
            }
        }
    }

    @Override
    public void onNearBlockChanged(GlowBlock block, BlockFace face, GlowBlock changedBlock,
        Material oldType, byte oldData, Material newType, byte newData) {
        // spread bubble column up or let it die out
        if (face != BlockFace.DOWN) {
            return;
        }
        switch (newType) {
            case BUBBLE_COLUMN: {
                block.setType(Material.BUBBLE_COLUMN);
                Bukkit.getLogger().info("spread bubble column at " + block.getLocation());
                // todo: set 'drag' blockdata to that of the block below
                break;
            }
            case WATER: {
                block.setType(Material.WATER);
                Bukkit.getLogger().info("destroy bubble column at " + block.getLocation());
                break;
            }
        }
    }

    private boolean hasNearSolidBlock(GlowBlock block) {
        // check there's at least a solid block around
        for (BlockFace face : SIDES) {
            if (block.getRelative(face).getType().isSolid()) {
                return true;
            }
        }
        return false;
    }
}
