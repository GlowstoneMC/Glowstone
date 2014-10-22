package net.glowstone.block.blocktype;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockFadeEvent;

import net.glowstone.EventFactory;
import net.glowstone.GlowWorld;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;

public class BlockSoil extends BlockType {

    private Material[] possibleCrops = {Material.CROPS, Material.CARROT, Material.POTATO, Material.MELON_STEM, Material.PUMPKIN_STEM};

    @Override
    public boolean canTickRandomly() {
        return true;
    }

    @Override
    public void updateBlock(GlowBlock block) {
        if (isNearWater(block) || block.getWorld().hasStorm()) { // better to ensure that rain is falling on block later
            block.setData((byte) 7); // set this block as fully wet
        } else if (block.getData() > 0) {
            block.setData((byte) (block.getData() - 1)); // if this block is wet, it becomes less wet
        } else if (!Arrays.asList(possibleCrops).contains(block.getRelative(BlockFace.UP).getType())) {
            // turns block back to dirt if nothing is planted on
            final GlowBlockState state = block.getState();
            state.setType(Material.DIRT);
            state.setRawData((byte) 0);
            BlockFadeEvent fadeEvent = new BlockFadeEvent(block, state);
            EventFactory.callEvent(fadeEvent);
            if (!fadeEvent.isCancelled()) {
                state.update(true);
            }
        }
    }

    private boolean isNearWater(GlowBlock block) {
        // check around for some water blocks
        final GlowWorld world = block.getWorld();
        for (int x = block.getX() - 4; x <= block.getX() + 4; x++) {
            for (int z = block.getZ() - 4; z <= block.getZ() + 4; z++) {
                for (int y = block.getY(); y <= block.getY() + 1; y++) {
                    if (world.getBlockAt(x, y, z).getType() == Material.WATER) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
